# Goal
This document aims to weigh in on different options for configuring the integration sub-system batch jobs. Examples of job configuration include the location of the source documents used, whom to notify about job outcome, and where to write the produced information.

In the remainder of the document different alternatives that meet the criteria are discussed, and finally the research conclusion is stated.

# Alternatives

## 1 Command Line switches

Each configuration could be passed with the `--` switch on application launch. For example, if we wanted to associate a key configuration with the value `X` we could do so by launch the app with:

`java -jar i-on-integration.jar --key.config=X`

The value `X` would be available at runtime using the annotation @Value("${key.config}").

- Despite being very simple, this approach is error-prone, as each new configuration would have to be added by hand in the command that launches I-On Integration.
- It would lead to a large and difficult to read java command.
- Also, different jobs could not have the same key.
  
## 2 Spring configuration application.properties

Apart from the default [properties](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#mail-properties) available at runtime in a spring application, we can define our own inside the application.properties file. This file should be present in a /config subdirectory of the current directory or in the current directory, in the classpath root or in the classpath /config package.

- The main issue with this option is that the configuration file will grow in size as new batch jobs are added. In the long run, that can cause bloating.
- Launching different instances of the same job would require the properties file to be updated or having a folder per job instance with an application.properties file. To specify the location of the properties file -Dspring.config.location=/path/to/application.properties should be used. Example:
`java -jar configurations-0.0.1-SNAPSHOT.jar --spring.config.location=./confs/job1/application.properties`

## 3 Additional .properties/.yml files

A properties file per job can be defined. In the cases where the job is defined per course (e.g. the isel timetable job), it is defined a file per course.
- Fairly easy to maintain properties segregation, having one config file per job.
- The number of files could grow very fast.

Job configuration files are included in the project's image, in .../resources/config/{batch-name}/{school-name}/.

The configuration file name can be chosen freely, but for reasons of organization it should be named {school-name}-{course-name}.properties(e.g. isel-leic.properties). There is no need to include the job name in the properties file name because the path of the file already identifies the school.

On application launch, the jobs that are to be run are specified, as well as the directories where to look for configuration files. For example, if the timetable job will run for isel and estsl, the method runJob will be executed with the job name and the configuration Path.(e.g. runJob("ISEL Timetable Batch Job","src/main/resources/config/timetable/isel"), and runJob("ISEL Timetable Batch Job","src/main/resources/config/timetable/estsl"))

This method will list all the files in the directory. Then, for each file, it will inject its properties on a jobparameters object. Finally, it will start a job instance with the added parameters, including a timestamp in order to be able to run the same job with the same parameters more than once.

As said, configurations for a job are passed as strings to job parameters on startup, but before being used they are coerced from the string type whenever possible, to ensure the correctness of the configurations.

## 4 External database

Parameterizing a job would require having an external unique identifier for a job instance.
If we used a relational db, the read times would be larger than using config files or command line arguments or job parameters.
If we used a non-relational db, we would have to configure and maintain it.

## 5 Passing custom configuration file to be parsed by a custom module

Requires more development time, which is not available at the moment. There is no guarantee that the final result is better than using job parameters or the spring application.properties.

## 6 Using job parameters
Spring Batch provides a way to pass external configuration to a job through the command line. It is similar to command line switches, but with no --.

Example:
`java -jar configurations-0.0.1-SNAPSHOT.jar key=value key2=value2`

Parameter type is inferred. As parameters are stored in column of a relational db, the supported types are Date, Double, Long and String, as seen [here](https://docs.spring.io/spring-batch/docs/4.2.x/api/org/springframework/batch/core/JobParametersBuilder.html).

The only drawback of this, as with command line switches, the java command can become very large if the number of parameters grows.

# Conclusion

After considering the different options we chose to use a combination of options 3 and 6.

Using an external non-relational database would provide the most flexibility but as it is not otherwise needed, it represents administration overhead, which we cannot absorb at the moment.

Having a custom configuration file would require development of a module for parsing it. Having a .properties file per job acchieves the same result and does not require additional development time.

Despite being advised on the standard references for Spring Batch, the use Job Parameters raises the same concern as command line switches, i.e. the launch command of the app would be very lengthy. On the other hand, using a .properties file does not have this inconvenient.

As we needed to have the same application running multiple instances of the same job in one go, and we also wanted the convenience of changing configuration alone when adding new instances, we ended up going with a hybrid solution between having properties files and using job parameters. Job parameters are used when launching a batch job instance, but they are not passed on the command line. Instead, they are built from properties contained in a file, which needs to be in a designated directory for it to be identified.
