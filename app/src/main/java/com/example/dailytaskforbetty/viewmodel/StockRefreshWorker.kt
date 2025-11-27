// filePath: dailytaskforbetty/viewmodel/StockRefreshWorker.kt
package com.example.dailytaskforbetty.viewmodel

import android.content.Context
import androidx.work.*
import com.example.dailytaskforbetty.data.AppDatabase
import com.example.dailytaskforbetty.model.ProductEntity
import com.example.dailytaskforbetty.model.StockRefreshCycle
import com.example.dailytaskforbetty.utils.TimeUtils
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StockRefreshWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(context)
        val productDao = database.productDao()

        val zone = ZoneId.of("Asia/Shanghai")
        val currentTime = LocalDateTime.now(zone)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(zone)

        val entities = productDao.observeAllProducts().first()
        entities.forEach { entity ->
            if (entity.refreshCycle == StockRefreshCycle.NONE.name) return@forEach

            val lastRefresh = LocalDateTime.parse(entity.lastRefreshTime, formatter)
            val needRefresh = when (StockRefreshCycle.valueOf(entity.refreshCycle)) {
                StockRefreshCycle.DAILY ->
                    currentTime.toLocalDate().isAfter(lastRefresh.toLocalDate())
                StockRefreshCycle.THREE_DAYS ->
                    ChronoUnit.DAYS.between(lastRefresh.toLocalDate(), currentTime.toLocalDate()) >= 3
                StockRefreshCycle.WEEKLY ->
                    ChronoUnit.WEEKS.between(lastRefresh.toLocalDate(), currentTime.toLocalDate()) >= 1
                StockRefreshCycle.NONE -> false
            }

            if (needRefresh) {
                val lastRefreshDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                    .parse(entity.lastRefreshTime)!!
                val nextRefreshDate = TimeUtils.calculateNextRefreshTime(
                    StockRefreshCycle.valueOf(entity.refreshCycle),
                    lastRefreshDate
                )
                val refreshTimeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                    .format(nextRefreshDate)

                productDao.updateProduct(
                    entity.copy(
                        stock = entity.initialStock,
                        lastRefreshTime = refreshTimeStr
                    )
                )
            }
        }
        return Result.success()
    }
}

/* --------------------- 调度函数 --------------------- */
fun setupStockRefreshWork(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .build()

    val request = PeriodicWorkRequestBuilder<StockRefreshWorker>(6, TimeUnit.HOURS)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "stock_refresh",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
}