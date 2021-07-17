package org.ionproject.integration.infrastructure.repository.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.Language
import org.ionproject.integration.domain.common.ProgrammeModel
import org.ionproject.integration.domain.common.ProgrammeResources
import org.ionproject.integration.infrastructure.exception.ArgumentException
import org.ionproject.integration.infrastructure.text.IgnoredWords
import org.ionproject.integration.infrastructure.text.generateAcronym
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.net.URI

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class InstitutionRepositoryImpl : IInstitutionRepository {
    private val mapper by lazy { ObjectMapper(YAMLFactory()) }

    @Autowired
    private lateinit var props: AppProperties

    override fun getInstitutionByIdentifier(identifier: String): InstitutionModel {
        val supportedInstitutions = readInstitutionsFromFile()
        val institutionDto = findInstitution(identifier, supportedInstitutions)

        return institutionDto.toModel()
    }

    internal fun readInstitutionsFromFile(): List<InstitutionDto> {
        val type = mapper.typeFactory.constructCollectionType(List::class.java, InstitutionDto::class.java)
        val file = props.configurationFile.asFile
        return mapper.readValue(file, type)
    }

    internal fun findInstitution(identifier: String, institutions: List<InstitutionDto>): InstitutionDto =
        institutions
            .firstOrNull { it.identifier.equals(identifier, ignoreCase = true) }
            ?: throw ArgumentException("Institution with ID '$identifier' not found")
}

internal data class InstitutionDto(
    val name: String = "",
    val identifier: String = "",
    val timezone: String = "UTC",
    val resources: List<ResourceDto> = listOf(),
    val programmes: List<ProgrammeDto> = listOf()
) {
    fun toModel(): InstitutionModel {
        val calendar = resources.first { it.type == ResourceType.CALENDAR.identifier }
        val institutionAcronym = generateAcronym(name, IgnoredWords.of(Language.PT))

        return InstitutionModel(
            name = this.name,
            acronym = institutionAcronym,
            identifier = this.identifier,
            timezone = this.timezone,
            academicCalendarUri = URI(calendar.uri)
        )
    }
}

internal data class ResourceDto(
    val type: String = "",
    val uri: String = ""
)

internal enum class ResourceType(val identifier: String) {
    CALENDAR("academic_calendar"),
    TIMETABLE("timetable"),
    EVALUATIONS("evaluations")
}

internal data class ProgrammeDto(
    val name: String = "",
    val acronym: String? = null,
    val resources: List<ResourceDto> = listOf()
) {
    fun toModel(institution: InstitutionModel): ProgrammeModel {
        val acronym = generateAcronym(name, IgnoredWords.of(Language.PT))
        val timetableUri = resources.first { it.type == ResourceType.TIMETABLE.identifier }
        val evaluationsUri = resources.first { it.type == ResourceType.EVALUATIONS.identifier }

        val resources = ProgrammeResources(
            timetableUri = URI(timetableUri.uri),
            evaluationsUri = URI(evaluationsUri.uri)
        )

        return ProgrammeModel(
            institutionModel = institution,
            name = this.name,
            acronym = acronym,
            resources = resources
        )
    }
}
