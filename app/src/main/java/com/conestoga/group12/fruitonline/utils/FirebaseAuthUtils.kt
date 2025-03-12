package com.conestoga.group12.fruitonline.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseAuthUtils {
    var CURRENT_USER: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    @JvmStatic
    val currentUserId: String?
        get() {
            if (CURRENT_USER != null) {
                return CURRENT_USER!!.uid
            }
            return null
        }

    @JvmStatic
    val currentUserLoginEmail: String?
        get() {
            if (CURRENT_USER != null) {
                return CURRENT_USER!!.email
            }
            return null
        }
}
