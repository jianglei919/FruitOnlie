package com.conestoga.group12.fruitonline.enums

enum class OrderStatusType(val code: Int, val type: String) {
    PENDING(1, "Pending"),  // 等待处理
    PROCESSING(2, "Processing"),  // 正在处理中
    SHIPPED(3, "Shipped"),  // 已发货
    DELIVERED(4, "Delivered"),  // 已送达
    CANCELLED(5, "Cancelled"),  // 已取消
    RETURNED(6, "Returned") // 已退货
}
