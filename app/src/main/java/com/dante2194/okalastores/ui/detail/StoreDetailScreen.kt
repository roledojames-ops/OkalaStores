package com.dante2194.okalastores.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeliveryDining
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dante2194.okalastores.OkalaApp
import com.dante2194.okalastores.R
import com.dante2194.okalastores.data.DeliveryMethodDto
import com.dante2194.okalastores.ui.components.InlineErrorBanner
import com.dante2194.okalastores.ui.components.StoreLabelChip
import com.dante2194.okalastores.ui.components.StoreLogo
import com.dante2194.okalastores.ui.theme.OkalaRed
import com.dante2194.okalastores.ui.theme.OkalaRedDark
import com.dante2194.okalastores.util.formatDistance
import com.dante2194.okalastores.util.formatEta
import com.dante2194.okalastores.util.formatMinimumOrder
import com.dante2194.okalastores.util.formatRadius
import com.dante2194.okalastores.util.formatToman
import com.dante2194.okalastores.util.toPersianDigits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    storeId: Int,
    onBack: () -> Unit,
    onShop: (Int) -> Unit,
) {
    val context = LocalContext.current
    val app = context.applicationContext as OkalaApp
    val viewModel: StoreDetailViewModel = viewModel(
        factory = StoreDetailViewModel.factory(app, storeId),
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    val store = state.store
    val detail = state.detail

    val name = detail?.storeName?.takeIf { it.isNotBlank() }
        ?: store?.storeName?.takeIf { it.isNotBlank() }
        ?: "فروشگاه اُکالا"
    val logo = detail?.logoUrl ?: store?.displayLogo
    val category = detail?.storeCategoryName
        ?: detail?.storeType
        ?: store?.storeCategoryName
        ?: store?.storeType
        ?: "فروشگاه"
    val rate = detail?.rate?.takeIf { it > 0.0 } ?: store?.rate ?: 0.0
    val ratingCount = detail?.csatCount?.takeIf { it > 0 } ?: store?.csatCount ?: 0
    val distance = detail?.distance ?: store?.distance
    val eta = detail?.etaCaption ?: store?.etaCaption
    val closed = detail?.isClosed ?: store?.isClosed ?: false
    val deliveryPrice = detail?.deliveryPrice ?: store?.deliveryPrice
    val packagingPrice = detail?.packagingPrice ?: store?.packagingPrice
    val minimumOrder = detail?.minimumOrder ?: store?.minimumOrder
    val labels = detail?.storeLabels?.takeIf { it.isNotEmpty() }
        ?: store?.storeLabels
        ?: emptyList()
    val methods = detail?.deliveryMethods?.takeIf { it.isNotEmpty() }
        ?: store?.deliveryMethods
        ?: emptyList()
    val address = detail?.storeAddress
    val latitude = detail?.storeLat
    val longitude = detail?.storeLng
    val statusMessage = detail?.storeStatusMessage

    val shopUrl = "https://www.okala.com/store/$storeId"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "بازگشت",
                        )
                    }
                },
                title = {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    IconButton(onClick = { shareStore(context, name, shopUrl) }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.store_share),
                        )
                    }
                    IconButton(onClick = { openBrowser(context, shopUrl) }) {
                        Icon(
                            imageVector = Icons.Rounded.OpenInNew,
                            contentDescription = stringResource(R.string.store_open_browser),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            DetailBottomBar(
                canNavigate = latitude != null && longitude != null,
                onNavigate = { openMap(context, latitude, longitude, name) },
                onShop = { onShop(storeId) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.isLoading) {
                item(key = "loading") {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50)),
                    )
                }
            }

            item(key = "header") {
                DetailHeader(
                    name = name,
                    category = category,
                    logo = logo,
                    rate = rate,
                    ratingCount = ratingCount,
                    distance = distance,
                    closed = closed,
                )
            }

            item(key = "info") {
                InfoGrid(
                    eta = eta,
                    deliveryPrice = deliveryPrice,
                    minimumOrder = minimumOrder,
                    packagingPrice = packagingPrice,
                )
            }

            if (labels.isNotEmpty()) {
                item(key = "labels") {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        items(labels) { label -> StoreLabelChip(label) }
                    }
                }
            }

            if (methods.isNotEmpty()) {
                item(key = "methods") {
                    DeliveryMethodsCard(methods)
                }
            }

            item(key = "address") {
                AddressCard(
                    address = address,
                    canNavigate = latitude != null && longitude != null,
                    onNavigate = { openMap(context, latitude, longitude, name) },
                )
            }

            if (!statusMessage.isNullOrBlank()) {
                item(key = "status") {
                    StatusMessageCard(statusMessage)
                }
            }

            if (state.error != null) {
                item(key = "error") {
                    InlineErrorBanner(
                        message = state.error ?: "",
                        onRetry = viewModel::load,
                    )
                }
            }

            item(key = "footer") {
                Text(
                    text = "داده‌ها از سرویس عمومی فروشگاه‌های اُکالا",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DetailHeader(
    name: String,
    category: String,
    logo: String?,
    rate: Double,
    ratingCount: Int,
    distance: Double?,
    closed: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(OkalaRed, OkalaRedDark),
                ),
            ),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_okala_logo_white),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 4.dp)
                .width(150.dp)
                .alpha(0.1f),
            contentScale = ContentScale.Fit,
        )

        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StoreLogo(url = logo, name = name, size = 82.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(9.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HeaderPill(
                        icon = Icons.Rounded.Star,
                        text = if (rate > 0.0) {
                            "${formatOneDecimal(rate)} (${ratingCount.toPersianDigits()})"
                        } else {
                            "جدید"
                        },
                    )
                    HeaderPill(
                        icon = Icons.Rounded.NearMe,
                        text = formatDistance(distance),
                    )
                }
                Spacer(Modifier.height(6.dp))
                HeaderStatusPill(closed = closed)
            }
        }
    }
}

