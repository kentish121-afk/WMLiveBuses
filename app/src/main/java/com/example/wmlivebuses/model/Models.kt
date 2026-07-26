package com.example.wmlivebuses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleListResponse(
    val count: Int = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Vehicle> = emptyList()
)

@Serializable
data class Vehicle(
    val id: Long? = null,
    val slug: String? = null,
    @SerialName("fleet_number") val fleetNumber: Int? = null,
    @SerialName("fleet_code") val fleetCode: String? = null,
    val reg: String? = null,
    @SerialName("previous_reg") val previousReg: String? = null,
    @SerialName("vehicle_type") val vehicleType: VehicleType? = null,
    val livery: Livery? = null,
    val branding: String? = null,
    val operator: Operator? = null,
    val garage: Garage? = null,
    val name: String? = null,
    val notes: String? = null,
    val withdrawn: Boolean? = null,
    @SerialName("special_features") val specialFeatures: List<String>? = null
) {
    val displayTitle: String
        get() = buildString {
            val code = fleetCode ?: fleetNumber?.toString()
            if (!code.isNullOrBlank()) append(code)
            if (!reg.isNullOrBlank()) {
                if (isNotEmpty()) append(" • ")
                append(reg)
            }
            if (isEmpty()) append(slug ?: "Unknown")
        }

    val subtitle: String
        get() = listOfNotNull(
            operator?.name,
            vehicleType?.name,
            garage?.name
        ).joinToString(" • ")
}

@Serializable
data class VehicleType(
    val id: Int? = null,
    val name: String? = null,
    val style: String? = null,
    val fuel: String? = null,
    @SerialName("double_decker") val doubleDecker: Boolean? = null,
    val coach: Boolean? = null,
    val electric: Boolean? = null
)

@Serializable
data class Livery(
    val id: Int? = null,
    val name: String? = null,
    val left: String? = null,
    val right: String? = null
)

@Serializable
data class Operator(
    val id: String? = null,
    val slug: String? = null,
    val name: String? = null,
    val parent: String? = null
)

@Serializable
data class Garage(
    val id: Int? = null,
    val code: String? = null,
    val name: String? = null
)

@Serializable
data class OperatorListResponse(
    val count: Int = 0,
    val results: List<Operator> = emptyList()
)
