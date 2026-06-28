package com.example.nutrifit.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nutrifit.ui.navigation.NavRoutes

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomNavItem(NavRoutes.Home, "Trang chủ", Icons.Filled.Home)
    data object Meal : BottomNavItem(NavRoutes.Meal, "Thực đơn", Icons.Filled.Restaurant)
    data object DailyLog : BottomNavItem(NavRoutes.DailyLog, "Nhật ký", Icons.Filled.Book)
    data object Workout : BottomNavItem(NavRoutes.Workout, "Bài tập", Icons.Filled.FitnessCenter)
    data object Setting : BottomNavItem(NavRoutes.Setting, "Cài đặt", Icons.Filled.Settings)
}

@Composable
fun BottomNavBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Meal,
        BottomNavItem.DailyLog,
        BottomNavItem.Workout,
        BottomNavItem.Setting
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface, // A standard color
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1AC9AC), // Your app's primary color
                    selectedTextColor = Color(0xFF1AC9AC),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFF1AC9AC).copy(alpha = 0.1f) // A subtle indicator
                )
            )
        }
    }
}
