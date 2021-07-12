
# Job creation
## TIMETABLE job creation

Example request body:

`application/json`
```json
{
    "institution": "pt.ipl.isel",
    "programme": "leic",
    "format": "json",
    "type": "timetable"
}
```

## ACADEMIC CALENDAR job creation

Example request body:

`application/json`
```json
{
    "institution": "pt.ipl.isel",
    "format": "yaml",
    "type": "calendar"
}
```

## Job creation response

### OK Response
```http
Status : 201 Created
Headers:
    Content-Type : application/json
    Location : http://localhost:80/integration/jobs/7
```
`application/json`
```json
{
    "location": "http://localhost:80/integration/jobs/7",
    "status": "CREATED"
}
```

### NOT OK response

```http
Status : 400 Bad Request
Headers:
    Content-Type : application/problem+json
```
`application/problem+json`
```json
{
    "type": "https://github.com/i-on-project/integration/blob/master/docs/infrastructure/ArgumentException.md",
    "title": "Bad Request",
    "status": 400,
    "detail": "Institution with ID 'pt.ipl.isexl' not found",
    "instance": "/integration/jobs"
}
```

# Job details

## Job list response
`application/json`
```json
[
    {
        "type": "timetable",
        "id": 4,
        "status": "RUNNING",
        "createdOn": "2021-07-05T02:03:52Z",
        "startedOn": "2021-07-05T02:03:53Z",
        "links": {
            "self": "http://localhost:80/integration/jobs/4"
        }
    },
    {
        "type": "timetable",
        "id": 5,
        "status": "CREATED",
        "createdOn": "2021-07-05T01:03:57Z",
        "links": {
            "self": "http://localhost:80/integration/jobs/5"
        }
    },
    {
        "type": "calendar",
        "id": 6,
        "status": "CREATED",
        "createdOn": "2021-07-05T01:03:59Z",
        "links": {
            "self": "http://localhost:80/integration/jobs/6"
        }
    }
]
```


## Job detail response

Example Calendar job response:
`application/json`
```json
{
    "type": "calendar",
    "id": 2,
    "status": "COMPLETED",
    "createdOn": "2021-07-05T00:58:22Z",
    "startedOn": "2021-07-05T00:58:22Z",
    "endedOn": "2021-07-05T00:58:32Z",
    "links": {
        "self": "http://localhost:80/integration/jobs/2"
    },
    "parameters": {
        "format": "YAML",
        "institution": {
            "name": "Instituto Superior de Engenharia de Lisboa",
            "acronym": "ISEL",
            "identifier": "pt.ipl.isel"
        },
        "sourceUris": [
            "https://www.isel.pt/media/uploads/OS09P2020_signed.pdf"
        ]
    }
}
```

Example timetable job response:
`application/json`
```json
{
    "type": "timetable",
    "id": 3,
    "status": "RUNNING",
    "createdOn": "2021-07-05T00:59:13Z",
    "startedOn": "2021-07-05T00:59:13Z",
    "links": {
        "self": "http://localhost:80/integration/jobs/3"
    },
    "parameters": {
        "format": "YAML",
        "institution": {
            "name": "Instituto Superior de Engenharia de Lisboa",
            "acronym": "ISEL",
            "identifier": "pt.ipl.isel"
        },
        "programme": {
            "name": "Licenciatura em Engenharia Informática e Multimédia",
            "acronym": "LEIM"
        },
        "sourceUris": [
            "https://www.isel.pt/media/uploads/ADEETC_LEIM_210228.pdf"
        ]
    }
}
```

## Wrong Job ID response
```http
Status : 404 Not Found
Headers:
    Content-Type : application/problem+json
```
`application/problem+json`
```json
{
    "type": "https://github.com/i-on-project/integration/blob/master/docs/infrastructure/JobNotFoundException.md",
    "title": "Not Found",
    "status": 404,
    "detail": "Job with ID 41 not found",
    "instance": "/integration/jobs/41"
}
```

