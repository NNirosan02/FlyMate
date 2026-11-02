package com.nnapps.flymate.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface FlightApiService {
    @GET("flights/number/{flightNumber}")
    suspend fun getFlightByNumber(
        @Path("flightNumber") flightNumber: String
    ): retrofit2.Response<List<AeroFlightItem>>
}