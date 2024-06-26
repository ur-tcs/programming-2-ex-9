name := "futures"
scalaVersion := "3.3.1"
libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.9.1"
libraryDependencies += "com.softwaremill.sttp.client3" %% "upickle" % "3.9.1"
libraryDependencies += "org.scalameta" %% "munit" % "1.0.0-M10" % Test
scalacOptions ++= Seq("-source:future", "-deprecation", "-Xfatal-warnings")
// run / fork := true
// run / connectInput := true
Test / parallelExecution := false
Test / testForkedParallel := false
