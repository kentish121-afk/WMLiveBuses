package com.example.wmlivebuses.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wmlivebuses.data.VehicleRepository
import com.example.wmlivebuses.model.Operator
import com.example.wmlivebuses.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val query: String = "",
    val vehicles: List<Vehicle> = emptyList(),
    val operators: List<Operator> = emptyList(),
    val selectedOperatorId: String? = null,
    val selectedVehicle: Vehicle? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class MainViewModel(
    private val repository: VehicleRepository = VehicleRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        loadPopularWestMidlands()
    }

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q) }
    }

    fun search() {
        val q = _state.value.query
        val op = _state.value.selectedOperatorId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.searchVehicles(q, op)
                .onSuccess { list ->
                    _state.update { it.copy(vehicles = list, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load vehicles"
                        )
                    }
                }
        }
    }

    fun selectOperator(id: String?) {
        _state.update { it.copy(selectedOperatorId = id) }
        search()
    }

    fun selectVehicle(v: Vehicle?) {
        _state.update { it.copy(selectedVehicle = v) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun loadPopularWestMidlands() {
        // Pre-load a useful starting set (NXWM / Diamond-ish search)
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.searchVehicles("", operatorId = null, limit = 20)
                .onSuccess { list ->
                    _state.update { it.copy(vehicles = list, isLoading = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }
}
