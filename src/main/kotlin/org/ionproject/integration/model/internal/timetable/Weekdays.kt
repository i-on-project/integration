package org.ionproject.integration.model.internal.timetable

enum class Weekdays {
    MONDAY {
        override fun toShortString() = "MO"
        override fun toPortuguese() = "SEGUNDA"
    },
    TUESDAY {
        override fun toShortString() = "TU"
        override fun toPortuguese() = "TERÇA"
    },
    WEDNESDAY {
        override fun toShortString() = "WE"
        override fun toPortuguese() = "QUARTA"
    },
    THURSDAY {
        override fun toShortString() = "TH"
        override fun toPortuguese() = "QUINTA"
    },
    FRIDAY {
        override fun toShortString() = "FR"
        override fun toPortuguese() = "SEXTA"
    },
    SATURDAY {
        override fun toShortString() = "SA"
        override fun toPortuguese() = "SÁBADO"
    },
    SUNDAY {
        override fun toShortString() = "SU"
        override fun toPortuguese() = "DOMINGO"
    };

    abstract fun toShortString(): String
    abstract fun toPortuguese(): String
}
