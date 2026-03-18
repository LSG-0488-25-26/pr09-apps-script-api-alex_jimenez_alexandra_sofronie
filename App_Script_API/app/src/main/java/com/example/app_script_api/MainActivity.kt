package com.example.app_script_api

import android.os.Bundle
import androidx.navigation.compose.rememberNavController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app_script_api.routes.Routes
import com.example.app_script_api.ui.theme.App_Script_APITheme
import com.example.app_script_api.view.FacturasScreen
import com.example.app_script_api.view.FormularioScreen
import com.example.app_script_api.view.LoginScreen
import com.example.app_script_api.viewmodel.FacturaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_Script_APITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    val viewModel: FacturaViewModel = viewModel()

                    val startDestination = if (viewModel.estaLogueado()) Routes.Formulario.route else Routes.Login.route

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Routes.Login.route) {
                            LoginScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }

                        composable(Routes.Formulario.route) {
                            FormularioScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }

                        composable(Routes.Facturas.route) {
                            FacturasScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

//Preview FormularioScreen
@Preview(showBackground = true, name = "Formulario Screen")
@Composable
fun FormularioScreenPreview() {
    App_Script_APITheme {
        FormularioScreen(viewModel = viewModel())
    }
}

//Preview FacturasScreen
@Preview(showBackground = true, name = "Facturas Screen")
@Composable
fun FacturasScreenPreview() {
    App_Script_APITheme {
        FacturasScreen(viewModel = viewModel())
    }
}