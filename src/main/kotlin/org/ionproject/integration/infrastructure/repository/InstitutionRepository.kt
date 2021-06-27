package org.ionproject.integration.infrastructure.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.ionproject.integration.application.config.AppProperties
import org.ionproject.integration.domain.common.InstitutionModel
import org.ionproject.integration.domain.common.Language
import org.ionproject.integration.infrastructure.text.IgnoredWords
import org.ionproject.integration.infrastructure.text.generateAcronym
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.net.URI

interface IInstitutionRepository {
    fun getInstitutionByIdentifier(identifier: String): InstitutionModel
}

@Service
class InstitutionRepositoryFile : IInstitutionRepository {
    private val mapper by lazy { ObjectMapper(YAMLFactory()) }

    @Autowired
    private lateinit var props: AppProperties

    override fun getInstitutionByIdentifier(identifier: String): InstitutionModel {
        val type = mapper.typeFactory.constructCollectionType(List::class.java, InstitutionDto::class.java)
        val file = props.configurationFile.asFile
        val supportedInstitutions: List<InstitutionDto> = mapper.readValue(file, type)

        return supportedInstitutions.first().toModel()
    }
}

private data class InstitutionDto(
    val name: String = "",
    val identifier: String = "",
    val resources: List<ResourceDto> = listOf(),
    val programmes: List<ProgrammeDto> = listOf()
) {
    fun toModel(): InstitutionModel {
        return InstitutionModel(
            name = this.name,
            acronym = generateAcronym(this.name, IgnoredWords.of(Language.PT)),
            identifier = this.identifier,
            academicCalendarUri = URI(this.resources.first().uri)
        )
    }
}

private data class ResourceDto(
    val type: String = "",
    val uri: String = ""
)

private data class ProgrammeDto(
    val name: String = "",
    val acronym: String? = null,
    val resources: List<ResourceDto> = listOf()
)
