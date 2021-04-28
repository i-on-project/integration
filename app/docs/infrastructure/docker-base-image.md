# The alternatives for Java 11 base docker image

Java 11 is an [LTS version](https://www.oracle.com/java/technologies/java-se-support-roadmap.html). But as Oracle only releases patches for the most recent versions and it has a 6-month release interval for major Java versions, [LTS versions are not eligible for Oracle support, which is effectivelly done by other companies such as Red Hat, Azure (Microsoft), etc](https://github.com/docker-library/official-images/pull/5710).

Oracle releases binaries for what are called OpenJDK distributions. Then, maintainers, spearheaded by RedHat, release patches on the AdoptOpenJDK stream.

This is true for Java versions. As for Docker images, despite images being associated with the same designations (OpenJDK and AdoptOpenJDK), there is a slight difference in who publishes them. OpenJDK docker images are not released by Oracle, but use OpenJDK java distributions to assemble docker images. These are entirely [crowd-sourced](https://github.com/docker-library/openjdk).

As for image size, images of jre >= 9 take up more storage than previous versions. Addition of a [modular architecture](https://www.oracle.com/corporate/features/understanding-java-9-modules.html) in Java 9 – project jigsaw – requires the jre to contain a `modules` file that lists all the modules that are shipped with the jre, contributing to larger jre images than in previous versions. Contrary to what happens with java 8, no linux alpine image exists currently for java 11.

The OpenJDK docker hub publishes [slim](https://github.com/docker-slim/docker-slim) images which are minified versions of its other images.

AdoptOpenJDK also publishes lightweight images based on Ubuntu 18.04.4 LTS (Bionic Beaver), which use the Hotspot implementation of the JVM.

Image size for slim and bionic are presented in the table below:

| Image                                     | Size      |
|-------------------------------------------|-----------|
| openjdk:11.0.7-jre-slim                   | 69,2Mb    |
| openjdk:11-jdk-slim                       | 216,32Mb  |
| adoptopenjdk:11.0.7_10-jre-hotspot-bionic | 79,81 Mb  |
| adoptopenjdk:11.0.7_10-jdk-hotspot-bionic | 204,03 Mb |


# Conclusion
AdoptOpenJDK provides patches and its images are equivalent in size to OpenJDK. So AdoptOpenJDK is a better alternative as a base image for the project.
