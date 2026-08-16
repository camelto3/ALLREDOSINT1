package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlists ORDER BY lastScanTimestamp DESC")
    fun getAllWatchlists(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(item: WatchlistEntity): Long

    @Update
    suspend fun updateWatchlist(item: WatchlistEntity)

    @Delete
    suspend fun deleteWatchlist(item: WatchlistEntity)

    @Query("DELETE FROM watchlists")
    suspend fun clearAllWatchlists()
}
