
package com.example.coflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.coflow.ui.auth.AuthRoute
import com.example.coflow.ui.tasks.TasksScreen
import com.example.coflow.ui.theme.CoFlowTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CoFlowTheme {
                // При запуске всегда показываем авторизацию.
                var isAuthenticated by remember {
                    mutableStateOf(false)
                }

                if (isAuthenticated) {
                    // Экран задач после успешной авторизации.
                    TasksScreen()
                } else {
                    AuthRoute(
                        onAuthSuccess = {
                            isAuthenticated = true
                        }
                    )
                }
            }
        }
    }
}
