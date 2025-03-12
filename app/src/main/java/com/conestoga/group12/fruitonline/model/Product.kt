package com.conestoga.group12.fruitonline.model

import java.io.Serializable

data class Product (
    var productId: String = "",
    var productName: String = "",
    var productImageUrl: String = "",
    var productImageDetailUrl: String = "",
    var price: Double = 0.0,
    var store: String = "",
    var description: String = "",
    var quantity: Int = 1,
) : Serializable {
    constructor() : this("", "", "", "", 0.0, "", "")
}