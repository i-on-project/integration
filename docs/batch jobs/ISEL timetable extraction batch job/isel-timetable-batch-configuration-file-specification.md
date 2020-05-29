# ISEL Timetable Batch Job Configurations
The isel timetable batch job receives as input a configuration file with the extension .properties.

The file is specified when the application is launched through the --spring.config.location option. The following properties are accepted:

| Property | Description | Default value |
|----------|-------------|---------------|
| local-file-path-key | Key to be used for identifying the timetable pdf path in Spring Batch's execution context | pdf-key |
| local-file-destination     | Path in the local filesystem in which the timetable pdf file is saved. When multiple instances of the job are running simultaneously, this property needs to be specified, otherwise different jobs will be potentially reading and writing on the same file. | /tmp/TIMETABLE.pdf  |
| pdf-remote-location | Url of the timetable pdf to be used on the job. | https://www.isel.pt/media/uploads/LEIC_0310.pdf |
| alert-recipient  | Email of the point-of-contact to notify about job outcome | org.ionproject.integration@gmail.com  |
| timetable-upload-url | Url to upload timetable information | tbd  |
| faculty-upload-url | Url to upload faculty information | tbd |
| upload-retry-limit | Number of times upload should be retried in case I-On Core responds with server error | 3 |
| upload-retry-sleep-seconds | Amount of seconds to wait before retrying upload | 300 |
