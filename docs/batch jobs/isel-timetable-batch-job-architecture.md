# ISEL Timetable Job Architecture

## Objectives of the present document

This document aims to introduce how Spring Batch is used in ISEL timetable job extraction process.
There is a small number of concepts that need to be introduced in advance relating to the Spring Batch Framework, its architecture and its domain specific language.

## Spring Batch Framework Architecture

Spring Batch is a framework used to abstract the common pieces used in batch applications, maximizing code reuse and minizing the amount of boilerplate code written.
In order to acchieve this, it has three layers - application, core and infrastructure. Wrapping the remaining two is the application layer, which consists of custom code and configuration used to build new batch processes. It is at this level that developers can intervene to build new batch jobs.
The core layer contains the interfaces that define the batch domain (e.g. JobLauncher, Job, Step). The infrastructure layer handles reading and writing to files and databases in addition to what to do when a job is retried after failure.
Embedded in Spring Batch by default are functionalities that are critical to any enterprise batch system, including the ability to retry a step after failure, keep the state of a job for re-execution and

## Domain Specific Language

The Spring Batch Documentation has a section on [Domain Specific Language](https://docs.spring.io/spring-batch/docs/current-SNAPSHOT/reference/html/domain.html). Most important to the present context are the concepts of `Job`, `Step`, `ItemReader`, `ItemProcessor` and `ItemWriter`.

Following is a summary of the concepts that are crucial to understand this document:

* `Job` - A process that executes from start to finish without interruption or interaction, consisting of one or more `steps`. It can have associated retry logic;
* `Step` - Independent and sequential phase of a batch job;
* `Item` - 
* `Chunk` - Fixed amount of items;
* `ItemReader` - Abstraction that represents `sStep` input, per item;
* `ItemProcessor` - Abstraction that represents `Step` processing logic, associated to the domain of the application
* `ItemWriter` - Abstraction that represents `Step` output, per item, or per chunk.