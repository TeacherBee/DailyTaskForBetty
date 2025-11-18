// filePath：dailytaskforbetty/ui/screens/TimeScreen.kt
package com.example.dailytaskforbetty.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape          // 可选，若用圆形
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailytaskforbetty.ui.theme.Pink40
import com.example.dailytaskforbetty.ui.theme.Pink80
import com.example.dailytaskforbetty.viewmodel.TimeViewModel

@Composable
fun TimeScreen(
    viewModel: TimeViewModel = viewModel()
) {
    val currentTime by viewModel.currentTime.collectAsState()
    val loveDays by viewModel.loveDays.collectAsState()

    // 爱心缩放动画状态
    val scale by animateFloatAsState(
        targetValue = if (loveDays > 0) 1.0f else 0.9f,
        label = "heart scale animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 当前时间卡片
        Box(
            modifier = Modifier
                .padding(bottom = 40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(20.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "当前时间",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = currentTime,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 恋爱天数标题
        Text(
            text = "我们已经相恋",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 爱心容器（包含天数）
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(250.dp)
        ) {
            // 自定义爱心绘制
            Canvas(modifier = Modifier.size(250.dp)) {
                val heartPath = Path().apply {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radius = size.width / 4

                    // 绘制爱心路径（调整曲线参数使爱心更饱满）
                    moveTo(centerX, centerY - radius / 2)
                    cubicTo(
                        centerX + radius * 1.2f, centerY - radius * 1.8f,
                        centerX + radius * 2.2f, centerY - radius * 0.3f,
                        centerX, centerY + radius * 1.1f
                    )
                    cubicTo(
                        centerX - radius * 2.2f, centerY - radius * 0.3f,
                        centerX - radius * 1.2f, centerY - radius * 1.8f,
                        centerX, centerY - radius / 2
                    )
                }

                // 绘制渐变爱心
                drawPath(
                    path = heartPath,
                    color = Pink40.copy(alpha = 0.9f),
                    style = Fill
                )
                // 爱心边框
                drawPath(
                    path = heartPath,
                    color = Pink80,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.dp.toPx())
                )
            }

            // 恋爱天数文字
            Text(
                text = "$loveDays 天",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.scale(scale)
            )
        }

        // 恋爱起始日期提示
        Text(
            text = "始于 2023.03.14",
            fontSize = 20.sp, // 字体调大
            fontWeight = FontWeight.Bold,
            color = Pink80, // 使用主题粉色系增强艺术感
            modifier = Modifier
                .padding(top = 32.dp)
                .background(
                    color = Color.Transparent,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimeScreenPreview() {
    MaterialTheme {
        TimeScreen()
    }
}