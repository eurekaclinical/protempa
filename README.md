# Protempa
[Atlanta Clinical and Translational Science Institute (ACTSI)](http://www.actsi.org), [Emory University](http://www.emory.edu), Atlanta, GA

## What does it do?
Protempa identifies temporal sequences in clinical data. It supports retrieving patient populations containing sequences of interest from clinical datasets and databases in support of clinical research, outcomes studies and quality improvement. It provides for portability across data sources, and the creation of libraries of temporal sequence definitions and time series data processing algorithms.

### Protempa architecture
Protempa is a software framework with a modular architecture. It has four modules, shown in the diagram below, that provide for 1)  defining time series data processing algorithms such as for finding states and trends (the *Algorithm Source*), 2) specifying frequency, sequence and overlap temporal patterns of interest (the *Knowledge Source*), 3) a connection to an existing data store (the *Data Source*), and 4) a data processing environment for managing the sequence-finding routines (the *Abstraction Finder*). The first three modules have back ends that implement environment- or application-specific features. Additinally, the framework provides a fifth module, not shown, the *Destination*, which processes the data and temporal sequences that are retrieved and identified.  Destinations can be plugged into Protempa that process its output in various ways.

![Protempa architecture](https://github.com/eurekaclinical/dev-wiki/blob/master/images/Protempa%20architecture.png)

### Methods
Protempa implements an extension of the [knowledge based temporal abstraction method](https://pdfs.semanticscholar.org/034c/09d382143cc392071ff2d47d2e95438d0bb4.pdf), which is a data summarization method in which an ontology is used to describe interpretations of the data of interest, with an emphasis on interpretations of time intervals containing frequency, sequence and overlap temporal patterns in the data. For portability across data sets, the descriptions use standard terminologies to describe the source data. The temporal abstraction process translates the ontology's descriptions into business rules that translate source data into a standard data model, compute the temporal patterns described in the ontology, and output the standardized data plus intervals representing the temporal patterns that were found. The diagram below depicts a collection of clinical observations and events, and time intervals representing a summary of its contents.

![Temporal abstraction example](https://github.com/eurekaclinical/dev-wiki/blob/master/images/BP%20figure.png)

In the clinical domain, temporal abstraction can be used for automated chart abstraction for large volumes of patient records (tens of millions), assuming that the the temporal patterns of interest can be described ahead of time in an ontology or as rules.

### For more information
See our [publication about Protempa](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC1975802/) for more information.

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
Protempa consists of a number of modules:

### protempa-framework
Provides 
```
<dependency>
    <groupId>org.eurekaclinical</groupId>
    <artifactId>protempa-framework</artifactId>
    <version>version</version>
</dependency>
```

## Example

To run Protempa, write code like the following:

```
import org.protempa.SourceFactory;
import org.protempa.backend.Configurations;
import org.protempa.bconfigs.ini4j.INIConfigurations;
import org.protempa.Protempa;
import org.protempa.dest.Destination;
import org.protempa.dest.map.MapDestination;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.Query;

// An implementation of org.protempa.backend.Configurations provides the backends to use.
Configurations backends = new INIConfigurations(new File("src/test/resources"));
SourceFactory sourceFactory = new SourceFactory(backends.load("protempa-config.ini"));

// Use try-with-resources to ensure resources are cleaned up.
try (Protempa protempa = Protempa.newInstance(sourceFactory)) {
    DefaultQueryBuilder q = new DefaultQueryBuilder();
    q.setName("My test query");
    q.setPropositionIds(new String[]{"ICD9:Diagnoses", "ICD9:Procedures", "LAB:LabTest", "Encounter", "MED:medications", "VitalSign",     
        "PatientDetails"}); // an array of the concept ids of the data to retrieve and/or temporal patterns to compute
    Query query = protempa.buildQuery(q);

    // An implementation of org.protempa.dest.Destination processes output from the temporal abstraction process.
    Destination dest = new MapDestination(); 
    protempa.execute(query, dest);
}
```

The `protempa-config.ini` file is an INI configuration file that specifies a data source backend, a knowledge source backend, and an algorithm source backend, for example:
```
# An implementation of org.protempa.backend.dsb.DataSourceBackend provides the data.
# Implementations of this interface are plugged into Protempa using the java.util.ServiceLoader mechanism.
[edu.emory.cci.aiw.cvrg.eureka.etl.dsb.EurekaDataSourceBackend]
dataSourceBackendId=Spreadsheet # any setters in the implementation that have the @BackendProperty annotation.
databaseName = spreadsheet
sampleUrl = ../docs/sample.xlsx

# An implementation of org.protempa.backend.dsb.KnowledgeSourceBackend provides the temporal sequence definitions.
# Implementations of this interface are plugged into Protempa using the java.util.ServiceLoader mechanism.
[edu.emory.cci.aiw.i2b2etl.ksb.I2b2KnowledgeSourceBackend]
databaseAPI = DATASOURCE # any setters in the implementation that have the @BackendProperty annotation.
databaseId = java:/comp/env/jdbc/I2b2KS
targetTable = EUREKAPHENOTYPEONTOLOGY

# An implementation of org.protempa.backend.asb.AlgorithmSourceBackend provides time series processing algorithms.
# Implementations of this interface are plugged into Protempa using the java.util.ServiceLoader mechanism.
# In general, use the built-in JavaAlgorithmBackend.
[org.protempa.backend.asb.java.JavaAlgorithmBackend]
```
## Developer documentation
* [Javadoc for latest development release](http://javadoc.io/doc/org.eurekaclinical/protempa-framework) [![Javadocs](http://javadoc.io/badge/org.eurekaclinical/protempa-framework.svg)](http://javadoc.io/doc/org.eurekaclinical/protempa-framework)

## Getting help
Feel free to contact us at help@eurekaclinical.org.
