package com.example.wmlivebuses.data

import com.example.wmlivebuses.model.Operator
import com.example.wmlivebuses.model.Vehicle
import com.example.wmlivebuses.network.BustimesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VehicleRepository(
    private val api: BustimesApi = BustimesApi.create()
) {
    suspend fun searchVehicles(
        query: String = "",
        operatorId: String? = null,
        limit: Int = 30
    ): Result<List<Vehicle>> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchVehicles(
                search = query.takeIf { it.isNotBlank() },
                operator = operatorId,
                withdrawn = false,
                limit = limit
            )
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchOperators(query: String = ""): Result<List<Operator>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getOperators(search = query.takeIf { it.isNotBlank() }, limit = 40)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
