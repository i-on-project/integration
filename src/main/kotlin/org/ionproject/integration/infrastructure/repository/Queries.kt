package org.ionproject.integration.infrastructure.repository

const val CREATE_JOBS_VIEW_QUERY = """create or replace view vw_job_detail
as
SELECT bje.job_instance_id as id
     , bji.job_name        as name
     , bje.create_time     as creation_date
     , bje.start_time      as start_date
     ,CASE 
		WHEN bje.STATUS IN ('STARTED','STARTING')
			AND bje.create_time < (CURRENT_TIMESTAMP - interval '1 hour')
			THEN 'FAILED'
		ELSE bje.status
		END as status
     , ct.format           as output_format
     , ct.institution      as institution
     , ct.programme        as programme
     , ct.uri              as resource_uri
FROM batch_job_execution bje
         INNER JOIN batch_job_instance bji ON bji.job_instance_id = bje.job_instance_id
         INNER JOIN crosstab(
        'SELECT job_execution_id, key_name, string_val
        FROM batch_job_execution_params
        ORDER BY 1',
        ${"$$"}SELECT unnest('{format,institution,programme,srcRemoteLocation}'::text[])${"$$"}
    ) AS ct("job_execution_id" int8, "format" VARCHAR(100), "institution" VARCHAR(250), "programme" VARCHAR(250),
            "uri" VARCHAR(250))
                    ON ct.job_execution_id = bje.job_execution_id
;"""

const val RUNNING_JOBS_QUERY = "SELECT * FROM vw_job_detail WHERE status IN ('STARTED', 'STARTING')"
const val JOB_DETAILS_QUERY = "SELECT * FROM vw_job_detail WHERE ID = ?"
const val GET_HASH_QUERY = "SELECT hash from filehashes where jobId = ?"
const val INSERT_HASH_QUERY = "insert into filehashes values(?,?)"
const val UPDATE_HASH_QUERY = "update filehashes set hash=? where jobId = ?"
