package com.example.coflow.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coflow.R
import com.example.coflow.ui.theme.CoFlowColors
import com.example.coflow.ui.theme.CoFlowShapes
import com.example.coflow.ui.theme.CoFlowSpacing
import com.example.coflow.ui.theme.coFlowAuthFieldColors
import com.example.coflow.ui.theme.coFlowBackgroundBrush

enum class AuthMode {
    LOGIN,
    REGISTER
}

@Composable
fun AuthScreen(
    state: AuthUiState,
    onContactChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onSubmit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(coFlowBackgroundBrush()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = CoFlowSpacing.ScreenHorizontal,
                    vertical = 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = CoFlowShapes.Card,
                color = CoFlowColors.CardBackground,
                border = BorderStroke(1.dp, CoFlowColors.BlueBorder.copy(alpha = 0.75f)),
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.auth_welcome_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = CoFlowColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "в CoFlow",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = CoFlowColors.TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = stringResource(
                            if (state.mode == AuthMode.LOGIN) R.string.auth_subtitle_login
                            else R.string.auth_subtitle_register
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = CoFlowColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Поле почты
                    OutlinedTextField(
                        value = state.contact,
                        onValueChange = onContactChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.auth_label_contact)) },
                        placeholder = { Text(stringResource(R.string.auth_placeholder_contact)) },
                        singleLine = true,
                        isError = state.contactError != null,
                        // 👇 ИСПРАВЛЕНО: безопасная Compose-лямбда
                        supportingText = {
                            if (state.contactError != null) {
                                Text(text = state.contactError)
                            }
                        },
                        shape = CoFlowShapes.Input,
                        colors = coFlowAuthFieldColors(),
                        enabled = !state.isSubmitting
                    )

                    // Поле ИМЯ (только для регистрации)
                    if (state.mode == AuthMode.REGISTER) {
                        Spacer(modifier = Modifier.height(5.dp))
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = onNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Ваше имя") },
                            placeholder = { Text("Как к вам обращаться?") },
                            singleLine = true,
                            isError = state.nameError != null,
                            // 👇 ИСПРАВЛЕНО
                            supportingText = {
                                if (state.nameError != null) {
                                    Text(text = state.nameError)
                                }
                            },
                            shape = CoFlowShapes.Input,
                            colors = coFlowAuthFieldColors(),
                            enabled = !state.isSubmitting
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Поле пароля
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = onPasswordChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.auth_label_password)) },
                        placeholder = { Text(stringResource(R.string.auth_placeholder_password)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = state.passwordError != null,
                        // 👇 ИСПРАВЛЕНО
                        supportingText = {
                            if (state.passwordError != null) {
                                Text(text = state.passwordError)
                            }
                        },
                        shape = CoFlowShapes.Input,
                        colors = coFlowAuthFieldColors(),
                        enabled = !state.isSubmitting
                    )

                    // Подтверждение пароля
                    if (state.mode == AuthMode.REGISTER) {
                        Spacer(modifier = Modifier.height(5.dp))
                        OutlinedTextField(
                            value = state.confirmPassword,
                            onValueChange = onConfirmChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.auth_label_confirm_password)) },
                            placeholder = { Text(stringResource(R.string.auth_placeholder_confirm_password)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = state.confirmError != null,
                            // 👇 ИСПРАВЛЕНО
                            supportingText = {
                                if (state.confirmError != null) {
                                    Text(text = state.confirmError)
                                }
                            },
                            shape = CoFlowShapes.Input,
                            colors = coFlowAuthFieldColors(),
                            enabled = !state.isSubmitting
                        )
                    }

                    // Вывод общей ошибки от Firebase
                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Основная кнопка
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = CoFlowShapes.Button,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoFlowColors.Blue,
                            contentColor = Color.White
                        ),
                        enabled = !state.isSubmitting
                    ) {
                        if (state.isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.height(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = stringResource(
                                    if (state.mode == AuthMode.LOGIN) R.string.auth_button_login
                                    else R.string.auth_button_register
                                ),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Переключатель режима
                    TextButton(
                        onClick = onToggleMode,
                        enabled = !state.isSubmitting
                    ) {
                        Text(
                            text = stringResource(
                                if (state.mode == AuthMode.LOGIN) R.string.auth_toggle_to_register
                                else R.string.auth_toggle_to_login
                            ),
                            color = CoFlowColors.TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuthRoute(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AuthScreen(
        state = state,
        onContactChange = viewModel::onContactChange,
        onNameChange = viewModel::onNameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmChange = viewModel::onConfirmChange,
        onToggleMode = viewModel::onToggleMode,
        onSubmit = { viewModel.onSubmit(onAuthSuccess) }
    )
}

// --- ПРЕВЬЮ ---

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AuthLoginPreview() {
    MaterialTheme {
        AuthScreen(
            state = AuthUiState(),
            onContactChange = {}, onNameChange = {}, onPasswordChange = {},
            onConfirmChange = {}, onToggleMode = {}, onSubmit = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AuthLoginErrorPreview() {
    MaterialTheme {
        AuthScreen(
            state = AuthUiState(
                contact = "liza@mail",
                password = "123",
                contactError = "Проверьте формат почты",
                passwordError = "Минимум 6 символов",
                errorMessage = "Неверная почта или пароль"
            ),
            onContactChange = {}, onNameChange = {}, onPasswordChange = {},
            onConfirmChange = {}, onToggleMode = {}, onSubmit = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AuthRegisterPreview() {
    MaterialTheme {
        AuthScreen(
            state = AuthUiState(mode = AuthMode.REGISTER),
            onContactChange = {}, onNameChange = {}, onPasswordChange = {},
            onConfirmChange = {}, onToggleMode = {}, onSubmit = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AuthRegisterMismatchPreview() {
    MaterialTheme {
        AuthScreen(
            state = AuthUiState(
                mode = AuthMode.REGISTER,
                contact = "liza@mail.ru",
                name = "Лиза",
                password = "parol123",
                confirmPassword = "parol124",
                confirmError = "Пароли не совпадают"
            ),
            onContactChange = {}, onNameChange = {}, onPasswordChange = {},
            onConfirmChange = {}, onToggleMode = {}, onSubmit = {}
        )
    }
}