import sbt._
import Keys._

object ProguardCache extends Plugin {
  object SbtJRubySettings {
    lazy val settings = proguardCacheSettings
  }

  val proguardCacheBuild = TaskKey[Unit]("proguard-cache-build", "build the jar")

  val proguardCacheBase = SettingKey[File]("proguard-cache-base", "path to the directory containing the proguard cache plugin source")
  val proguardCacheRubyLib = SettingKey[File]("proguard-cache-ruby-lib", "path to the directory containing the jruby library")
  val proguardCacheStorage = SettingKey[File]("proguard-cache-storage", "path to the directory to store dependency files")
  val proguardCacheConfigFile = SettingKey[File]("proguard-cache-config-file", "path to the proguard configuration file")

  lazy val proguardCacheSettings = Seq(
    proguardCacheBase := file("/must/specify/this"),
    proguardCacheRubyLib <<= proguardCacheBase(_ / "src" / "main" / "jruby"),
    proguardCacheBuild <<= buildProguardCachedJar)

  lazy val buildProguardCachedJar = (proguardCacheBase, proguardCacheRubyLib, sourceDirectories in Compile, classDirectory in Compile, proguardCacheStorage, proguardCacheConfigFile) map {
    (pcb, pcrbl, sd, cd, cacheDestination, proguardCacheConfigFile) =>
      println("building?!" + sd + cd + cacheDestination)

      val oldContextClassLoader = Thread.currentThread().getContextClassLoader
      Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader())

      val pc = new com.restphone.ProguardCache
      println("sgotasf")
      pc.build_dependency_files_and_final_jar(List(cacheDestination), proguardCacheConfigFile, "/tmp/out.jar", "target/proguard_cache", None)

      Thread.currentThread().setContextClassLoader(oldContextClassLoader)
      ()
  }
}
//
//  #  ProguardCache.new.build_dependency_files_and_final_jar %w(target/scala-2.9.1), "proguard_config/proguard_android_scala.config.unix", "/tmp/out.jar", "target/proguard_cache"
//  def build_dependency_files_and_final_jar input_directories, proguard_config_file, destination_jar, cache_dir = nil, cache_jar_pattern = nil
