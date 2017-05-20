# Protempa
[Atlanta Clinical and Translational Science Institute (ACTSI)](http://www.actsi.org), [Emory University](http://www.emory.edu), Atlanta, GA

## What does it do?
Protempa identifies temporal sequences in clinical data. It supports retrieving patient populations containing sequences of interest from clinical datasets and databases in support of clinical research, outcomes studies and quality improvement. It provides for portability across data sources, and the creation of libraries of temporal sequence definitions and time series data processing algorithms.

### Protempa architecture
Protempa is a software framework with a modular architecture. It has four modules, shown in the diagram below, that provide for 1)  defining temporal abstraction primitives and processing time-stamped data with those primitives (the Algorithm Source), 2) specifying algorithm parameters and interval relationships that define sequences of interest (the Knowledge Source), 3) a connection to an existing data store (the Data Source), and 4) a data processing environment for managing the sequence-finding routines (the Abstraction Finder). The first three modules have back ends that implement environment- or application-specific features.

![Protempa architecture](https://github.com/eurekaclinical/dev-wiki/blob/master/images/Protempa%20architecture.png)

### Methods
Protempa implements an extension of the knowledge based temporal abstraction method, which is a data summarization method in which an ontology is used to describe interpretations of the data of interest, with an emphasis on interpretations of time intervals containing frequency, sequence and overlap temporal patterns in the data. For portability across data sets, the descriptions use standard terminologies to describe the source data. The temporal abstraction process translates the ontology's descriptions into business rules that translate source data into a standard data model, compute the temporal patterns described in the ontology, and output the standardized data plus intervals representing the temporal patterns that were found. The diagram below depicts a collection of clinical observations and events, and time intervals representing a summary of its contents.

![Temporal abstraction example](https://github.com/eurekaclinical/dev-wiki/blob/master/images/BP%20figure.png)

In the clinical domain, temporal abstraction can be used for automated chart abstraction for large volumes of patient records (tens of millions), assuming that the the temporal patterns of interest can be described ahead of time in an ontology or as rules.

## Version 4.0 development series
Latest release: [![Latest release](https://maven-badges.herokuapp.com/maven-central/org.eurekaclinical/protempa/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.eurekaclinical/protempa)

## Version history
### Version 3.5

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
    <artifactId>protempa</artifactId>
    <version>version</version>
</dependency>
```

## Developer documentation
* [Javadoc for latest development release](http://javadoc.io/doc/org.eurekaclinical/protempa) [![Javadocs](http://javadoc.io/badge/org.eurekaclinical/protempa.svg)](http://javadoc.io/doc/org.eurekaclinical/protempa)

## Getting help
Feel free to contact us at help@eurekaclinical.org.
