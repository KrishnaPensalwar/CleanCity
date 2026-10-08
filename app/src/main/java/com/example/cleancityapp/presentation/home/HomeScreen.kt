package com.example.cleancityapp.presentation.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.cleancityapp.data.remote.UserDto
import com.example.cleancityapp.presentation.home.sections.ActivityItem
import com.example.cleancityapp.presentation.home.sections.StatBubble
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    user: UserDto?,
    onNavigateToReport: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.refreshHome(force = true)
        }
    }

    val displayUser = state.currentUser ?: user

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState),
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Your impact",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatBubble(
                        label = "Rank",
                        value = "${state.userRank?.currentUser?.rank ?: "—"}",
                        modifier = Modifier.weight(1f),
                    )
                    StatBubble(
                        label = "Reports",
                        value = "${displayUser?.reportsFiled ?: state.userReports.size}",
                        modifier = Modifier.weight(1f),
                    )
                    StatBubble(
                        label = "Resolved",
                        value = "${displayUser?.reportsResolved ?: 0}",
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Recent activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                state.userReports.take(5).forEach { report ->
                    ActivityItem(report.description, "Recently updated", report.status)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.userReports.isEmpty() && !state.isLoading) {
                    ActivityItem("No reports yet", "Tap + to file your first report", "Clean")
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (state.isLoading && state.userReports.isEmpty() && state.currentUser == null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

//        PhotoApp(photos = photos)
    }
}


data class Photo(
    val id: Int,
    val title: String,
    val imageUrl: String
)

private val photos = listOf(
    Photo(
        1,
        "Mountain",
        "https://images.unsplash.com/photo-1500534623283-312aade485b7"
    ),
    Photo(
        2,
        "Forest",
        "https://images.unsplash.com/photo-1448375240586-882707db888b"
    ),
    Photo(
        3,
        "Ocean",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e"
    )
)

private const val PhotoListRoute = "photos"
private const val PhotoDetailRoute = "photos/{id}"

@OptIn(ExperimentalSharedTransitionApi::class)
private val ImageBoundsTransform = BoundsTransform { _, _ ->
    tween(durationMillis = 600, easing = FastOutSlowInEasing)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PhotoApp(photos: List<Photo>) {
    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = PhotoListRoute,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable(PhotoListRoute) {
                PhotoList(
                    photos = photos,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onPhotoClick = { photo ->
                        navController.navigate("photos/${photo.id}")
                    },
                )
            }
            composable(
                route = PhotoDetailRoute,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { entry ->
                val id = entry.arguments?.getInt("id")
                val photo = photos.find { it.id == id } ?: return@composable
                PhotoDetail(
                    photo = photo,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhotoList(
    photos: List<Photo>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPhotoClick: (Photo) -> Unit,
) {
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items = photos, key = { it.id }) { photo ->
                PhotoItem(
                    photo = photo,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onClick = { onPhotoClick(photo) },
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhotoItem(
    photo: Photo,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit,
) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        ) {
            Column {
                AsyncImage(
                    model = photo.imageUrl,
                    contentDescription = photo.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "image-${photo.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = ImageBoundsTransform,
                        ),
                )
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun PhotoDetail(
    photo: Photo,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBack: () -> Unit,
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .clickable(onClick = onBack),
        ) {
            AsyncImage(
                model = photo.imageUrl,
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "image-${photo.id}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = ImageBoundsTransform,
                    ),
            )
            Text(
                text = photo.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}