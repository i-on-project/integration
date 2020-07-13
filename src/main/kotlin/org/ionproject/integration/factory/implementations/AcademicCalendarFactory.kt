package org.ionproject.integration.factory.implementations

import java.nio.file.Path
import org.ionproject.integration.factory.interfaces.IGenericFactory
import org.ionproject.integration.model.internal.generic.AcademicCalendar
import org.ionproject.integration.model.internal.generic.IInternalModel
import org.ionproject.integration.utils.YamlUtils
import org.ionproject.integration.utils.orThrow

class AcademicCalendarFactory : IGenericFactory {
    override fun parse(path: Path): IInternalModel {
        return YamlUtils
            .fromYaml(
                path.toFile(),
                AcademicCalendar::class.java
            )
            .orThrow()
    }
}
