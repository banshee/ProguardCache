import sbt._
import Keys._

object ProguardCache extends Plugin {
  object SbtJRubySettings {
    lazy val settings = proguardCacheSettings
  }

  val jrubySetup = TaskKey[Unit]("jruby-setup", "initial JRuby setup, install gems, etc")

  val proguardCacheRubyLib = SettingKey[File]("proguard-cache-ruby-lib", "path to the directory containing the jruby library")

  lazy val proguardCacheSettings = Seq(
    proguardCacheRubyLib := file("/must/specify/this"))
}
