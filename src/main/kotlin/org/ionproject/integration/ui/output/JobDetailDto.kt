package org.ionproject.integration.ui.output

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.application.job.CALENDAR_IDENTIFIER
import org.ionproject.integration.application.job.EVALUATIONS_IDENTIFIER
import org.ionproject.integration.application.job.TIMETABLE_IDENTIFIER
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.infrastructure.DateUtils
import org.ionproject.integration.infrastructure.file.OutputFormat
import java.lang.IllegalStateException
import java.net.URI
import java.time.LocalDateTime

internal const val RUNNING_JOBS_EXAMPLE =
    "[\n    {\n        \"type\": \"timetable\",\n        \"id\": 37,\n        \"status\": \"RUNNING\",\n        \"createdOn\": \"2021-07-17T23:07:50Z\",\n        \"startedOn\": \"2021-07-17T23:07:50Z\",\n        \"links\": {\n            \"self\": \"http://localhost/integration/jobs/37\"\n        }\n    },\n    {\n        \"type\": \"timetable\",\n        \"id\": 38,\n        \"status\": \"CREATED\",\n        \"createdOn\": \"2021-07-17T23:07:52Z\",\n        \"links\": {\n            \"self\": \"http://localhost/integration/jobs/38\"\n        }\n    }\n]"

data class JobDetailCollectionDto(
    @Schema(description = "List of active and pending jobs.", example = RUNNING_JOBS_EXAMPLE)
    val jobs: List<JobDetailDto>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class JobDetailDto(
    @Schema(
        description = "Job type",
        allowableValues = [EVALUATIONS_IDENTIFIER, TIMETABLE_IDENTIFIER, CALENDAR_IDENTIFIER],
        example = TIMETABLE_IDENTIFIER
    )
    val type: String,

    @Schema(description = "Job ID", example = "42")
    val id: Long,

    @Schema(description = "Current Job execution status.", implementation = JobEngine.JobExecutionResult::class)
    val status: String,

    @Schema(description = "Job creation date in UTC time.", implementation = LocalDateTime::class)
    val createdOn: String,

    @Schema(description = "Job start date in UTC time.", implementation = LocalDateTime::class)
    val startedOn: String? = null,

    @Schema(description = "Job completion date in UTC time.", implementation = LocalDateTime::class)
    val endedOn: String? = null,

    @Schema(description = "Links related to this Job.")
    val links: Links? = null,

    @Schema(description = "Parameters provided during job creation.")
    val parameters: Parameters? = null
) {

    companion object Factory {
        fun of(
            job: JobEngine.IntegrationJob,
            requestURL: String,
            detailType: DetailType = DetailType.FULL
        ): JobDetailDto {

            val parameters = if (detailType == DetailType.FULL) Parameters.from(job) else null

            return JobDetailDto(
                type = job.type.identifier,
                id = job.status.jobId ?: throw IllegalStateException("Job $job cannot have an empty ID field"),
                status = job.status.result.name,
                createdOn = job.parameters.creationDate.toOutputFormat(),
                startedOn = job.parameters.startDate?.toOutputFormat(),
                endedOn = job.parameters.endDate?.toOutputFormat(),
                links = Links(requestURL),
                parameters = parameters
            )
        }

        private fun LocalDateTime.toOutputFormat(): String = DateUtils.formatToISO8601(this)
    }

    enum class DetailType {
        FULL, METADATA_ONLY
    }

    data class Links(
        @Schema(
            description = "Link for this Job.",
            implementation = URI::class,
            example = "http://localhost/integration/jobs/32"
        )
        val self: String
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Parameters(
        @Schema(description = "The requested output format.", implementation = OutputFormat::class)
        val format: String,

        @Schema(description = "Institution metadata.")
        val institution: Institution,

        @Schema(description = "Programme metadata.")
        val programme: Programme? = null,

        @Schema(
            description = "URIs used as sources for the parsed data.",
            implementation = URI::class,
            example = """[ "https://www.isel.pt/media/uploads/ADEETC_MEIC_210301.pdf" ]"""
        )
        val sourceUris: List<URI>
    ) {

        companion object Factory {
            fun from(job: JobEngine.IntegrationJob): Parameters {
                return Parameters(
                    format = job.parameters.format.name,
                    institution = Institution.from(job.parameters.institution),
                    programme = job.parameters.programme?.let { Programme.from(it) },
                    sourceUris = listOf(job.parameters.uri)
                )
            }
        }

        data class Institution(
            @Schema(description = "Institution name", example = "Instituto Superior de Engenharia de Lisboa")
            val name: String,

            @Schema(description = "Institution acronym", example = "ISEL")
            val acronym: String,

            @Schema(description = "Institution identifier", example = "pt.ipl.isel")
            val identifier: String
        ) {
            companion object Factory {
                fun from(model: InstitutionModel): Institution =
                    Institution(
                        name = model.name,
                        acronym = model.acronym,
                        identifier = model.identifier
                    )
            }
        }

        data class Programme(
            @Schema(
                description = "Full programme name",
                example = "Licenciatura em Engenharia Informática, Redes e Telecomunicações"
            )
            val name: String,

            @Schema(description = "Programme acronym", example = "LEIRT")
            val acronym: String
        ) {
            companion object Factory {
                fun from(model: ProgrammeModel): Programme = Programme(model.name, model.acronym)
            }
        }
    }
}
