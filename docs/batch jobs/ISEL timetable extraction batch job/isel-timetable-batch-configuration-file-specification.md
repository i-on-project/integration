# ISEL Timetable Batch Job Configurations
The integration application starts an instance of the ISEL Timetable Batch job for each file present in src/main/resources/config/timetable/isel. The parameters passed for each job instance are the properties contained in a file of that directory.

The following properties are accepted:

| Property | Description |
|----------|-------------|
| pdfKey | Key to be used for identifying the timetable pdf path in Spring Batch's execution context |
| hashKey | Key to be used for identifying the timetable pdf hash in Spring Batch's execution context |
| localFileDestination     | Path in the local filesystem in which the timetable pdf file is saved. When multiple instances of the job are running simultaneously, this property needs to be specified, otherwise different jobs will be potentially reading and writing on the same file. |
| pdfRemoteLocation | Url of the timetable pdf to be used on the job. |
| alertRecipient  | Email of the point-of-contact to notify about job outcome |
