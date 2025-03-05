package com.conestoga.group12.fruitonline.model

data class Order(
    val orderId: String,
    val loginEmail: String,
    val status: String,
    val orderTime: Long,
    val totalPrice: Double,
    val paymentInfo: PaymentInfo,
    val productItemList: List<CartItem>,
)
