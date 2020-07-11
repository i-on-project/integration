package org.ionproject.integration.factory.implementations

import java.nio.file.Path
import org.ionproject.integration.factory.interfaces.IGenericFactory
import org.ionproject.integration.model.internal.generic.IInternalModel
import org.ionproject.integration.model.internal.generic.Timetable
import org.ionproject.integration.utils.YamlUtils
import org.ionproject.integration.utils.orThrow

class TimetableFactory : IGenericFactory {
    override fun parse(path: Path): IInternalModel {
        return YamlUtils
            .fromYaml(
                path.toFile(),
                Timetable::class.java
            )
            .orThrow()
    }
}
