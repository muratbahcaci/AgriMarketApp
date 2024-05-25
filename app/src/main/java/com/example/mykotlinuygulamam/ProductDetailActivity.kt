package com.example.mykotlinuygulamam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

@Suppress("NAME_SHADOWING")
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var imageViewFavoriKalp: ImageView
    private lateinit var buttonAddToCart: Button // Button widget için lateinit var tanımı

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.urundetay_product)
        supportActionBar?.hide()
        val imageViewProduct: ImageView = findViewById(R.id.productImage)
        val textViewProductName: TextView = findViewById(R.id.productName)
        val textViewProductPrice: TextView = findViewById(R.id.productPrice)
        val textViewProductDescription: TextView = findViewById(R.id.productDescription)
        imageViewFavoriKalp = findViewById(R.id.buttonFavoriKalp)
        buttonAddToCart = findViewById(R.id.addToCartButton) // findViewById ile Button widget'ı alınıyor

        // Intent'ten gelen verileri al
        val productId = intent.getStringExtra("productId")
        val productName = intent.getStringExtra("name") ?: ""
        val productPrice = intent.getDoubleExtra("price", 0.0)
        val productDescription = intent.getStringExtra("description") ?: ""
        val productImage = intent.getStringExtra("imageUrl") ?: ""

        // UI güncelle
        textViewProductName.text = productName
        textViewProductPrice.text = getString(R.string.product_price, productPrice)
        textViewProductDescription.text = productDescription
        Glide.with(this).load(productImage).into(imageViewProduct)

        // Sepete ekleme butonuna tıklama olayı
        buttonAddToCart.setOnClickListener {
            addToCart(productId, productName, productPrice, productDescription, productImage)
        }

        // Favorilere ekleme butonuna tıklama olayı
        imageViewFavoriKalp.setOnClickListener {
            addToFavorites(productId, productName, productPrice, productDescription, productImage)
        }
    }

    private fun addToCart(productId: String?, productName: String, productPrice: Double, productDescription: String, productImage: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userCartRef = firestore.collection("UserCarts").document(userId).collection("CartItems")
        val newCartItemId = UUID.randomUUID().toString() // Yeni ID oluştur
        val cartItem = hashMapOf(
            "id" to newCartItemId,
            "name" to productName,
            "price" to productPrice,
            "description" to productDescription,
            "imageUrl" to productImage,
            "quantity" to 1  // Miktar varsayılan olarak 1
        )

        userCartRef.document(newCartItemId).set(cartItem) // Yeni ID ile belge oluştur
            .addOnSuccessListener {
                Toast.makeText(this, "$productName sepete eklendi", Toast.LENGTH_SHORT).show()
                val cartIntent = Intent(this, SepeteEkleActivity::class.java)
                startActivity(cartIntent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Sepete eklerken hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addToFavorites(productId: String?, productName: String, productPrice: Double, productDescription: String, productImage: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userFavoritesRef = firestore.collection("UserFavorites").document(userId).collection("Favorites")
        val productId = UUID.randomUUID().toString()
        val favoriteItem = hashMapOf(
            "id" to productId,
            "name" to productName,
            "price" to productPrice,
            "description" to productDescription,
            "imageUrl" to productImage
        )

        userFavoritesRef.document(productId ?: productIdGenerator()).set(favoriteItem)
            .addOnSuccessListener {
                imageViewFavoriKalp.setImageResource(R.drawable.begeniekleem)
                Toast.makeText(this, "$productName favorilere eklendi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Favorilere eklerken hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun productIdGenerator(): String {
        return UUID.randomUUID().toString()
    }
}
