# ISEL Timetable Batch Job Configurations
The integration application starts an instance of the ISEL Timetable Batch job for each file present in src/main/resources/config/timetable/isel. The parameters passed for each job instance are the properties contained in a file of that directory.

The following properties are accepted:

| Property | Description |
|----------|-------------|
| srcRemoteLocation | Url of the timetable pdf to be used on the job. |
| alertRecipient  | Email of the point-of-contact to notify about job outcome |
