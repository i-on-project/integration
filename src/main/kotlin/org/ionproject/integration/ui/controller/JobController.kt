package org.ionproject.integration.ui.controller

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.ui.input.CreateJobDto
import org.ionproject.integration.ui.input.InputProcessor
import org.ionproject.integration.ui.output.JobDetailCollectionDto
import org.ionproject.integration.ui.output.JobDetailDto
import org.ionproject.integration.ui.output.PostResponse
import org.ionproject.integration.ui.output.Problem
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import io.swagger.v3.oas.annotations.parameters.RequestBody as OpenApiRequestBody

internal const val JOBS_RESOURCE = "/jobs"
private const val HTTP_PORT = 80
private const val JSON_MEDIA_TYPE = "application/json"
private const val PROBLEM_MEDIA_TYPE = "application/problem+json"

@RestController
@RequestMapping(JOBS_RESOURCE)
@OpenAPIDefinition(
    info = Info(
        title = "i-on Integration Web API",
        version = "1.0.0",
        description = "This API allows the creation of parameterized job executions as well as retrieving data about running tasks and details about specific jobs.",
        license = License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
    ),
    security = [SecurityRequirement(name = "bearerAuth")]
)
@SecuritySchemes(
    SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
    )
)
@ApiResponses(
    ApiResponse(
        responseCode = "403",
        description = "Invalid authorization token.",
        content = [Content(mediaType = PROBLEM_MEDIA_TYPE, schema = Schema(implementation = Problem::class))]
    ),
    ApiResponse(
        responseCode = "401",
        description = "Missing authorization token.",
        content = [Content(mediaType = PROBLEM_MEDIA_TYPE, schema = Schema(implementation = Problem::class))]
    )
)
class JobController(
    val jobEngine: JobEngine,
    val inputProcessor: InputProcessor,
) {

    private val logger = LoggerFactory.getLogger(JobController::class.java)

    @Operation(summary = "Create a new Job Execution.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Job created successfully.",
                content = [Content(mediaType = JSON_MEDIA_TYPE, schema = Schema(implementation = PostResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid or missing arguments.",
                content = [Content(mediaType = PROBLEM_MEDIA_TYPE, schema = Schema(implementation = Problem::class))]
            )
        ]
    )
    @PostMapping(consumes = [JSON_MEDIA_TYPE], produces = [JSON_MEDIA_TYPE])
    fun createJob(
        @OpenApiRequestBody(description = "Parameters required to create a new job.")
        @RequestBody
        body: CreateJobDto,
        servletRequest: HttpServletRequest,
        response: HttpServletResponse
    ): PostResponse {
        val request = inputProcessor.getJobRequest(body)
        val requestResult = jobEngine.runJob(request)

        return when (requestResult.result) {
            JobEngine.JobExecutionResult.CREATED -> {
                PostResponse(
                    location = servletRequest.getLocationForJobRequest(requestResult),
                    status = HttpStatus.CREATED,
                    response = response
                )
            }
            else -> {
                logger.error("Job creation failed: $body")
                PostResponse(
                    status = HttpStatus.BAD_REQUEST,
                    response = response
                )
            }
        }
    }

    @Operation(summary = "Get running and pending Job executions.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "List of running or pending job executions.",
                content = [
                    Content(
                        mediaType = JSON_MEDIA_TYPE,
                        schema = Schema(implementation = JobDetailCollectionDto::class)
                    )
                ],
            )
        ]
    )
    @GetMapping(produces = [JSON_MEDIA_TYPE])
    fun getJobs(servletRequest: HttpServletRequest): List<JobDetailDto> =
        jobEngine.getRunningJobs()
            .map { job ->
                val url = servletRequest.getLocationForJobRequest(job.status)
                JobDetailDto.of(job, url, JobDetailDto.DetailType.METADATA_ONLY)
            }

    @Operation(summary = "Get details about a specific job execution.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Job's current status, metadata and arguments.",
                content = [Content(mediaType = JSON_MEDIA_TYPE, schema = Schema(implementation = JobDetailDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Job ID provided does not exist.",
                content = [Content(mediaType = PROBLEM_MEDIA_TYPE, schema = Schema(implementation = Problem::class))]
            )
        ]
    )
    @GetMapping("/{id}", produces = [JSON_MEDIA_TYPE])
    fun getJobDetails(
        @Parameter(description = "Job ID")
        @PathVariable id: Long,
        servletRequest: HttpServletRequest
    ): JobDetailDto {
        val job = jobEngine.getJob(id)
        val url = servletRequest.getLocationForJobRequest(job.status)

        return JobDetailDto.of(job, url, JobDetailDto.DetailType.FULL)
    }

    private fun HttpServletRequest.getLocationForJobRequest(jobStatus: JobEngine.JobStatus): String {
        val portField = if (serverPort != HTTP_PORT) ":$serverPort" else ""

        return "$scheme://$serverName$portField$contextPath$JOBS_RESOURCE/${jobStatus.jobId}"
    }
}
