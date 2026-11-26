package com.dante2194.okalastores.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dante2194.okalastores.OkalaApp
import com.dante2194.okalastores.ui.detail.StoreDetailScreen
import com.dante2194.okalastores.ui.stores.StoresScreen
import com.dante2194.okalastores.ui.web.ShopWebViewScreen

@Composable
fun OkalaNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "stores",
    ) {
        composable("stores") {
            StoresScreen(
                onStoreClick = { store ->
                    navController.navigate("store/${store.storeId}")
                },
            )
        }

        composable(
            route = "store/{storeId}",
            arguments = listOf(
                navArgument("storeId") { type = NavType.IntType },
            ),
        ) { entry ->
            val storeId = entry.arguments?.getInt("storeId") ?: 0
            StoreDetailScreen(
                storeId = storeId,
                onBack = { navController.popBackStack() },
                onShop = { id -> navController.navigate("shop/$id") },
            )
        }

        composable(
            route = "shop/{storeId}",
            arguments = listOf(
                navArgument("storeId") { type = NavType.IntType },
            ),
        ) { entry ->
            val storeId = entry.arguments?.getInt("storeId") ?: 0
            val app = LocalContext.current.applicationContext as OkalaApp
            val storeName = app.container.repository.cachedStore(storeId)?.storeName
            ShopWebViewScreen(
                storeId = storeId,
                storeName = storeName,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
