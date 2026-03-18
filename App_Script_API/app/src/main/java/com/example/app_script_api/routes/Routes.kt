package com.example.app_script_api.routes

//Class para las rutas de navegación
sealed class Routes(val route: String) {
    object Login : Routes("login")              //Pantalla de inicio de sesión
    object Formulario : Routes("formulario")    //Pantalla para crear/editar facturas
    object Facturas : Routes("facturas")        //Pantalla para listar y gestionar facturas
}
