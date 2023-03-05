package com.example.placeautocompletionsample

import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.AutocompletePrediction

data class PlacesState(
    val query: String = "",
    val predictions: List<AutocompletePrediction> = emptyList(),
    val addressDetails: AddressDetails? = null,
)

data class AddressDetails(
    val streetNumber: AddressComponent?,
    val route: AddressComponent?,
    val postalCode: AddressComponent?,
    val country: AddressComponent?,
)
