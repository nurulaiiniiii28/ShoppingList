package com.example.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarItems = listOf(Screen.ShoppingList, Screen.Profile)

    val currentTitle = when (currentDestination?.route) {
        Screen.Profile.route -> Screen.Profile.title
        Screen.Settings.route -> Screen.Settings.title
        else -> Screen.ShoppingList.title
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Settings") },
                    selected = currentDestination?.route == Screen.Settings.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentTitle) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    bottomBarItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Screen.ShoppingList.route, modifier = modifier) {
        composable(
            route = Screen.ShoppingList.route,
            exitTransition = { fadeOut(animationSpec = tween(300)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) }
        ) {
            ShoppingListScreen()
        }
        composable(
            route = Screen.Profile.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) }
        ) {
            ProfileScreen()
        }
        composable(
            route = Screen.Settings.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            SettingsScreen()
        }
    }
}


data class ShoppingItem(val id: Int, val name: String, val isChecked: Boolean = false)

@Composable
fun ShoppingListScreen() {
    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var newItemName by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                label = { Text("Add new item") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newItemName.isNotBlank()) {
                        sItems = sItems + ShoppingItem(
                            id = (sItems.maxOfOrNull { it.id } ?: 0) + 1,
                            name = newItemName
                        )
                        newItemName = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Icon", tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Pencarian
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Item") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Daftar item
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val filteredItems = sItems.filter { it.name.contains(searchQuery, ignoreCase = true) }
            items(filteredItems, key = { it.id }) { item ->
                ShoppingListItem(
                    item = item,
                    onItemAction = { currentItem ->
                        if (!currentItem.isChecked) {
                            val newItem = ShoppingItem(
                                id = (sItems.maxOfOrNull { it.id } ?: 0) + 1,
                                name = currentItem.name,
                                isChecked = false
                            )
                            sItems = sItems + newItem
                        }
                        else {
                            sItems = sItems.map {
                                if (it.id == currentItem.id) it.copy(isChecked = false) else it
                            }
                        }
                    },
                    onItemClick = { currentItem ->
                        sItems = sItems.map {
                            if(it.id == currentItem.id) it.copy(isChecked = !it.isChecked) else it
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onItemAction: (ShoppingItem) -> Unit,
    onItemClick: (ShoppingItem) -> Unit
) {
    val cardColor = if (item.isChecked) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant // Warna yang lebih lembut dari surface
    }

    val textColor = if (item.isChecked) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        onClick = { onItemClick(item) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lingkaran Inisial Nama
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (item.isChecked) MaterialTheme.colorScheme.primary else Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.name.first().uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Nama Item
            Text(
                text = item.name,
                modifier = Modifier.weight(1f),
                color = textColor,
                fontSize = 16.sp
            )
            // Tombol Check / Add (duplikat)
            IconButton(
                onClick = { onItemAction(item) },
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (item.isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(
                    imageVector = if (item.isChecked) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = "Item Action",
                    tint = if (item.isChecked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}