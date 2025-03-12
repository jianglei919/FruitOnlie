package com.conestoga.group12.fruitonline

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.conestoga.group12.fruitonline.enums.CollectionName
import com.conestoga.group12.fruitonline.enums.OrderStatusType
import com.conestoga.group12.fruitonline.manager.CartManager.Companion.instance
import com.conestoga.group12.fruitonline.model.CartItem
import com.conestoga.group12.fruitonline.model.Order
import com.conestoga.group12.fruitonline.model.PaymentInfo
import com.conestoga.group12.fruitonline.utils.FirebaseAuthUtils.currentUserId
import com.conestoga.group12.fruitonline.utils.FirebaseAuthUtils.currentUserLoginEmail
import com.conestoga.group12.fruitonline.utils.ValidateUtils.isNumeric
import com.conestoga.group12.fruitonline.utils.ValidateUtils.isValidCardNumber
import com.conestoga.group12.fruitonline.utils.ValidateUtils.isValidPostalCode
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase

class CheckoutActivity : AppCompatActivity() {
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var unitNumberEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var postalCodeEditText: EditText
    private lateinit var cardNumberEditText: EditText
    private lateinit var cvvEditText: EditText
    private lateinit var paymentOptionsSpinner: Spinner
    private lateinit var submitOrderButton: Button

    private lateinit var cartItemList: List<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        firstNameEditText = findViewById(R.id.firstName)
        lastNameEditText = findViewById(R.id.lastName)
        addressEditText = findViewById(R.id.address)
        unitNumberEditText = findViewById(R.id.unitNumber)
        cityEditText = findViewById(R.id.city)
        stateEditText = findViewById(R.id.state)
        postalCodeEditText = findViewById(R.id.postalCode)
        phoneEditText = findViewById(R.id.phone)
        emailEditText = findViewById(R.id.email)
        cardNumberEditText = findViewById(R.id.cardNumber)
        cvvEditText = findViewById(R.id.cvv)
        paymentOptionsSpinner = findViewById(R.id.paymentOptions)
        submitOrderButton = findViewById(R.id.submitOrderButton)

        val adapter = ArrayAdapter.createFromResource(
            this, R.array.payment_methods, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        paymentOptionsSpinner.setAdapter(adapter)

        cartItemList = intent.getSerializableExtra( "cartItems") as? List<CartItem> ?: emptyList()

        submitOrderButton.setOnClickListener(View.OnClickListener { v: View? ->
            if (validateFields()) {
                processOrder()
            }
        })
    }

    private fun validateFields(): Boolean {
        if (TextUtils.isEmpty(firstNameEditText!!.text)) {
            firstNameEditText!!.error = "First name is required"
            return false
        }
        if (TextUtils.isEmpty(lastNameEditText!!.text)) {
            lastNameEditText!!.error = "Last name is required"
            return false
        }
        if (TextUtils.isEmpty(addressEditText!!.text)) {
            addressEditText!!.error = "Address is required"
            return false
        }
        if (TextUtils.isEmpty(cityEditText!!.text)) {
            cityEditText!!.error = "City is required"
            return false
        }
        if (TextUtils.isEmpty(stateEditText!!.text)) {
            stateEditText!!.error = "State is required"
            return false
        }
        val postalCode = postalCodeEditText!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(postalCode)) {
            postalCodeEditText!!.error = "Postal code is required"
            return false
        }
        if (!isValidPostalCode(postalCode)) {
            postalCodeEditText!!.error = "Invalid postal code. Please enter a valid postal code."
            return false
        }
        if (TextUtils.isEmpty(phoneEditText!!.text)) {
            phoneEditText!!.error = "Phone number is required"
            return false
        }
        val email = emailEditText!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(email)) {
            emailEditText!!.error = "Email is required"
            return false
        }
        if (!isValidEmail(email)) {
            emailEditText!!.error = "Invalid email address. Please enter a valid email."
            return false
        }
        val cardNumber = cardNumberEditText!!.text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(cardNumber)) {
            cardNumberEditText!!.error = "Card number is required"
            return false
        }
        if (!isValidCardNumber(cardNumber)) {
            cardNumberEditText!!.error =
                "Invalid card number. Please enter 13-19 digit card number."
            return false
        }

        val cvv = cvvEditText!!.text.toString()
        if (TextUtils.isEmpty(cvv)) {
            cvvEditText!!.error = "CVV is required"
            return false
        }
        if (!isNumeric(cvv)) {
            cvvEditText!!.error = "CVV must be 3 digit numbers"
            return false
        }
        return true
    }

    private fun processOrder() {
        val userId = currentUserId
        val loginEmail = currentUserLoginEmail

        if (TextUtils.isEmpty(userId) && TextUtils.isEmpty(loginEmail)) {
            Toast.makeText(this, "Failed to get current user. Try again.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val paymentInfo = PaymentInfo(
            firstNameEditText!!.text.toString().trim { it <= ' ' },
            lastNameEditText!!.text.toString().trim { it <= ' ' },
            addressEditText!!.text.toString().trim { it <= ' ' },
            unitNumberEditText!!.text.toString().trim { it <= ' ' },
            cityEditText!!.text.toString().trim { it <= ' ' },
            stateEditText!!.text.toString().trim { it <= ' ' },
            postalCodeEditText!!.text.toString().trim { it <= ' ' },
            phoneEditText!!.text.toString().trim { it <= ' ' },
            emailEditText!!.text.toString().trim { it <= ' ' },
            paymentOptionsSpinner!!.selectedItem.toString(),
            cardNumberEditText!!.text.toString().trim { it <= ' ' },
            cvvEditText!!.text.toString().trim { it <= ' ' })


        val ordersRef = FirebaseDatabase.getInstance().getReference(CollectionName.ORDERS.code)

        val orderId = ordersRef.push().key
        if (orderId != null) {
            val order = Order()
            order.orderId = orderId
            if (loginEmail != null) {
                order.loginEmail = loginEmail
            }
            order.orderTime = System.currentTimeMillis()
            order.status = OrderStatusType.PENDING.type
            order.totalPrice = instance!!.totalPrice

            cartItemList = instance!!.getCartItems()

            order.paymentInfo = paymentInfo
            order.productItemList = cartItemList

            val currentUserId = currentUserId
            if (!TextUtils.isEmpty(currentUserId)) {
                ordersRef.child(currentUserId!!).child(orderId).setValue(order)
                    .addOnCompleteListener { task: Task<Void?> ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT)
                                .show()

                            // Clear the cart and navigate to ThankYouActivity
                            instance!!.clearCart()

                            val cartItemRef = FirebaseDatabase.getInstance()
                                .getReference(CollectionName.CART_ITEMS.code)
                            cartItemRef.child(currentUserId).removeValue()
                                .addOnCompleteListener { task1: Task<Void?>? ->
                                    if (task.isSuccessful) {
                                        Log.i(
                                            TAG,
                                            "Successful to clear all cart item. currentUserId=$currentUserId"
                                        )
                                    } else {
                                        Log.i(
                                            TAG,
                                            "Failed to clear all cart item. currentUserId=$currentUserId"
                                        )
                                    }
                                }

                            val intent = Intent(this@CheckoutActivity, ThankYouActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to place order. Try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Log.w(TAG, "Failed to get current user id. currentId=$currentUserId")
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    companion object {
        private const val TAG = "CheckoutActivity"
    }
}