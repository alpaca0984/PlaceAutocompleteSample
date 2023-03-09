package com.example.placeautocompletionsample

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val TAG = "PlaceAutocompletionViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val placesClient: PlacesClient,
) : ViewModel() {

    private val _placesState = MutableStateFlow(PlacesState())
    val placesState: StateFlow<PlacesState> = _placesState.asStateFlow()

    private var token = AutocompleteSessionToken.newInstance()

    fun predictPlace(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setCountries("DE")
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                _placesState.update { state ->
                    state.copy(
                        query = query,
                        predictions = response.autocompletePredictions,
                    )
                }
            }
            .addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: " + exception.message)
                }
            }
    }

    fun fetchPlace(placeId: String) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.ADDRESS_COMPONENTS,
        )
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                _placesState.update { state ->
                    state.copy(
                        addressDetails = response.retrieveAddressDetails(),
                    )
                }
            }
            .addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: " + exception.message)
                }
            }

        token = AutocompleteSessionToken.newInstance()
    }
}

private fun FetchPlaceResponse.retrieveAddressDetails(): AddressDetails? =
    place.addressComponents?.asList()?.let { components ->
        AddressDetails(
            streetNumber = components.find { component ->
                component.types.any { it == "street_number" }
            },
            route = components.find { component ->
                component.types.any { it == "route" }
            },
            postalCode = components.find { component ->
                component.types.any { it == "postal_code" }
            },
            country = components.find { component ->
                component.types.any { it == "country" }
            },
        )
    }