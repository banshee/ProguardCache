import sbt._
object PluginDef extends Build {
        override lazy val projects = Seq(root)
        lazy val root = Project("plugins", file(".")) dependsOn( webPlugin )
	// Change /Users/james/workspace/ProguardCache to the location
	// where you checked out git://github.com/banshee/ProguardCache.git
        lazy val webPlugin = file("/Users/james/workspace/ProguardCache")
}
