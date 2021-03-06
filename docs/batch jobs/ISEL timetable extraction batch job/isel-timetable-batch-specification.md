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
    "school": {
        "name": "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
        "acr": ""
    },
    "programme": {
        "name": "Licenciatura em Engenharia Informática e de Computadores",
        "acr": ""
    },
    "calendarTerm": "2019/20-Verão",
    "calendarSection": "LI11D",
    "language": "pt-PT",
    "courses": [
        {
            "label": {
                "acr": "ALGA"
            },
            "events": [
                {
                    "title": "",
                    "description": "Aulas Teóricas de ALGA",
                    "category": 2,
                    "location": [
                        "E.1.08"
                    ],
                    "startDate": "2020-01-20T12:30",
                    "endDate": "2020-01-20T15:30",
                    "weekday": [
                        "FR"
                    ]
                }
            ]
        }
    ]
}
```
And the kotlin data classes that generate it
```kotlin
data class Timetable (
	@JsonProperty("school") val school : School,
	@JsonProperty("programme") val programme : Programme,
	@JsonProperty("calendarTerm") val calendarTerm : String,
	@JsonProperty("calendarSection") val calendarSection : String,
    @JsonProperty("language") val language : String,
	@JsonProperty("courses") val courses : List<Course>
)
data class School(
    @JsonProperty("name") val name: String,
    @JsonProperty("acr") val acr: String
)
data class Programme(
    @JsonProperty("name") val name: String,
    @JsonProperty("acr") val acr: String
)
data class Course (
	@JsonProperty("label") val label : Label,
    @JsonProperty("events") val events : List<Event>
)
data class Label(
    @JsonProperty("acr") val acr: String
)
data class Event (
	@JsonProperty("title") val title : String,
    @JsonProperty("description") val description : String,
    @JsonProperty("category") val category : Integer,
	@JsonProperty("location") val location : List<String>,
	@JsonProperty("startDate") val beginTime : String,
	@JsonProperty("endDate") val endTime : String,
    @JsonProperty("weekday") val weekday : List<String>
)
```
### Faculty
Example data in json format
```json
{
	"school": {
		"name": "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA",
		"acr": ""
	},
	"programme": {
		"name": "Licenciatura em Engenharia Informática e de Computadores",
		"acr": ""
	},
	"calendarTerm": "2019/20-Verão",
	"calendarSection": "LI11D",
	"language": "pt-PT",
	"courses": [
		{
			"label": {
				"acr": "ALGA"
			},
			"teachers": [
				{
					"name": "Teresa Maria de Araújo Melo Quinteiro"
				}
			]
		}
	]
}
```
And the kotlin data classes that generate it
```kotlin
data class CourseTeacher (
	@JsonProperty("school") val school : School,
    @JsonProperty("programme") val programme : Programme,
	@JsonProperty("calendarTerm") val calendarTerm : String,
	@JsonProperty("calendarSection") val calendarSection : String,
    @JsonProperty("language") val language : String,
	@JsonProperty("courses") val courses : List<Faculty>
)
data class School(
    @JsonProperty("name") val name: String,
    @JsonProperty("acr") val acr: String
)
data class Programme(
    @JsonProperty("name") val name: String,
    @JsonProperty("acr") val acr: String
)
data class Faculty (
	@JsonProperty("label") val label : Label,
	@JsonProperty("teachers") val teachers : List<Teacher>
)
data class Teacher (
	@JsonProperty("name") val name : String
)
```
