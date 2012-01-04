import sbt._
import Keys._

object ProguardCache extends Plugin {
  object Settings {
    lazy val settings = proguardCacheSettings
  }

  val proguardCacheBuild = TaskKey[Unit]("proguard-cache-build", "build the jar")

  val proguardCacheBase = SettingKey[File]("proguard-cache-base", "path to the directory containing the proguard cache plugin source")
  val proguardCacheRubyLib = SettingKey[File]("proguard-cache-ruby-lib", "path to the directory containing the jruby library")
  val proguardCacheStorage = SettingKey[File]("proguard-cache-storage", "path to the directory to store dependency files")
  val proguardCacheConfigFile = SettingKey[File]("proguard-cache-config-file", "path to the proguard configuration file")
  val proguardCacheFinalJar = SettingKey[File]("proguard-cache-final-jar", "path to the final jarfile")

  lazy val proguardCacheSettings = Seq(
    proguardCacheBase := file("/must/specify/this"),
    proguardCacheRubyLib <<= proguardCacheBase(_ / "src" / "main" / "jruby"),
    proguardCacheBuild <<= buildProguardCachedJar,
    proguardCacheStorage <<= cacheDirectory(_ / "proguard_cache"))

  lazy val buildProguardCachedJar = (
    proguardCacheBase,
    proguardCacheRubyLib,
    classDirectory in Compile,
    proguardCacheStorage,
    proguardCacheConfigFile,
    proguardCacheFinalJar,
    managedClasspath in Compile) map {
      (pcb, proguardCacheRubyLibValue, classDirectoryValue, proguardCacheStorageValue, proguardCacheConfigFileValue, proguardCacheFinalJarValue, managedClasspathValue) =>
        val oldContextClassLoader = Thread.currentThread().getContextClassLoader
        
        // SBT doesn't set the thread context class loader.  JRuby depends on it, so set it and then restore to the old value
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader())
        
        ((managedClasspathValue map {_.data.getParent}) ++ List(proguardCacheRubyLibValue)) map {_.toString} map com.restphone.JrubyEnvironmentSetup.addToLoadPath
        
        val pc = new com.restphone.ProguardCacheRuby
        pc.build_dependency_files_and_final_jar(
          List(classDirectoryValue.toString).toArray,
          proguardCacheConfigFileValue.toString,
          proguardCacheFinalJarValue.toString,
          proguardCacheStorageValue.toString,
          proguardCacheStorageValue.toString + "/scala-library.CKSUM.jar")
        
          Thread.currentThread().setContextClassLoader(oldContextClassLoader)
        ()
    }
}
