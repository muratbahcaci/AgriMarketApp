package com.example.mykotlinuygulamam

import CartItem
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SepeteEkleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonCompleteOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sepete_ekle)
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.recyclerViewCartItems)
        buttonCompleteOrder = findViewById(R.id.buttonCompleteOrder)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            fetchCartItems(currentUserId)
        } else {
            Toast.makeText(this, "Kullanıcı girişi yapılmalıdır.", Toast.LENGTH_LONG).show()
        }

        buttonCompleteOrder.setOnClickListener {
            completeOrder()
        }
    }

    private fun fetchCartItems(userId: String) {
        val userCartRef = FirebaseFirestore.getInstance().collection("UserCarts").document(userId)
        userCartRef.collection("CartItems").get().addOnSuccessListener { documents ->
            val cartItems = documents.toObjects(CartItem::class.java)
            setupRecyclerView(cartItems)
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Sepet ürünleri yüklenirken hata oluştu: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView(cartItems: List<CartItem>) {
        val adapter = CartItemAdapter(cartItems, { cartItem ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("productId", cartItem.id) // 'id' alanınızı buraya ekleyin
                putExtra("name", cartItem.name)
                putExtra("price", cartItem.price)
                putExtra("description", cartItem.description)
                putExtra("imageUrl", cartItem.imageUrl)
            }
            startActivity(intent)
        }, { cartItem ->
            deleteCartItem(cartItem)
        })
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }

    private fun deleteCartItem(cartItem: CartItem) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val cartItemRef = FirebaseFirestore.getInstance()
                .collection("UserCarts")
                .document(currentUserId)
                .collection("CartItems")
                .document(cartItem.id) // Burada 'id' alanınızı kullanın

            cartItemRef.delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Ürün sepetten silindi", Toast.LENGTH_SHORT).show()
                    fetchCartItems(currentUserId) // Sepeti yeniden yükle
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ürün silinirken hata oluştu: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun completeOrder() {
        Toast.makeText(this, "Siparişiniz alındı", Toast.LENGTH_SHORT).show()
        // Burada gerçek sipariş tamamlama işlemlerini gerçekleştirin.
    }
}
