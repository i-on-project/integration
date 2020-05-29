# Goal
This document aims to weigh in on different options for configuring the integration sub-system batch jobs. Examples of job configuration include the location of the source documents used, whom to notify about job outcome, and where to write the produced information.

Options that would require the application to be compiled in order to change the value associated with a configuration were left out. The application configurations should be provided at launch-time.

In the remainder of the document different alternatives that meet the criteria are discussed, and finally the research conclusion is stated.

# Alternatives

## Command Line switches

Each configuration could be passed with the `--` switch on application launch. For example, if we wanted to associate a key configuration with the value `X` we could do so by launch the app with:

`java -jar i-on-integration.jar --key.config=X`

The value `X` would be available at runtime using the annotation @Value("${key.config}").

- Despite being very simple, this approach is error-prone, as each new configuration would have to be added by hand in the command that launches I-On Integration.
- It would lead to a large and difficult to read java command.
- Also, different jobs could not have the same key.
  
## Spring configuration application.properties

Apart from the default [properties](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#mail-properties) available at runtime in a spring application, we can define our own inside the application.properties file. This file should be present in a /config subdirectory of the current directory or in the current directory, in the classpath root or in the classpath /config package.

- The main issue with this option is that the configuration file will grow in size as new batch jobs are added. In the long run, that can cause bloating.
- Launching different instances of the same job would require the properties file to be updated or having a folder per job instance with an application.properties file. To specify the location of the properties file -Dspring.config.location=/path/to/application.properties should be used. Example:
`java -jar configurations-0.0.1-SNAPSHOT.jar --spring.config.location=./confs/job1/application.properties`

## Additional .properties/.yml files passed in --spring.config.location or spring.config.additional-location

What is done for different instances of the same job (having different application.properties files in different folders) can be done to segregate properties across jobs. In practice, we could define a properties file per job instance.
- Fairly easy to maintain properties segregation, having one config file per job. This would mean that the class where it was used would have to be annotated with @PropertySource if we wanted to change the name of the file to a more meaningful name.
- The number of files could grow very fast.

## External database

Parameterizing a job would require having an external unique identifier for a job instance.
If we used a relational db, the read times would be larger than using config files or command line arguments or job parameters.
If we used a non-relational db, we would have to configure and maintain it.

## Passing custom configuration file to be parsed by a custom module

Requires more development time, which is not available at the moment. There is no guarantee that the final result is better than using job parameters or the spring application.properties.

## Using job parameters
Spring Batch provides a way to pass external configuration to a job through the command line. It is similar to command line switches, but with no --.

Example:
`java -jar configurations-0.0.1-SNAPSHOT.jar key=value key2=value2`

Parameter type is inferred. As parameters are stored in column of a relational db, the supported types are Date, Double, Long and String, as seen [here](https://docs.spring.io/spring-batch/docs/4.2.x/api/org/springframework/batch/core/JobParametersBuilder.html).

The only drawback of this, as with command line switches, the java command can become very large if the number of parameters grows.

# Conclusion

From what was gathered, the strongest option for this task is using a .properties/.yml file per job.

Using an external non-relational database would provide the most flexibility but as it is not otherwise needed, it represents administration overhead, which we cannot absorb at the moment.
Having a custom configuration file would require development of a module for parsing it. Having a .properties file per job acchieves the same result and does not require additional development time.
Despite being advised on the standard references for Spring Batch, the use Job Parameters raises the same concern as command line switches, i.e. the launch command of the app would be very lengthy. On the other hand, using a .properties file does not have this inconvenient.
