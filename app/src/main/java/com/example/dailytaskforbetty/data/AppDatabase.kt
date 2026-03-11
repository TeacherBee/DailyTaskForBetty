package com.example.dailytaskforbetty.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.dailytaskforbetty.model.*

// 数据库版本号（后续更新表结构时需递增）
@Database(
    entities = [
        TotalReward::class,
        RewardHistory::class,
        TaskEntity::class,
        RedeemedPrizeEntity::class,
        ProductEntity::class,
        RedPacketBalanceEntity::class,
        RedPacketHistoryEntity::class,
        RetroactiveHistoryEntity::class
    ], // 关联的实体类
    version = 7, // 修改entities后（即修改数据库schema），需要升级version，否则会校验失败导致闪退
    exportSchema = false // 简化示例，不导出数据库schema
)
abstract class AppDatabase : RoomDatabase() {
    // 提供Dao实例
    abstract fun rewardDao(): RewardDao
    abstract fun taskDao(): TaskDao
    abstract fun redeemedPrizeDao(): RedeemedPrizeDao
    abstract fun productDao(): ProductDao
    abstract fun redPacketDao(): RedPacketDao
    abstract fun retroactiveHistoryDao(): RetroactiveHistoryDao

    // 单例模式，避免重复创建数据库实例
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // 应用上下文
                    AppDatabase::class.java,
                    "app_database" // 数据库文件名
                )
                // 添加从版本6到7的迁移（添加RetroactiveHistoryEntity表）
                .addMigrations(object : androidx.room.migration.Migration(6, 7) {
                    override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS `retroactive_history` (" +
                                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "`taskId` TEXT NOT NULL, " +
                                    "`taskTitle` TEXT NOT NULL, " +
                                    "`retroactiveDate` INTEGER NOT NULL, " +
                                    "`year` INTEGER NOT NULL, " +
                                    "`month` INTEGER NOT NULL)"
                        )
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}