    How to prepare artifacts

Run these commands to prepare binaries and sources for web site:

>mvn clean

>mvn dependency:copy-dependencies

>mvn assembly:assembly

and optionally:

>ant add.version

Artifacts will be located in target directory.