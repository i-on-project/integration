# ISEL Timetable Batch Specification

## Overview

This documents aims to specify the process of extracting information from the timetable document published by ISEL in the beggining of each semester. For more information on how the isel timetable extraction batch job uses the components of Spring Batch, refer to the [batch job architecture page]().

## Extractable information

From the timetable document there is a wide variety of information that can be extracted about school activities and organization, including course offer and the teaching staff of the course. We divide the information according to its source, as it is found on the page header or on the tables in the center and footer of the page. This categorization is relevant in the context of the batch job, because the library used to parse the section of the page that contains tabular data (tabula) is not the same that is used to parse the headers (iText) and so these two segments of the job are independent.

Following we have the information that can be extracted from the timetable document grouped in two clusters of independent data, timetable and faculty, in json format.

### Timetable
Example data in json format
```json
{
    "school": "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
    "programme": "Licenciatura Engenharia Informática e Computadores",
    "term": "2019/20-Verão",
    "class": "LI11D",
    "courses": [
        {
            "course": "ALGA[I]",
            "course_type": "(T)",
            "room": "E.1.31",
            "begin_time": "14:00:00",
            "end_time": "15:30:00",
            "duration": "1:30:00",
            "weekday": "Monday"
        }
    ]
}
```
And the kotlin data classes that generate it
```kotlin
data class Timetable (
	@JsonProperty("school") val school : String,
	@JsonProperty("programme") val programme : String,
	@JsonProperty("term") val term : String,
	@JsonProperty("class") val klass : String,
	@JsonProperty("courses") val courses : List<Course>
)
data class Course (
	@JsonProperty("name") val name : String,
	@JsonProperty("type") val type : String,
	@JsonProperty("room") val room : String,
	@JsonProperty("begin_time") val begin_time : String,
	@JsonProperty("end_time") val end_time : String,
	@JsonProperty("duration") val duration : String,
	@JsonProperty("weekday") val weekday : String
)
```
### Faculty
Example data in json format
```json
{
    "school": "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
    "programme": "Licenciatura Engenharia Informática e Computadores",
    "term": "2019/20-Verão",
    "class": "LI11D",
    "faculty": [
        {
            "course": "ALGA",
            "course_type": "(T)",
            "teachers": [
                {
                    "name": "João Trindade"
                }
            ]
        }
    ]
}
```
And the kotlin data classes that generate it
```kotlin
data class CourseTeacher (
	@JsonProperty("school") val school : String,
	@JsonProperty("programme") val programme : String,
	@JsonProperty("term") val term : String,
	@JsonProperty("class") val klass : String,
	@JsonProperty("faculty") val faculty : List<Faculty>
)
data class Faculty (
	@JsonProperty("course") val course : String,
	@JsonProperty("course_type") val course_type : String,
	@JsonProperty("teachers") val teachers : List<Teacher>
)
data class Teacher (
	@JsonProperty("name") val name : String
)
```
