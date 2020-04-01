# ISEL Timetable Batch Specification

## Overview

This documents aims to specify the process of extracting information from the timetable document published by ISEL in the beggining of each semester. The extraction is a batch job developed using [Spring Batch](https://spring.io/projects/spring-batch) - a framework to build and run batch applications. For more information on how the isel timetable extraction batch job uses the components of Spring Batch, refer to the [batch job architecture summary]()

## Extractable information

From the timetable document there is a wide variety of information that can be extracted about school activities and organization, including course offer and the teaching body of the course. We divide the information according to its source, as it is found on the page header or on the tables in the center and footer of the page. This categorization is relevant in the context of the batch job, because the library used to parse the section of the page that contains tabular data (tabula) is not the same that is used to parse the headers (iText) and so these two segments of the job are independent.

Following is a summary of the information that can be extracted from the timetable document according to what section of the page it is located in, accompanied with its type and an example.

Non-tabular data:

  * Course Name  
    Data Type - String  
    Example - `Licenciatura em Engenharia Informática e de Computadores`  

  * Class Section  
    Data Type - String  
    Example - `LI11N`  

  * Semester  
    Data Type - String  
    Example - `2019/20-Verão`  


Tabular data:

  * Class Offer  
    Data Type - String  
    Example - `Pg`  
  
  * Class Offer Type  
    Data Type - String  
    Example - `T`  

  * Classsroom  
    Data Type - String  
    Example - `G.0.08`  

  * Class Begin Time  
    Data Type - String  
    Example - `15.30`  

  * Class End Time  
    Data Type - String  
    Example - `18.30`  

  * Lecturer  
    Data Type - String  
    Example - `John Doe`  