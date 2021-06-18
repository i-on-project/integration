package org.ionproject.integration.infrastructure.repository

const val SETUP_EXTENSION_QUERY = "CREATE extension IF NOT EXISTS tablefunc;"

const val RUNNING_JOBS_QUERY = """
SELECT bje.job_instance_id
,bji.job_name
,bje.create_time
,ct.format
,ct.institution
,ct.programme
,ct.uri
FROM batch_job_execution bje
INNER JOIN batch_job_instance bji ON bji.job_instance_id = bje.job_instance_id
INNER JOIN crosstab('SELECT job_execution_id, key_name, string_val
FROM batch_job_execution_params
ORDER BY 1',
${"$"}${"$"}SELECT unnest('{format,institution,programme,srcRemoteLocation}'::text[])${"$"}$)
AS ct("job_execution_id" int8, "format" VARCHAR(100), "institution" VARCHAR(250), "programme" VARCHAR(250), "uri" VARCHAR(250))
ON ct.job_execution_id = bje.job_execution_id
WHERE bje.STATUS = 'STARTED' and bje.exit_code = 'UNKNOWN'
"""
