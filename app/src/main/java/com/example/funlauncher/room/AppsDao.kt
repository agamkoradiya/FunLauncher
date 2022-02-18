package com.example.funlauncher.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.funlauncher.room.model.AppDbModel

@Dao
interface AppsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppDbModel(appDbModel: AppDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAppDbModel(vararg appDbModels: AppDbModel)

    @Update
    suspend fun update(appDbModel: AppDbModel)

    @Delete
    suspend fun delete(appDbModel: AppDbModel)

    @Query("SELECT * FROM appDbModel")
    fun getAllAppDbModel(): LiveData<List<AppDbModel>>

}