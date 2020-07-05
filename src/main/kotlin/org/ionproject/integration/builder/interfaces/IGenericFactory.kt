package org.ionproject.integration.builder.interfaces

import java.nio.file.Path
import org.ionproject.integration.model.internal.generic.IInternalModel

interface IGenericFactory {
    fun parse(path: Path): IInternalModel
}
