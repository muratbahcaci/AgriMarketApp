package com.example.mykotlinuygulamam

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderStatusActivity : AppCompatActivity() {

    private lateinit var textViewOrderStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_status)

        textViewOrderStatus = findViewById(R.id.textViewOrderStatus)
        fetchOrderStatus()
    }

    private fun fetchOrderStatus() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("Orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val order = documents.first().toObject(Order::class.java)
                    textViewOrderStatus.text = "Siparişiniz başarıyla alındı. Toplam Tutar: ₺${order.totalPrice}"
                } else {
                    textViewOrderStatus.text = "Herhangi bir sipariş bulunamadı."
                }
            }
            .addOnFailureListener { e ->
                textViewOrderStatus.text = "Sipariş durumu alınamadı: ${e.message}"
            }
    }
}
