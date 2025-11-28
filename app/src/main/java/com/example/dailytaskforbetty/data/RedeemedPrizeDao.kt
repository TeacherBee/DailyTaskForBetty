package com.example.dailytaskforbetty.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.dailytaskforbetty.model.RedeemedPrizeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RedeemedPrizeDao {
    // 监听所有已兑换奖品（自动更新）
    @Query("SELECT * FROM redeemed_prizes ORDER BY redeemTime DESC")
    fun observeAllRedeemedPrizes(): Flow<List<RedeemedPrizeEntity>>

    // 插入新的已兑换奖品
    @Insert
    suspend fun insertRedeemedPrize(entity: RedeemedPrizeEntity)

    // 更新奖品状态
    @Update
    suspend fun updateRedeemedPrize(entity: RedeemedPrizeEntity)

    @Query("SELECT * FROM redeemed_prizes WHERE id = :id")
    suspend fun getRedeemedPrizeById(id: String): RedeemedPrizeEntity?
}