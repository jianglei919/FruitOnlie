package com.conestoga.group12.fruitonline.enums

enum class CollectionName(val code: String, val desc: String) {
    PRODUCTS("products", "The collections of product"),
    CART_ITEMS("cartItems", "The collections of cart item"),
    ORDERS("orders", "The collections of user's order list"),
}