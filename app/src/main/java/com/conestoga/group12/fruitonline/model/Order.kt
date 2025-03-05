package com.conestoga.group12.fruitonline.model

import java.io.Serializable

data class Order(
    var orderId: String,
    var loginEmail: String,
    var status: String,
    var orderTime: Long,
    var totalPrice: Double,
    var paymentInfo: PaymentInfo,
    var productItemList: List<CartItem>,
) : Serializable
