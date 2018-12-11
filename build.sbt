name := "binary-file-finder"
description := """A Scala CLI App to find binary files."""
organization := "nz.co.bottech"
organizationName := "BotTech"
homepage := Some(url("https://github.com/BotTech/binary-file-finder"))
licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

scalaVersion := "2.12.8"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies += "com.lihaoyi" %% "utest" % "0.6.6" % Test
testFrameworks += new TestFramework("utest.runner.Framework")

publishMavenStyle := true

bintrayOrganization := Some("bottech")
bintrayPackageLabels := Seq("scala", "cli")

ghreleaseRepoOrg := organizationName.value

publishLocal / gpgSignArtifacts := false
gpgPassphrase := Option(System.getenv("PGP_PASS"))
gpgKeyFile := file("travis") / "key.asc"
gpgKeyFingerprint := "TODO!"
