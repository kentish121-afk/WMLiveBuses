package com.example.wmlivebuses.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wmlivebuses.model.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WM Live Buses") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Fleet, reg or search") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { vm.search() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )

            Spacer(Modifier = Modifier.height(8.dp))

            // Quick operator chips for West Midlands favourites
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.selectedOperatorId == null,
                    onClick = { vm.selectOperator(null) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = state.selectedOperatorId == "NXWM",
                    onClick = { vm.selectOperator("NXWM") },
                    label = { Text("NXWM") }
                )
                FilterChip(
                    selected = state.selectedOperatorId == "DIAM",
                    onClick = { vm.selectOperator("DIAM") },
                    label = { Text("Diamond") }
                )
                FilterChip(
                    selected = state.selectedOperatorId == "ARMD",
                    onClick = { vm.selectOperator("ARMD") },
                    label = { Text("Arriva") }
                )
            }

            Spacer(Modifier = Modifier.height(12.dp))

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.error?.let { err ->
                Text(err, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = vm::clearError) { Text("Dismiss") }
            }

            if (state.selectedVehicle != null) {
                VehicleDetail(
                    vehicle = state.selectedVehicle!!,
                    onBack = { vm.selectVehicle(null) },
                    onOpenForum = {
                        val q = state.selectedVehicle!!.fleetCode
                            ?: state.selectedVehicle!!.reg
                            ?: ""
                        val url = "https://wmbusphotos.com/forum/index.php?action=search2&search=$q"
                        // Prefer opening the dedicated forum app if installed
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.vehicles, key = { it.id ?: it.slug ?: it.hashCode() }) { v ->
                        VehicleCard(v) { vm.selectVehicle(v) }
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier = Modifier.padding(16.dp)) {
            Text(
                vehicle.displayTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                vehicle.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            vehicle.vehicleType?.let {
                Text(
                    buildString {
                        append(it.name ?: "")
                        if (it.electric == true) append(" • Electric")
                        if (it.doubleDecker == true) append(" • Double deck")
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetail(
    vehicle: Vehicle,
    onBack: () -> Unit,
    onOpenForum: () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                vehicle.displayTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier = Modifier.height(12.dp))

        DetailRow("Operator", vehicle.operator?.name)
        DetailRow("Fleet", vehicle.fleetCode ?: vehicle.fleetNumber?.toString())
        DetailRow("Registration", vehicle.reg)
        DetailRow("Type", vehicle.vehicleType?.name)
        DetailRow("Fuel / Style", listOfNotNull(
            vehicle.vehicleType?.fuel,
            vehicle.vehicleType?.style
        ).joinToString(" • ").ifBlank { null })
        DetailRow("Livery", vehicle.livery?.name)
        DetailRow("Garage", vehicle.garage?.name)
        DetailRow("Branding", vehicle.branding?.takeIf { it.isNotBlank() })
        vehicle.specialFeatures?.takeIf { it.isNotEmpty() }?.let {
            DetailRow("Features", it.joinToString(", "))
        }

        Spacer(Modifier = Modifier.height(24.dp))

        Button(
            onClick = onOpenForum,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.OpenInNew, contentDescription = null)
            Spacer(Modifier = Modifier.width(8.dp))
            Text("Search on WM Bus Photos Forum")
        }

        Text(
            "Tip: Open NextStopRealtime to see live departures at stops this vehicle serves.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun DetailRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(Modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            "$label: ",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(100.dp)
        )
        Text(value)
    }
}
