package com.example.placeautocompletionsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.placeautocompletionsample.ui.theme.PlaceAutocompletionSampleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaceAutocompletionSampleTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun MainScreen(
    viewModel: MainViewModel,
) {
    val state by viewModel.placesState

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.predictPlace(query = it) },
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.predictions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                        )
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(state.predictions) { index, prediction ->
                        if (index > 0)
                            Divider()

                        Column(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clickable {
                                    viewModel.fetchPlace(placeId = prediction.placeId)
                                },
                        ) {
                            Text(
                                text = prediction.getPrimaryText(null).toString(),
                                style = MaterialTheme.typography.body2,
                            )

                            Text(
                                text = prediction.getSecondaryText(null).toString(),
                                style = MaterialTheme.typography.caption,
                            )
                        }
                    }
                }
            }

            state.addressDetails?.let { details ->
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(text = "Street Number: ${details.streetNumber?.name}")
                    Text(text = "Street: ${details.route?.name}")
                    Text(text = "Postal Code: ${details.postalCode?.name}")
                    Text(text = "Country: ${details.country?.name}")
                }
            }
        }
    }
}

