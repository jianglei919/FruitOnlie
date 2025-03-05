package com.conestoga.group12.fruitonline.utils

import android.util.Patterns

object ValidateUtils {
    const val POSTAL_CODE_REGEX: String = "^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$"

    const val CARD_NUMBER_REGEX: String = "^\\d{13,19}$"

    @JvmStatic
    fun isValidEmail(email: String?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    @JvmStatic
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) {
            return false
        }

        if (!password.matches(".*\\d.*".toRegex())) {
            return false
        }

        if (!password.matches(".*[A-Z].*".toRegex())) {
            return false
        }

        if (!password.matches(".*[a-z].*".toRegex())) {
            return false
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*".toRegex())) {
            return false
        }

        return true
    }

    @JvmStatic
    fun isValidPostalCode(postalCode: String): Boolean {
        return postalCode.matches(POSTAL_CODE_REGEX.toRegex())
    }

    @JvmStatic
    fun isValidCardNumber(cardNumber: String): Boolean {
        return cardNumber.matches(CARD_NUMBER_REGEX.toRegex())
    }

    @JvmStatic
    fun isNumeric(number: String?): Boolean {
        if (number == null || number.isEmpty()) {
            return false
        }
        for (c in number.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false
            }
        }
        return true
    }
}
