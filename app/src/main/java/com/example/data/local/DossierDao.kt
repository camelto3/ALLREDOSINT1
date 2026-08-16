package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DossierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DossierDao {
    @Query("SELECT * FROM dossiers ORDER BY createdAt DESC")
    fun getAllDossiers(): Flow<List<DossierEntity>>

    @Query("SELECT * FROM dossiers WHERE id = :id LIMIT 1")
    suspend fun getDossierById(id: Long): DossierEntity?

    @Query("SELECT * FROM dossiers WHERE target LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchDossiers(query: String): Flow<List<DossierEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDossier(dossier: DossierEntity): Long

    @Update
    suspend fun updateDossier(dossier: DossierEntity)

    @Delete
    suspend fun deleteDossier(dossier: DossierEntity)

    @Query("DELETE FROM dossiers")
    suspend fun clearAllDossiers()
}
