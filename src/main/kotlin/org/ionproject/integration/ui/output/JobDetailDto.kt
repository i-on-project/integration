package org.ionproject.integration.ui.output

import com.fasterxml.jackson.annotation.JsonInclude
import org.ionproject.integration.application.JobEngine
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.infrastructure.DateUtils
import java.lang.IllegalStateException
import java.net.URI
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class JobDetailDto(
    val type: String,
    val id: Long,
    val status: String,
    val createdOn: String,
    val startedOn: String? = null,
    val endedOn: String? = null,
    val links: Links? = null,
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
        val self: String
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Parameters(
        val format: String,
        val institution: Institution,
        val programme: Programme? = null,
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
            val name: String,
            val acronym: String,
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
            val name: String,
            val acronym: String
        ) {
            companion object Factory {
                fun from(model: ProgrammeModel): Programme = Programme(model.name, model.acronym)
            }
        }
    }
}
