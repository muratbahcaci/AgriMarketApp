package com.example.mykotlinuygulamam

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

data class Urun(
    var id: String = "",
    var name: String = "",
    var info: String = "",
    var price: Double = 0.0,
    var imageUrl: String = "",
    var storeName: String = "",
    var category: String = ""
)

class MainActivity3 : AppCompatActivity(), CategoriesBottomSheetDialogFragment.CategorySelectListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var urunAdapter: UrunAdapter
    private lateinit var addButton: ImageButton
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val IMAGE_PICK_CODE = 1001
    private val PERMISSION_CODE = 1002
    private lateinit var dbHelper: DBHelper
    val categories = arrayOf("Tekerlekler", "Haplar", "Fareler", "Bilgisayarlar", "Peçeteler", "Kalemler", "Defterler", "Soğutucular", "Kemer", "Cüzdanlar", "Okul İhtiyaçları", "Masalar", "Ağız Bakım Ürünleri", "Telefonlar", "Okuma Kitapları", "Tarım İhtiyaçları", "Tabletler", "Fare Bezi", "Hayvan İlaçları", "Parfümler", "Battaniyeler", "Seccadeler", "Araç Süsleri", "Işıklar", "Kablolar", "Kulaklıklar", "Klimalar", "Televizyonlar")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        dbHelper = DBHelper(this)
        setupViews()
        addButton = findViewById(R.id.btnAdd)
        addButton.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
            }
        }
        fetchProductsFromFirebase()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.rvProductList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val urunListesi = mutableListOf<Urun>()
        urunAdapter = UrunAdapter(this, urunListesi)
        recyclerView.adapter = urunAdapter

        val btnProfile: ImageButton = findViewById(R.id.btnProfile)
        val btnCategories: ImageButton = findViewById(R.id.btnCategories)
        val btnCart: ImageButton = findViewById(R.id.btnCart)
        val btnFavorites: ImageButton = findViewById(R.id.btnFavorites)

        btnCategories.setOnClickListener {
            val categoriesBottomSheetDialogFragment = CategoriesBottomSheetDialogFragment()
            categoriesBottomSheetDialogFragment.show(supportFragmentManager, "CategoriesBottomSheet")
        }

        btnFavorites.setOnClickListener {
            val intent = Intent(this, FavoriUrunlerActivity::class.java)
            startActivity(intent)
        }

        btnCart.setOnClickListener {
            val intent = Intent(this, SepeteEkleActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCategorySelected(category: String) {
        Toast.makeText(this, "Seçilen kategori: $category", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                showAddProductDialog(imageUri)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showAddProductDialog(imageUri: Uri) {
        val view = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val nameEditText: EditText = view.findViewById(R.id.editTextProductName)
        val priceEditText: EditText = view.findViewById(R.id.editTextProductPrice)
        val infoEditText: EditText = view.findViewById(R.id.editTextProductInfo)
        val storeNameEditText: EditText = view.findViewById(R.id.editTextStoreName)
        val categoryEditText: EditText = view.findViewById(R.id.editTextProductCategory)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Ekle") { dialog, _ ->
                val name = nameEditText.text.toString().trim()
                val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0
                val info = infoEditText.text.toString().trim()
                val storeName = storeNameEditText.text.toString().trim()
                val category = categoryEditText.text.toString().trim()

                if (name.isEmpty() || price == 0.0 || storeName.isEmpty() || category.isEmpty()) {
                    Toast.makeText(this, "Ürün adı, fiyatı, mağaza adı ve kategori boş bırakılamaz.", Toast.LENGTH_SHORT).show()
                } else {
                    val newProduct = Urun(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        price = price,
                        info = info,
                        category = category,
                        imageUrl = imageUri.toString()
                    )
                    uploadImageToFirebase(newProduct)
                }
                dialog.dismiss()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun uploadImageToFirebase(urunler: Urun) {
        val refStorage = storageInstance.reference.child("images/${UUID.randomUUID()}.jpg")
        refStorage.putFile(Uri.parse(urunler.imageUrl)).addOnSuccessListener { taskSnapshot ->
            refStorage.downloadUrl.addOnSuccessListener { uri ->
                val updatedProduct = urunler.copy(imageUrl = uri.toString())
                saveProductToFirestore(updatedProduct)
            }
        }.addOnFailureListener { e ->
            showError("Resim yüklenirken bir hata oluştu: ${e.message}")
        }
    }

    private fun saveProductToFirestore(urunler: Urun) {
        val userId = authInstance.currentUser?.uid ?: return

        val productData = mapOf(
            "id" to urunler.id,
            "name" to urunler.name,
            "info" to urunler.info,
            "price" to urunler.price,
            "imageUrl" to urunler.imageUrl,
            "storeName" to urunler.storeName,
            "category" to urunler.category,
            "userId" to userId
        )

        firestoreInstance.collection("Products").document(urunler.id).set(productData).addOnSuccessListener {
            showSuccess("Ürün başarıyla eklendi.")
        }.addOnFailureListener { e ->
            showError("Ürün eklenirken hata oluştu: ${e.message}")
        }
    }

    private fun fetchProductsFromFirebase() {
        firestoreInstance.collection("Products").get().addOnSuccessListener { documents ->
            val productList = mutableListOf<Urun>()
            for (document in documents) {
                document.toObject(Urun::class.java)?.let { product ->
                    productList.add(product)
                }
            }
            urunAdapter.updateUrunler(productList)
        }.addOnFailureListener { exception ->
            showError("Error loading products: ${exception.message}")
        }
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
    }
}
