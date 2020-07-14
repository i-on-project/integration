[![ionproject.org](https://raw.githubusercontent.com/i-on-project/integration/master/img/i-on_logo.png)](https://www.ionproject.org)

[![License](https://img.shields.io/github/license/i-on-project/integration)](https://github.com/i-on-project/integration/blob/master/LICENSE)
[![GitHub build](https://img.shields.io/github/workflow/status/i-on-project/integration/I-On%20Integration%20Staging)](https://github.com/i-on-project/integration/actions?query=branch%3Amaster)
[![GitHub commits](https://img.shields.io/github/last-commit/i-on-project/integration)](https://github.com/i-on-project/integration/commits/master)
[![GitHub pull-requests](https://img.shields.io/github/issues-pr/i-on-project/integration)](https://github.com/i-on-project/integration/pull/)
[![GitHub issues](https://img.shields.io/github/issues/i-on-project/integration)](https://github.com/i-on-project/integration/issues/)

[![GitHub contributors](https://img.shields.io/github/contributors/i-on-project/integration)](https://github.com/i-on-project/integration/graphs/contributors/)

I-On Integration has the responsibility of collecting relevant academic information from external sources and uploading it to I-On Core, which is the I-On system central repository.

[I-On](https://github.com/i-on-project) is an academic information aggregation and distribution system, from which I-On Integration is part of with 2 additional projects:

* [I-On Core](https://github.com/i-on-project/core) the repository of academic information
* [I-On Android](https://github.com/i-on-project/android) the mobile application

## Requirements

* Linux/macOS/Windows (on Windows some tests may fail)
* [Docker](https://www.docker.com/)
* [I-On Core](https://github.com/i-on-project/core/blob/master/README.md#running) running
* [PostgreSQL](https://www.postgresql.org/) instance running with database **spring_batch** created. 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;With Docker installed just use `docker run --name postgres -e POSTGRES_PASSWORD=1234 -e POSTGRES_DB=spring_batch -p 5432:5432 -d postgres`

#### Application.properties

Before running some variables have to be updated, such as `spring.datasource.url`, `spring.datasource.username` and `spring.datasource.password`

If the previous docker command was used, the values to use would be:
```
spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/spring_batch
spring.datasource.username=postgres
spring.datasource.password=1234 
```

There is also the need to update `ion.core-base-url` and `ion.core-token`. Check [I-On Core documentation](https://github.com/i-on-project/core/blob/master/README.md)

Finally `spring.mail.username`, `spring.mail.password` and `email.sender` for the email user that will send the alert emails.

## Build

    $ git clone git@github.com:i-on-project/integration.git
    $ cd integration
    
You can build via command line

    $ ./gradlew build
    
Or using an IDE with gradle support such as [IntelliJ IDEA](https://www.jetbrains.com/idea/) or [Eclipse](https://www.eclipse.org/ide/)
    
## Running

When running via IDE first you need to update:

* `ion.resources-folder` on application.properties to **src/main/resources**
* On IOnIntegrationApplication.kt all config path should point to **src/main/resources/config...**

You can also run with Docker (no need to update paths)

    $ docker run -it --network=host i-on-integration-image