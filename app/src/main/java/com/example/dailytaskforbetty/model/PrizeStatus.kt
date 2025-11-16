package com.example.dailytaskforbetty.model

// 奖品状态：待发货→已发货→已收货
enum class PrizeStatus {
    PENDING_SHIPMENT, // 待发货
    SHIPPED,          // 已发货
    RECEIVED          // 已收货
}