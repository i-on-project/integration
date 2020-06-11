# ISEL Timetable Batch Job Configurations
The isel timetable batch job receives as input one or more input files with the extension .properties. The files have to be located in .../src/main/resources/config/timetable/isel.

Each file will correspond to an execution of the timetable batch job.

The following properties are accepted:

| Property | Description |
|----------|-------------|
| pdfKey | Key to be used for identifying the timetable pdf path in Spring Batch's execution context |
| hashKey | Key to be used for identifying the timetable pdf hash in Spring Batch's execution context |
| localFileDestination     | Path in the local filesystem in which the timetable pdf file is saved. When multiple instances of the job are running simultaneously, this property needs to be specified, otherwise different jobs will be potentially reading and writing on the same file. |
| pdfRemoteLocation | Url of the timetable pdf to be used on the job. |
| alertRecipient  | Email of the point-of-contact to notify about job outcome |
| timetableUploadUrl | Url to upload timetable information |
| facultyUploadUrl | Url to upload faculty information |
| uploadRetryLimit | Number of times upload should be retried in case I-On Core responds with server error |
| uploadRetrySleepSeconds | Amount of seconds to wait before retrying upload |
