package com.koin.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.koin.domain.user.User

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) {
            onLogout()
        }
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    state.user?.let { user ->
        ProfileContentWithMenu(
            user = user,
            onSave = { name, email, bio ->
                viewModel.onEvent(ProfileUiEvent.Save(name, email, bio, user.avatarUri))
            },
            viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContentWithMenu(
    user: User,
    onSave: (String, String, String?) -> Unit,
    viewModel: ProfileViewModel
) {
    val navController = rememberNavController()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val tabs = listOf("Portfolio", "Watchlist", "Edit Profile")

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (user.avatarUri != null) {
                            AsyncImage(
                                model = user.avatarUri,
                                modifier = Modifier.size(32.dp),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Profile")
                    }
                },
                actions = {
                    var showMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { /* TODO: Navigate to settings */ }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = { viewModel.logout() }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = { /* handled by pager */ }
                    )
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> PortfolioTab()
                    1 -> WatchlistTab()
                    2 -> EditProfileTab(user, onSave)
                }
            }
        }
    }
}

@Composable
private fun PortfolioTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Portfolio", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        // Add portfolio content here
    }
}

@Composable
private fun WatchlistTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Watchlist", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        // Add watchlist content here
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileTab(
    user: User,
    onSave: (String, String, String?) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var bio by remember { mutableStateOf(user.bio ?: "") }
    val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Avatar
        if (user.avatarUri != null) {
            AsyncImage(
                model = user.avatarUri,
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Form fields
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            isError = email.isNotBlank() && !emailValid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio (optional)") },
            singleLine = false,
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSave(name, email, bio) },
            enabled = name.isNotBlank() && emailValid,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Changes")
        }
    }
}
///
//package com.koin.ui.profile
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.AccountCircle
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Snackbar
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import com.koin.domain.user.User
//
//@Composable
//fun ProfileScreen(viewModel: ProfileViewModel, onLogout: () -> Unit) {
//    val state by viewModel.uiState.collectAsState()
//
//    LaunchedEffect(state.loggedOut) {
//        if (state.loggedOut) {
//            onLogout()
//        }
//    }
//
//    if (state.isLoading) {
//        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//        return
//    }
//
//    state.error?.let { msg ->
//        Snackbar { Text(text = msg) }
//    }
//
//    state.user?.let { user ->
//        ProfileContentWithMenu(
//            user = user,
//            onSave = { name, email, bio ->
//                viewModel.onEvent(ProfileUiEvent.Save(name, email, bio, user.avatarUri))
//            },
//            onLogout = {
//                viewModel.logout()
//            }
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun ProfileContentWithMenu(
//    user: User,
//    onSave: (String, String, String?) -> Unit,
//    onLogout: () -> Unit
//) {
//    val navController = androidx.navigation.compose.rememberNavController()
//    androidx.compose.material3.Scaffold(topBar = {
//        androidx.compose.material3.TopAppBar(
//            navigationIcon = {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    androidx.compose.material3.Icon(
//                        Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Back"
//                    )
//                }
//            },
//            title = {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    if (user.avatarUri != null) {
//                        AsyncImage(
//                            model = user.avatarUri,
//                            modifier = Modifier.size(32.dp),
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop
//                        )
//                    } else {
//                        androidx.compose.material3.Icon(
//                            Icons.Default.AccountCircle,
//                            contentDescription = null,
//                            modifier = Modifier.size(32.dp)
//                        )
//                    }
//                    Spacer(Modifier.width(8.dp))
//                    Text("Profile")
//                }
//            },
//            actions = {
//                androidx.compose.material3.DropdownMenu(expanded = false, onDismissRequest = {}) {
//                    androidx.compose.material3.DropdownMenuItem(
//                        text = { Text("Settings") },
//                        onClick = { /*TODO open settings*/ })
//                    androidx.compose.material3.DropdownMenuItem(
//                        text = { Text("Logout") },
//                        onClick = { onLogout() })
//                }
//            })
//    }) { inner ->
//        ProfileContent(user, onSave, Modifier.padding(inner))
//    }
//}
//
//@Composable
//private fun ProfileContent(
//    user: User,
//    onSave: (String, String, String?) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var name by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(user.name) }
//    var email by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(user.email) }
//    var bio by androidx.compose.runtime.remember {
//        androidx.compose.runtime.mutableStateOf(
//            user.bio ?: ""
//        )
//    }
//    val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
//
//    Column(modifier.padding(16.dp)) {
//        // quick links
//        androidx.compose.material3.Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 4.dp)
//        ) {
//            androidx.compose.material3.ListItem(
//                headlineContent = { Text("Portfolio") },
//                trailingContent = {
//                    androidx.compose.material3.Icon(
//                        Icons.AutoMirrored.Filled.ArrowBack,
//                        null
//                    )
//                })
//        }
//        androidx.compose.material3.Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 4.dp)
//        ) {
//            androidx.compose.material3.ListItem(
//                headlineContent = { Text("Watchlist") },
//                trailingContent = {
//                    androidx.compose.material3.Icon(
//                        Icons.AutoMirrored.Filled.ArrowBack,
//                        null
//                    )
//                })
//        }
//        Spacer(Modifier.height(16.dp))
//        // Avatar
//        if (user.avatarUri != null) {
//            AsyncImage(
//                model = user.avatarUri,
//                contentDescription = null,
//                modifier = Modifier.size(96.dp),
//                contentScale = ContentScale.Crop
//            )
//        }
//
//        Spacer(Modifier.height(16.dp))
//        OutlinedTextField(
//            value = name,
//            onValueChange = { name = it },
//            placeholder = { Text("Name") },
//            singleLine = true,
//            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(8.dp))
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            placeholder = { Text("Email") },
//            singleLine = true,
//            isError = email.isNotBlank() && !emailValid,
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Next,
//                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
//            ),
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(8.dp))
//        OutlinedTextField(
//            value = bio,
//            onValueChange = { bio = it },
//            placeholder = { Text("Bio (optional)") },
//            singleLine = false,
//            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
//            modifier = Modifier.fillMaxWidth()
//        )
//        Spacer(Modifier.height(16.dp))
//        Button(enabled = name.isNotBlank() && emailValid, onClick = { onSave(name, email, bio) }) {
//            Text("Save")
//        }
//    }
//
//
//}
