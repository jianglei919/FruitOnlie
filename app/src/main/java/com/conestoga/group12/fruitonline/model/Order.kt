package com.conestoga.group12.fruitonline.model

import com.conestoga.group12.fruitonline.enums.OrderStatusType
import java.io.Serializable

data class Order(
    var orderId: String = "",
    var loginEmail: String = "",
    var status: String = OrderStatusType.PENDING.type,
    var orderTime: Long = System.currentTimeMillis(),
    var totalPrice: Double = 0.0,
    var paymentInfo: PaymentInfo? = null,
    var productItemList: List<CartItem> = emptyList(),
) : Serializable {
    constructor() : this("", "", "", 0, 0.0, null, emptyList())
}
