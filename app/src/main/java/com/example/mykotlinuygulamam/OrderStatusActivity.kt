package com.example.mykotlinuygulamam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mykotlinuygulamam.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderStatusActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_status)

        recyclerView = findViewById(R.id.rvOrderList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(mutableListOf())
        recyclerView.adapter = orderAdapter

        fetchOrders()
    }

    private fun fetchOrders() {
        val userId = authInstance.currentUser?.uid ?: return
        firestoreInstance.collection("Orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val orderList = mutableListOf<Order>()
                for (document in documents) {
                    document.toObject(Order::class.java)?.let { order ->
                        orderList.add(order)
                    }
                }
                orderAdapter.updateOrders(orderList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Siparişler yüklenirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
