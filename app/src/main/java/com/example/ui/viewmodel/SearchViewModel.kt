package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Property
import com.example.data.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val listings: List<Property>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

/**
 * ViewModel responsible for managing rental search UI state, filter states,
 * and performing queries through the [SearchRepository].
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchRepository = SearchRepository(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCity = MutableStateFlow("All")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedType = MutableStateFlow("All")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private val _maxRent = MutableStateFlow(30000)
    val maxRent: StateFlow<Int> = _maxRent.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Property>>(emptyList())
    val searchResults: StateFlow<List<Property>> = _searchResults.asStateFlow()

    private var searchJob: Job? = null

    init {
        performSearch()
    }

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        debounceSearch()
    }

    fun onCitySelected(city: String) {
        _selectedCity.value = city
        performSearch()
    }

    fun onTypeSelected(type: String) {
        _selectedType.value = type
        performSearch()
    }

    fun onMaxRentChanged(rent: Int) {
        _maxRent.value = rent
        performSearch()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedCity.value = "All"
        _selectedType.value = "All"
        _maxRent.value = 30000
        performSearch()
    }

    private fun debounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // 300ms debounce
            performSearch()
        }
    }

    fun performSearch() {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            val query = _searchQuery.value
            val city = _selectedCity.value
            val type = _selectedType.value
            val maxPrice = _maxRent.value

            val result = searchRepository.searchListings(
                query = query,
                city = city,
                maxRent = maxPrice,
                propertyType = type
            )

            result.fold(
                onSuccess = { properties ->
                    _searchResults.value = properties
                    _uiState.value = SearchUiState.Success(properties)
                },
                onFailure = { throwable ->
                    _uiState.value = SearchUiState.Error(
                        throwable.localizedMessage ?: "Failed to fetch rental listings"
                    )
                }
            )
        }
    }
}