@Composable
private fun HeaderPill(
    icon: ImageVector,
    text: String,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.16f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun HeaderStatusPill(closed: Boolean) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.16f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (closed) Color(0xFFFFB4AB) else Color(0xFF9CF5D8)),
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = stringResource(if (closed) R.string.store_closed else R.string.store_open),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun InfoGrid(
    eta: String?,
    deliveryPrice: Double?,
    minimumOrder: Double?,
    packagingPrice: Double?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoTile(
                icon = Icons.Rounded.Schedule,
                label = stringResource(R.string.detail_eta),
                value = formatEta(eta),
                modifier = Modifier.weight(1f),
            )
            InfoTile(
                icon = Icons.Rounded.DeliveryDining,
                label = stringResource(R.string.detail_delivery_fee),
                value = formatToman(deliveryPrice, freeLabel = true),
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoTile(
                icon = Icons.Rounded.Payments,
                label = stringResource(R.string.detail_min_order),
                value = formatMinimumOrder(minimumOrder),
                modifier = Modifier.weight(1f),
            )
            InfoTile(
                icon = Icons.Rounded.ShoppingBag,
                label = stringResource(R.string.detail_packaging),
                value = formatToman(packagingPrice),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun InfoTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(Modifier.padding(13.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.09f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DeliveryMethodsCard(methods: List<DeliveryMethodDto>) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.DeliveryDining,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = stringResource(R.string.detail_delivery_methods),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            methods.forEach { method ->
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeliveryDining,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = method.description ?: method.title ?: "ارسال با پیک",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                        )
                        val radius = formatRadius(method.radius ?: method.defaultRadius)
                        if (radius.isNotEmpty()) {
                            Text(
                                text = radius,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    val available = method.status?.equals("Available", ignoreCase = true) == true
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (available) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        },
                    ) {
                        Text(
                            text = if (available) "در دسترس" else "غیرفعال",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (available) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressCard(
    address: String?,
    canNavigate: Boolean,
    onNavigate: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = stringResource(R.string.detail_address),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(7.dp))
            Text(
                text = address?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.detail_address_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (canNavigate) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = onNavigate) {
                    Icon(
                        imageVector = Icons.Rounded.Map,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.store_navigate))
                }
            }
        }
    }
}

@Composable
private fun StatusMessageCard(message: String) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = stringResource(R.string.detail_status),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Composable
private fun DetailBottomBar(
    canNavigate: Boolean,
    onNavigate: () -> Unit,
    onShop: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 14.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = onNavigate,
                enabled = canNavigate,
                modifier = Modifier.height(52.dp),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Map,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.store_navigate))
            }
            Button(
                onClick = onShop,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp),
                )
                Spacer(Modifier.width(7.dp))
                Text(
                    text = stringResource(R.string.store_shop),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun formatOneDecimal(value: Double): String =
    String.format(java.util.Locale.US, "%.1f", value)
        .toPersianDigits()

private fun shareStore(context: Context, name: String, url: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "$name\n$url")
    }
    runCatching {
        context.startActivity(Intent.createChooser(intent, "اشتراک‌گذاری فروشگاه"))
    }
}

private fun openBrowser(context: Context, url: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}

private fun openMap(context: Context, latitude: Double?, longitude: Double?, name: String) {
    if (latitude == null || longitude == null) return
    val label = Uri.encode(name)
    val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
    val fallback = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
    val opened = runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
    }.isSuccess
    if (!opened) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, fallback))
        }
    }
}
