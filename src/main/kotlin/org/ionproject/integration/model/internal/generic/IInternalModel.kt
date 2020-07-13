package org.ionproject.integration.model.internal.generic

import org.ionproject.integration.model.external.generic.ICoreModel

interface IInternalModel {
    fun toCore(): ICoreModel
}
