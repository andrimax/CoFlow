package com.example.coflow.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coflow.data.firebase.AuthRepository
import com.example.coflow.data.firebase.FirebaseAuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val contact: String = "",
    val name: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val contactError: String? = null,
    val nameError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false,
)

class AuthViewModel(
    private val repository: AuthRepository = FirebaseAuthRepositoryImpl()
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onContactChange(value: String) {
        _state.value = _state.value.copy(contact = value, contactError = null, errorMessage = null)
    }

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value, nameError = null, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _state.value = _state.value.copy(password = value, passwordError = null, errorMessage = null)
    }

    fun onConfirmChange(value: String) {
        _state.value = _state.value.copy(confirmPassword = value, confirmError = null, errorMessage = null)
    }

    fun onToggleMode() {
        _state.value = AuthUiState(
            mode = if (_state.value.mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
        )
    }

    fun onSubmit(onSuccess: () -> Unit) {
        val current = _state.value

        // Валидация
        val isContactValid = current.contact.contains("@") && current.contact.contains(".")
        val isPasswordValid = current.password.length >= 6
        val isNameValid = current.mode == AuthMode.LOGIN || current.name.isNotBlank()
        val isConfirmValid = current.mode == AuthMode.LOGIN || current.password == current.confirmPassword

        if (!isContactValid || !isPasswordValid || !isNameValid || !isConfirmValid) {
            _state.value = current.copy(
                contactError = if (!isContactValid) "Введите корректный email" else null,
                passwordError = if (!isPasswordValid) "Минимум 6 символов" else null,
                nameError = if (!isNameValid) "Введите имя" else null,
                confirmError = if (!isConfirmValid) "Пароли не совпадают" else null
            )
            return
        }

        // Начинаем загрузку
        _state.value = current.copy(isSubmitting = true, errorMessage = null)

        // Вызов бэкенда
        viewModelScope.launch {
            val result = if (current.mode == AuthMode.REGISTER) {
                repository.register(current.contact, current.password, current.name)
            } else {
                repository.login(current.contact, current.password)
            }

            result.fold(
                onSuccess = {
                    _state.value = _state.value.copy(isSubmitting = false)
                    onSuccess()
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isSubmitting = false,
                        errorMessage = error.message
                    )
                }
            )
        }
    }
}