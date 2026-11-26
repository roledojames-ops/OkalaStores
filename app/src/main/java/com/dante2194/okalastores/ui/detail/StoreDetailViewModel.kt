package com.dante2194.okalastores.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dante2194.okalastores.OkalaApp
import com.dante2194.okalastores.data.StoreDetailDto
import com.dante2194.okalastores.data.StoreDto
import com.dante2194.okalastores.data.StoreRepository
import com.dante2194.okalastores.util.friendlyError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StoreDetailUiState(
    val store: StoreDto? = null,
    val detail: StoreDetailDto? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class StoreDetailViewModel(
    private val repository: StoreRepository,
    private val storeId: Int,
) : ViewModel() {

    private val _state = MutableStateFlow(
        StoreDetailUiState(store = repository.cachedStore(storeId)),
    )
    val state: StateFlow<StoreDetailUiState> = _state.asStateFlow()

    private var requestInFlight = false

    init {
        load()
    }

    fun load() {
        if (requestInFlight) return
        requestInFlight = true
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val result = repository.loadDetails(storeId)
                result.fold(
                    onSuccess = { detail ->
                        _state.update {
                            it.copy(
                                detail = detail,
                                isLoading = false,
                                error = null,
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = friendlyError(throwable),
                            )
                        }
                    },
                )
            } finally {
                requestInFlight = false
            }
        }
    }

    companion object {
        fun factory(app: OkalaApp, storeId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StoreDetailViewModel(
                    repository = app.container.repository,
                    storeId = storeId,
                )
            }
        }
    }
}
