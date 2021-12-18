package com.oucs.mystores.repo.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.oucs.mystores.model.entities.Store
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity")
    fun getStoreList():LiveData<MutableList<Store>>
    @Query("SELECT * FROM StoreEntity WHERE id = :id")
    fun getStoreById(id:Long): LiveData<Store>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStore(store: Store)
    @Update
    suspend fun updateStore(store: Store):Int
    @Delete
    suspend fun deleteStore(store: Store):Int
}