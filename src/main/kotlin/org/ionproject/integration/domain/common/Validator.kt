package org.ionproject.integration.domain.common

class Validator {
    companion object {
        fun isNotValidTerm(term: Int) = (term < 1) || (term > 2)
    }
}
