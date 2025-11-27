package com.example.dailytaskforbetty.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.dailytaskforbetty.model.RedPacketBalanceEntity
import com.example.dailytaskforbetty.model.RedPacketHistoryEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy

@Dao
interface RedPacketDao {
    // 红包余额相关
    @Query("SELECT * FROM red_packet_balance LIMIT 1")
    fun getRedPacketBalanceFlow(): Flow<RedPacketBalanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceRedPacketBalance(balance: RedPacketBalanceEntity)

    // 红包历史相关
    @Query("SELECT * FROM red_packet_history ORDER BY time DESC")
    fun getRedPacketHistories(): Flow<List<RedPacketHistoryEntity>>

    @Insert
    suspend fun insertRedPacketHistory(history: RedPacketHistoryEntity)
}