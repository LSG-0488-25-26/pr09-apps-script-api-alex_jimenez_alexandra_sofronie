package com.example.app_script_api.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_script_api.routes.Routes
import com.example.app_script_api.viewmodel.FacturaViewModel

private const val USUARIO_VALIDO = "Usuario"
private const val PASSWORD_VALIDO = "1234"

/**
 * Pantalla de login. Guarda credencials (user, pwd) a SharedPreferences
 * i redirigeix a FormularioScreen en iniciar sessió.
 */

@Composable
fun LoginScreen(
    viewModel: FacturaViewModel,
    navController: NavHostController
) {
    var user by remember { mutableStateOf(viewModel.obtenerUser()) }
    var pwd by remember { mutableStateOf(viewModel.obtenerPwd()) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6200EE)),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    "🔐 Iniciar sessió",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Generador de Factures",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = user,
                        onValueChange = {
                            user = it
                            error = ""
                        },
                        label = { Text("Usuari") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = pwd,
                        onValueChange = {
                            pwd = it
                            error = ""
                        },
                        label = { Text("Contrasenya") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    if (error.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            when {
                                user.isBlank() && pwd.isBlank() -> error = "Omple l'usuari i la contrasenya"
                                user.isBlank() -> error = "Introdueix l'usuari"
                                pwd.isBlank() -> error = "Introdueix la contrasenya"
                                else -> {
                                    if (user.trim() == USUARIO_VALIDO && pwd.trim() == PASSWORD_VALIDO) {
                                        viewModel.guardarCredenciales(user.trim(), pwd.trim())
                                        navController.navigate(Routes.Formulario.route) {
                                            popUpTo(Routes.Login.route) { inclusive = true }
                                        }
                                    } else {
                                        error = "Usuari o contrasenya incorrectes"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text("Iniciar sessió", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}