package org.prm.drica.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.prm.drica.models.VehiclesModel

@Dao
interface VehiclesDao {
    @Upsert
    suspend fun upsert(item: VehiclesModel)

    @Query("SELECT count(*) FROM VehicleData")
    suspend fun count(): Int

    @Query("SELECT * FROM VehicleData")
    fun getAll(): Flow<List<VehiclesModel>>

    @Query("SELECT * FROM VehicleData where id=:id LIMIT 1")
    suspend fun getVehicleById(id: String): VehiclesModel?

    @Delete
    suspend fun deleteOne(vehicleModel: VehiclesModel)
}