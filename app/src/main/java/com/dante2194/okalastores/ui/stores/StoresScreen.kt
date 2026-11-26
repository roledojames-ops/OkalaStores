package com.dante2194.okalastores.ui.stores

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dante2194.okalastores.OkalaApp
import com.dante2194.okalastores.R
import com.dante2194.okalastores.data.StoreDto
import com.dante2194.okalastores.ui.components.AboutDialog
import com.dante2194.okalastores.ui.components.CategoryChips
import com.dante2194.okalastores.ui.components.EmptyState
import com.dante2194.okalastores.ui.components.ErrorState
import com.dante2194.okalastores.ui.components.HeroHeader
import com.dante2194.okalastores.ui.components.InlineErrorBanner
import com.dante2194.okalastores.ui.components.OfflineBanner
import com.dante2194.okalastores.ui.components.OkalaLogo
import com.dante2194.okalastores.ui.components.SectionHeader
import com.dante2194.okalastores.ui.components.SearchField
import com.dante2194.okalastores.ui.components.SortRow
import com.dante2194.okalastores.ui.components.StoreCard
import com.dante2194.okalastores.ui.components.StoreCardSkeleton
import com.dante2194.okalastores.util.toPersianDigits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoresScreen(
    onStoreClick: (StoreDto) -> Unit,
) {
    val context = LocalContext.current
    val app = context.applicationContext as OkalaApp
    val viewModel: StoresViewModel = viewModel(factory = StoresViewModel.factory(app))
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showAbout by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onPermissionResult(granted)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    OkalaLogo(
                        modifier = Modifier.padding(start = 16.dp),
                        height = 27.dp,
                    )
                },
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.stores_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp),
                            )
                            Spacer(Modifier.width(3.dp))
                            Text(
                                text = state.locationLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = stringResource(R.string.refresh),
                        )
                    }
                    IconButton(onClick = { showAbout = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = stringResource(R.string.about),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 28.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "hero") {
                    HeroHeader(
                        storeCount = state.allStores.size,
                        partnerCount = state.partnerCount,
                        averageRating = state.averageRating,
                        locationLabel = state.locationLabel,
                        showPermissionCard = !state.hasLocationPermission,
                        onEnableLocation = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ),
                            )
                        },
                    )
                }

                item(key = "search") {
                    SearchField(
                        query = state.query,
                        onQueryChange = viewModel::setQuery,
                    )
                }

                if (state.categories.size > 1) {
                    item(key = "categories") {
                        CategoryChips(
                            categories = state.categories,
                            selectedKey = state.selectedCategory,
                            onSelect = viewModel::setCategory,
                        )
                    }
                }

                item(key = "sort") {
                    SortRow(
                        sort = state.sort,
                        onSortChange = viewModel::setSort,
                    )
                }

                if (state.fromCache) {
                    item(key = "offline") {
                        OfflineBanner(stringResource(R.string.offline_banner))
                    }
                }

                if (state.error != null && state.allStores.isNotEmpty()) {
                    item(key = "inline-error") {
                        InlineErrorBanner(
                            message = state.error ?: "",
                            onRetry = viewModel::refresh,
                        )
                    }
                }

                when {
                    state.isLoading -> {
                        items(count = 3, key = { "skeleton-$it" }) {
                            StoreCardSkeleton()
                        }
                    }

                    state.error != null && state.allStores.isEmpty() -> {
                        item(key = "error") {
                            ErrorState(
                                title = stringResource(R.string.error_title),
                                message = state.error ?: "",
                                onRetry = viewModel::refresh,
                            )
                        }
                    }

                    state.visibleStores.isEmpty() -> {
                        item(key = "empty") {
                            EmptyState(
                                title = stringResource(R.string.empty_title),
                                message = stringResource(R.string.empty_body),
                            )
                        }
                    }

                    else -> {
                        item(key = "list-header") {
                            SectionHeader(
                                title = "نزدیک‌ترین فروشگاه‌ها",
                                subtitle = "${state.visibleStores.size.toPersianDigits()} فروشگاه • ${state.locationLabel}",
                            )
                        }
                        items(
                            items = state.visibleStores,
                            key = { it.storeId },
                        ) { store ->
                            StoreCard(
                                store = store,
                                onClick = { onStoreClick(store) },
                            )
                        }
                        item(key = "footer") {
                            Footer()
                        }
                    }
                }
            }
        }
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

@Composable
private fun Footer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = "داده‌ها از سرویس عمومی فروشگاه‌های اُکالا",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "ساخته‌شده برای خرید راحت‌تر",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
    }
}
