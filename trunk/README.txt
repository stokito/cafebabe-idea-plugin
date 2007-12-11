    How to prepare artifacts

Run these commands to prepare binaries and sources for web site:

>mvn clean
>mvn assembly:assembly

and optionally:

>ant add.version

Artifacts will be located in target directory.


How to build updated help.jar

>ant help.jar
