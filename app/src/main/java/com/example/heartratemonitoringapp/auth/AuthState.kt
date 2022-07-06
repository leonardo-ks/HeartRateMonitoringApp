package com.example.heartratemonitoringapp.auth

sealed class AuthState {
    data class Fail(val message: String) : AuthState()
    object Success : AuthState()
    object First : AuthState()
    object Loading : AuthState()
}