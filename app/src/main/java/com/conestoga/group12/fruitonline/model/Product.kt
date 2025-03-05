package com.conestoga.group12.fruitonline.model

data class Product (
    var productId: String,
    var productName: String,
    var productImageUrl: String,
    var productImageDetailUrl: String,
    var price: Double,
    var store: String,
    var description: String,
    var quantity: Int,
)