import sbt._
object PluginDef extends Build {
        override lazy val projects = Seq(root)
        lazy val root = Project("plugins", file(".")) dependsOn( webPlugin )
        lazy val webPlugin = file("/Users/james/workspace/ProguardCache")
}
