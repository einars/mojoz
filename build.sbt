name := "mojoz"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature")

retrieveManaged := true

resolvers ++= Seq(
  "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

libraryDependencies ++= Seq(
  "org.yaml" % "snakeyaml" % "1.13",
  // test
  "com.typesafe" % "config" % "1.2.0" % "it",
  "org.tresql" %% "tresql" % "5.3" % "it,test",
  "org.scalatest" % "scalatest_2.10" % "2.0.M8" % "it,test"
)

scalacOptions in (Compile, doc) <++= (baseDirectory in
 LocalProject("mojoz")).map {
   bd => Seq("-sourcepath", bd.getAbsolutePath,
             "-doc-source-url", "https://github.com/guntiso/mojoz/blob/develop€{FILE_PATH}.scala")
 }
