package com.example.shoppinglist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object ShoppingList : Screen("shopping_list", "Shopping List", Icons.Default.Home)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Settings : Screen("settings", "Settings", Icons.Default.Home) // Ikon tidak akan ditampilkan di drawer
}
