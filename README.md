# Protempa
[Atlanta Clinical and Translational Science Institute (ACTSI)](http://www.actsi.org), [Emory University](http://www.emory.edu), Atlanta, GA

## What does it do?
Protempa is a software library with a modular architecture. The following description provides a generic overview of PROTEMPA's structure and the subsequent section describes our implementation in Java. PROTEMPA has four modules, shown in Figure 1, that provide 1) a framework for defining temporal abstraction primitives and processing time-stamped data with those primitives (the Algorithm Source), 2) a framework for specifying algorithm parameters and interval relationships that define abstractions of interest (the Knowledge Source), 3) a connection to an existing data store (the Data Source), and 4) a data processing environment for managing the abstraction-finding routines (the Abstraction Finder). The first three modules have back ends that implement environment- or application-specific features.

## Version history
### Version 1.0
* Initial version, used for caching in Eureka! Clinical.

## Build requirements
* [Oracle Java JDK 8](http://www.oracle.com/technetwork/java/javase/overview/index.html)
* [Maven 3.2.5 or greater](https://maven.apache.org)

## Runtime requirements
* [Oracle Java JRE 8](http://www.oracle.com/technetwork/java/javase/overview/index.html)

## Building it
The project uses the maven build tool. Typically, you build it by invoking `mvn clean install` at the command line. For simple file changes, not additions or deletions, you can usually use `mvn install`. See https://github.com/eurekaclinical/dev-wiki/wiki/Building-Eureka!-Clinical-projects for more details.

## Maven dependency
```
<dependency>
    <groupId>org.eurekaclinical</groupId>
    <artifactId>datastore</artifactId>
    <version>version</version>
</dependency>
```

## Developer documentation
* [Javadoc for latest development release](http://javadoc.io/doc/org.eurekaclinical/datastore) [![Javadocs](http://javadoc.io/badge/org.eurekaclinical/datastore.svg)](http://javadoc.io/doc/org.eurekaclinical/datastore)

## Getting help
Feel free to contact us at help@eurekaclinical.org.
