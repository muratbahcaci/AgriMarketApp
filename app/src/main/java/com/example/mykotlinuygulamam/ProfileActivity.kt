package com.example.mykotlinuygulamam

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var profileAdapter: ProfileAdapter
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val imagePickCode = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupViews()
        fetchUserProfile()
        fetchUserProducts()
        supportActionBar?.hide()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.rvProfileProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val urunListesi = mutableListOf<Product>()
        profileAdapter = ProfileAdapter(urunListesi, this::onDeleteProduct, this::onProductClick) // Tıklama olayını ekleyin
        recyclerView.adapter = profileAdapter

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val ivProfileImage: ImageView = findViewById(R.id.ivProfileImage)
        val tvStoreName: TextView = findViewById(R.id.tvStoreName)

        btnBack.setOnClickListener {
            finish()
        }

        ivProfileImage.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun fetchUserProfile() {
        val userId = authInstance.currentUser?.uid ?: return
        val userDocRef = firestoreInstance.collection("Users").document(userId)

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val storeName = document.getString("storeName")
                val profileImageUrl = document.getString("profileImageUrl")

                val tvStoreName: TextView = findViewById(R.id.tvStoreName)
                val ivProfileImage: ImageView = findViewById(R.id.ivProfileImage)

                tvStoreName.text = storeName
                Glide.with(this).load(profileImageUrl).into(ivProfileImage)
            } else {
                val newUser = mapOf(
                    "storeName" to "Yeni Mağaza",
                    "profileImageUrl" to ""
                )
                userDocRef.set(newUser).addOnSuccessListener {
                    val tvStoreName: TextView = findViewById(R.id.tvStoreName)
                    tvStoreName.text = "Yeni Mağaza"
                }
            }
        }.addOnFailureListener { exception ->
            showError("Error loading profile: ${exception.message}")
        }
    }

    private fun fetchUserProducts() {
        val userId = authInstance.currentUser?.uid
        if (userId != null) {
            firestoreInstance.collection("Products")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val productList = mutableListOf<Product>()
                    for (document in documents) {
                        document.toObject(Product::class.java)?.let { product ->
                            productList.add(product)
                        }
                    }
                    profileAdapter.updateData(productList)
                }
                .addOnFailureListener { exception ->
                    showError("Error loading products: ${exception.message}")
                }
        }
    }

    private fun onDeleteProduct(product: Product) {
        ProductUtils.deleteProduct(product.id, {
            showSuccess("Ürün başarıyla silindi.")
            fetchUserProducts() // Update the product list after deletion
        }, { e ->
            showError("Ürün silinirken hata oluştu: ${e.message}")
        })
    }

    private fun onProductClick(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", product.id)
        intent.putExtra("name", product.name)
        intent.putExtra("price", product.price)
        intent.putExtra("description", product.description)
        intent.putExtra("imageUrl", product.imageUrl)
        startActivity(intent)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imagePickCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagePickCode && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                uploadProfileImage(imageUri)
            }
        }
    }

    private fun uploadProfileImage(imageUri: Uri) {
        val userId = authInstance.currentUser?.uid ?: return
        val refStorage = storageInstance.reference.child("profileImages/$userId.jpg")
        refStorage.putFile(imageUri).addOnSuccessListener {
            refStorage.downloadUrl.addOnSuccessListener { uri ->
                val profileImageUrl = uri.toString()
                val userDocRef = firestoreInstance.collection("Users").document(userId)
                userDocRef.update("profileImageUrl", profileImageUrl)
                    .addOnSuccessListener {
                        val ivProfileImage: ImageView = findViewById(R.id.ivProfileImage)
                        Glide.with(this).load(profileImageUrl).into(ivProfileImage)
                        showSuccess("Profil fotoğrafı başarıyla güncellendi.")
                    }
                    .addOnFailureListener { e ->
                        showError("Profil fotoğrafı güncellenirken hata oluştu: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            showError("Profil fotoğrafı yüklenirken hata oluştu: ${e.message}")
        }
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
