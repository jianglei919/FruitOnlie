package com.conestoga.group12.fruitonline

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ThankYouActivity : AppCompatActivity() {
    private lateinit var backToProductsButton: Button
    private lateinit var viewOrdersButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)

        backToProductsButton = findViewById(R.id.backToProductsButton)
        viewOrdersButton = findViewById(R.id.viewOrdersButton)

        // 返回到产品页面
        backToProductsButton.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@ThankYouActivity, ProductActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        })

        // 查看我的订单
        viewOrdersButton.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@ThankYouActivity, OrderListActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}