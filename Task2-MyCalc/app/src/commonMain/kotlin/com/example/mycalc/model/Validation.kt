package com.example.mycalc.model

data class ValidationResult(
    val principalError: String? = null,
    val rateError: String? = null,
    val yearsError: String? = null
) {
    val isValid: Boolean
        get() = principalError == null && rateError == null && yearsError == null
}
