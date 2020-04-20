# ISEL Timetable Batch Specification

## Overview

This documents aims to specify the process of extracting information from the timetable document published by ISEL in the beggining of each semester. For more information on how the isel timetable extraction batch job uses the components of Spring Batch, refer to the [batch job architecture page]().

## Extractable information

From the timetable document there is a wide variety of information that can be extracted about school activities and organization, including course offer and the teaching staff of the course. We divide the information according to its source, as it is found on the page header or on the tables in the center and footer of the page. This categorization is relevant in the context of the batch job, because the library used to parse the section of the page that contains tabular data (tabula) is not the same that is used to parse the headers (iText) and so these two segments of the job are independent.

Following we have the information that can be extracted from the timetable document grouped in two clusters of independent data, timetable and faculty, in json format.
### Timetable

```json
{
    "type": "object",
    "title": "Class Timetable",
    "properties": {
        "school": {
            "type": "string",
            "title": "School name",
            "examples": [
                "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA"
            ]
        },
        "programme": {
            "type": "string",
            "title": "Programme",
            "default": "",
            "examples": [
                "Licenciatura Engenharia Informática e Computadores"
            ]
        },
        "term": {
            "type": "string",
            "title": "Term",
            "examples": [
                "2019/20-Verão"
            ]
        },
        "class": {
            "type": "string",
            "title": "Class",
            "examples": [
                "LI11D"
            ]
        },
        "courses": {
            "type": "array",
            "title": "Courses for class in term",
            "items": {
                "type": "object",
                "title": "Course details",
                "examples": [
                    {
                        "course": "ALGA[I]",
                        "course_type": "(T)",
                        "room": "E.1.31",
                        "begin_time": "14:00:00",
                        "end_time": "15:30:00",
                        "duration": "1:30:00",
                        "weekday": "Monday"
                    }
                ],
                "properties": {
                    "course": {
                        "type": "string",
                        "title": "Course",
                        "examples": [
                            "ALGA"
                        ]
                    },
                    "course_type": {
                        "type": "string",
                        "title": "Type of course: Theory or Practice",
                        "examples": [
                            "(T)"
                        ]
                    },
                    "room": {
                        "title": "Course room",
                        "examples": [
                            "E.1.31"
                        ]
                    },
                    "begin_time": {
                        "type": "string",
                        "title": "Time that course begins",
                        "examples": [
                            "14:00:00"
                        ]
                    },
                    "end_time": {
                        "type": "string",
                        "title": "Time that course ends",
                        "examples": [
                            "15:30:00"
                        ]
                    },
                    "duration": {
                        "type": "integer",
                        "title": "Course duration",
                        "examples": [
                            90
                        ]
                    },
                    "weekday": {
                        "type": "string",
                        "title": "Weekday for course instance",
                        "examples": [
                            "Monday"
                        ]
                    }
                }
            }
        }
    }
}
```
### Faculty
```json
{
    "type": "object",
    "title": "Class faculty",
    "properties": {
        "school": {
            "type": "string",
            "title": "School name",
            "examples": [
                "INSTITUTO SUPERIOR DE ENGENHARIA DE LISBOA"
            ]
        },
        "programme": {
            "type": "string",
            "title": "Programme",
            "default": "",
            "examples": [
                "Licenciatura Engenharia Informática e Computadores"
            ]
        },
        "term": {
            "type": "string",
            "title": "Term",
            "examples": [
                "2019/20-Verão"
            ]
        },
        "class": {
            "type": "string",
            "title": "Class",
            "examples": [
                "LI11D"
            ]
        },
        "faculty": {
            "type": "array",
            "title": "Faculty",
            "items": {
                "type": "object",
                "title": "Faculty details",
                "examples": [
                    {
                        "course": "ALGA",
                        "course_type": "(T)",
                        "teachers": [
                            {
                                "name": "João Trindade"
                            }
                        ]
                    }
                ],
                "properties": {
                    "course": {
                        "type": "string",
                        "title": "Course",
                        "examples": [
                            "ALGA"
                        ]
                    },
                    "course_type": {
                        "type": "string",
                        "title": "Type of course: Theory or Practice",
                        "examples": [
                            "(T)"
                        ]
                    },
                    "teachers": {
                        "type": "array",
                        "title": "Teachers",
                        "items": {
                            "type": "object",
                            "title": "Teacher details",
                            "examples": [
                                {
                                    "name": "João Trindade"
                                }
                            ],
                            "properties": {
                                "name": {
                                    "type": "string",
                                    "title": "Teacher name",
                                    "examples": [
                                        "João Trindade"
                                    ]
                                }
                            }
                        }
                    }
                }
            }
        }
    }
```
