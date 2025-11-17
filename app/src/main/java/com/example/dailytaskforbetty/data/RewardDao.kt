package com.example.dailytaskforbetty.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.dailytaskforbetty.model.RewardHistory
import com.example.dailytaskforbetty.model.TotalReward
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy

@Dao
interface RewardDao {
    // 总积分监听
    @Query("SELECT * FROM total_reward LIMIT 1")
    fun getTotalRewardFlow(): Flow<TotalReward?>

    // 唯一的插入/更新方法（用冲突替换）
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceTotalReward(totalReward: TotalReward)

    // 积分历史相关
    @Query("SELECT * FROM reward_history ORDER BY time DESC")
    fun getRewardHistories(): Flow<List<RewardHistory>>

    @Insert
    suspend fun insertRewardHistory(history: RewardHistory)
}