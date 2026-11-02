package com.nnapps.flymate.data.network

import retrofit2.http.GET
import retrofit2.http.Path

interface AircraftPhotoService {
    @GET("photos/reg/{registration}")
    suspend fun getPhotoByReg(
        @Path("registration") reg: String
    ): retrofit2.Response<PlanespottersResponse>
}