package com.example.core.data

sealed class Validate(var message: String? = null) {
    object Loading : Validate()
    object Success : Validate()
    class Error(message: String) : Validate(message)
}