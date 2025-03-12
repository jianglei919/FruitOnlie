package com.conestoga.group12.fruitonline.model

import java.io.Serializable

data class PaymentInfo(
    var firstName : String = "",
    var lastName : String = "",
    var address : String = "",
    var unitNumber : String = "",
    var city : String = "",
    var state : String = "",
    var postalCode : String = "",
    var phone : String = "",
    var email : String = "",
    var paymentMethod : String = "",
    var cardNumber : String = "",
    var cvv : String = "",
) : Serializable {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "")
}
