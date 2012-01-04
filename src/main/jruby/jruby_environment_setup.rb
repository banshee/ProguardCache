require 'java'

java_package 'com.restphone'

class JrubyEnvironmentSetup
  java_signature 'void addToLoadPath(String file)'
  def self.add_to_load_path file
    $LOAD_PATH << file
  end
end

