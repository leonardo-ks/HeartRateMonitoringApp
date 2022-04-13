package com.example.heartratemonitoringapp.app.auth

sealed class AuthState {
    data class Fail(val message: String) : AuthState()
    object Success : AuthState()
    object First : AuthState()
    object Loading : AuthState()
}