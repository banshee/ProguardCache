require 'rake'
require 'asm_support'
require 'asm_support/asm_visitor_harness'
require 'asm_support/dependency_signature_visitor'
require 'digest/sha2'
require 'proguardrunner'
require 'rake'

def proguard_output pattern, checksum
  pattern.sub("CKSUM", checksum)
end

def proto_to_class files
  files.pathmap("%X.class").sub("proguard_depend/", "bin/")
end

def class_to_proto files
  files.pathmap("%X.proguard_depend").sub("bin/", "proguard_depend/")
end

def unique_lines_in_files_as_string files
  (unique_lines_in_files files).join("\n")
end

def unique_lines_in_files files
  result = files.map {|f| IO.read(f).split(/[\n\r]+/)}.flatten.sort.uniq
  result.select {|x| x =~ %r(scala/)}
end

def checksum_of_lines_in_files files
  file_contents = (unique_lines_in_files_as_string files)
  Digest::SHA512.hexdigest file_contents
end

rule(/\.proguard_depend$/ => [
  proc {|task_name| proto_to_class task_name }
]) do |p|
  corresponding_classfile = proto_to_class p.name
  dependencies = AsmSupport::AsmVisitorHarness.build_for_filename(AsmSupport::DependencySignatureVisitor, corresponding_classfile)
  File.open(p.name, "w") do |f|
    f.write dependencies.values.first.keys.sort.uniq.join("\n")
  end
end

classfiles = FileList[%w(bin/**/*.class)].sort
proguard_dependency_files = class_to_proto classfiles

(proguard_dependency_files).each do |d|
  matching_directory = (d.pathmap "%d")
  file d => matching_directory
  directory matching_directory
  file d => (proto_to_class d)
end

desc "Build a proguarded scala library.  Arguments are:
proguard_file: The proguard config file
destination_jar: The final, proguarded jar file
cache_jar_pattern: The file name of the cached jars
cache_dir: Where the cached jars are stored

Example: jruby -S rake -T -v proguard[proguard_android_scala.config,proguard_cache/scala-proguard.jar]
"
task :proguard, [:proguard_file, :destination_jar, :cache_jar_pattern, :cache_dir] => :output do |t, args|
  args.with_defaults :proguard_file => 'proguard_android_scala.config', :destination_jar => "proguard_cache/scala-library-proguarded.jar", :cache_jar_pattern => "proguard_cache/scala-library.CKSUM.jar", :cache_dir => "proguard_cache"
  mkdir_p args.cache_dir if !File.exists? args.cache_dir
  dependency_checksum = checksum_of_lines_in_files(proguard_dependency_files + [args[:proguard_file]])
  proguard_destination_file = proguard_output args[:cache_jar_pattern], dependency_checksum
  if !File.exists? proguard_destination_file
    puts "proguard destination file is " + proguard_destination_file
    ProguardRunner.execute_proguard(:config_file => args.proguard_file, :cksum => ".#{dependency_checksum}")
    copy_file proguard_destination_file, args.destination_jar
    puts "Copied cache file #{proguard_destination_file} to destination #{args.destination_jar}"
  end

  contents = unique_lines_in_files_as_string proguard_dependency_files
  File.open "#{args.cache_dir}/dependency_lines." + dependency_checksum, "w" do |f|
    f.write contents
  end
end

file :output => proguard_dependency_files
task :proguard => :output
task :default => [:proguard]
