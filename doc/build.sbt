import AndroidKeys._

name := "pcache"

Seq(ProguardCache.Settings.settings: _*)

proguardCacheBase := file("/Users/james/workspace/ProguardCache")

proguardCacheConfigFile := file("proguard.conf")

proguardCacheFinalJar := file("target/scala_lib_after_proguard.jar")

proguardInJars in Android += file("target/scala_lib_after_proguard.jar")

useProguard in Android := false

logLevel := Level.Debug
