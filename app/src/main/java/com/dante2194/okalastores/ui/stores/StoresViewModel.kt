package com.dante2194.okalastores.ui.stores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dante2194.okalastores.OkalaApp
import com.dante2194.okalastores.data.LocationProvider
import com.dante2194.okalastores.data.StoreDto
import com.dante2194.okalastores.data.StoreRepository
import com.dante2194.okalastores.util.etaSortKey
import com.dante2194.okalastores.util.friendlyError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SortMode {
    Distance,
    Rating,
    Popular,
}

data class StoreCategory(
    val key: String,
    val label: String,
    val count: Int,
)

data class StoresUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val allStores: List<StoreDto> = emptyList(),
    val visibleStores: List<StoreDto> = emptyList(),
    val categories: List<StoreCategory> = emptyList(),
    val selectedCategory: String? = null,
    val query: String = "",
    val sort: SortMode = SortMode.Distance,
    val error: String? = null,
    val fromCache: Boolean = false,
    val locationLabel: String = "تهران (موقعیت پیش‌فرض)",
    val hasLocationPermission: Boolean = false,
    val partnerCount: Int = 0,
    val averageRating: Double = 0.0,
    val lastUpdated: Long = 0L,
)

class StoresViewModel(
    private val repository: StoreRepository,
    private val locationProvider: LocationProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(StoresUiState())
    val state: StateFlow<StoresUiState> = _state.asStateFlow()

    private var requestInFlight = false
    private var refreshQueued = false

    init {
        load()
    }

    fun load() {
        if (requestInFlight) {
            refreshQueued = true
            return
        }
        requestInFlight = true
        viewModelScope.launch {
            _state.update { current ->
                current.copy(
                    isLoading = current.allStores.isEmpty(),
                    isRefreshing = current.allStores.isNotEmpty(),
                    error = null,
                )
            }
            try {
                val hasPermission = locationProvider.hasLocationPermission()
                val location = locationProvider.currentLocation()
                val latitude = location?.latitude ?: StoreRepository.DEFAULT_LATITUDE
                val longitude = location?.longitude ?: StoreRepository.DEFAULT_LONGITUDE

                val result = repository.loadNearby(latitude, longitude)
                val data = result.data
                val categories = buildCategories(data.stores)
                val average = data.stores
                    .map { it.rate }
                    .filter { it > 0.0 }
                    .average()
                    .takeIf { !it.isNaN() } ?: 0.0

                _state.update { current ->
                    val selected = current.selectedCategory
                        ?.takeIf { key -> categories.any { it.key == key } }
                    current.copy(
                        isLoading = false,
                        isRefreshing = false,
                        allStores = data.stores,
                        visibleStores = filterAndSort(
                            stores = data.stores,
                            query = current.query,
                            category = selected,
                            sort = current.sort,
                        ),
                        categories = categories,
                        selectedCategory = selected,
                        error = null,
                        fromCache = result.fromCache,
                        locationLabel = when {
                            hasPermission && location != null -> "نزدیک موقعیت شما"
                            hasPermission -> "موقعیت شما در دسترس نیست"
                            else -> "تهران (موقعیت پیش‌فرض)"
                        },
                        hasLocationPermission = hasPermission,
                        partnerCount = data.partnerCount.takeIf { it > 0 }
                            ?: data.stores.mapNotNull { it.partnerName }.distinct().size,
                        averageRating = average,
                        lastUpdated = System.currentTimeMillis(),
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                val hasPermission = locationProvider.hasLocationPermission()
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = friendlyError(throwable),
                        hasLocationPermission = hasPermission,
                        locationLabel = if (hasPermission) "نزدیک موقعیت شما" else "تهران (موقعیت پیش‌فرض)",
                    )
                }
            } finally {
                requestInFlight = false
                if (refreshQueued) {
                    refreshQueued = false
                    load()
                }
            }
        }
    }

    fun refresh() = load()

    fun onPermissionResult(granted: Boolean) {
        _state.update { it.copy(hasLocationPermission = granted) }
        if (granted) load()
    }

    fun setQuery(query: String) {
        _state.update { current ->
            current.copy(
                query = query,
                visibleStores = filterAndSort(
                    stores = current.allStores,
                    query = query,
                    category = current.selectedCategory,
                    sort = current.sort,
                ),
            )
        }
    }

    fun setCategory(category: String?) {
        _state.update { current ->
            current.copy(
                selectedCategory = category,
                visibleStores = filterAndSort(
                    stores = current.allStores,
                    query = current.query,
                    category = category,
                    sort = current.sort,
                ),
            )
        }
    }

    fun setSort(sort: SortMode) {
        _state.update { current ->
            current.copy(
                sort = sort,
                visibleStores = filterAndSort(
                    stores = current.allStores,
                    query = current.query,
                    category = current.selectedCategory,
                    sort = sort,
                ),
            )
        }
    }

    private fun buildCategories(stores: List<StoreDto>): List<StoreCategory> =
        stores
            .groupBy { it.categoryKey }
            .map { (key, grouped) ->
                val label = grouped.firstOrNull()?.storeCategoryName
                    ?: grouped.firstOrNull()?.storeType
                    ?: "سایر"
                StoreCategory(key = key, label = label, count = grouped.size)
            }
            .sortedWith(compareByDescending<StoreCategory> { it.count }.thenBy { it.label })

    private fun filterAndSort(
        stores: List<StoreDto>,
        query: String,
        category: String?,
        sort: SortMode,
    ): List<StoreDto> {
        val needle = query.trim()
        val comparator = when (sort) {
            SortMode.Distance -> compareBy<StoreDto> { it.distance ?: Double.MAX_VALUE }
            SortMode.Rating -> compareByDescending<StoreDto> { it.rate }
            SortMode.Popular -> compareByDescending<StoreDto> { it.csatCount }
        }
        return stores.asSequence()
            .filter { category == null || it.categoryKey == category }
            .filter { store ->
                needle.isBlank() ||
                    store.storeName.contains(needle, ignoreCase = true) ||
                    store.storeType?.contains(needle, ignoreCase = true) == true ||
                    store.storeCategoryName?.contains(needle, ignoreCase = true) == true ||
                    store.partnerName?.contains(needle, ignoreCase = true) == true
            }
            .sortedWith(comparator.thenBy { etaSortKey(it.etaCaption) })
            .toList()
    }

    companion object {
        fun factory(app: OkalaApp): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StoresViewModel(
                    repository = app.container.repository,
                    locationProvider = app.container.locationProvider,
                )
            }
        }
    }
}
