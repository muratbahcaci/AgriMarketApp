package com.example.mykotlinuygulamam

import UrunAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.widget.SearchView
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
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storageInstance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    private val authInstance: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val imagePickCode = 1001
    private val permissionCode = 1002
    private lateinit var dbHelper: DBHelper

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
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), permissionCode)
            }
        }
        supportActionBar?.hide()
        fetchProductsFromFirebase()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.rvProductList)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        searchView = findViewById(R.id.searchView)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // Her satırda iki öğe görüntüle
        val urunListesi = mutableListOf<Urun>()
        urunAdapter = UrunAdapter(this, urunListesi)
        recyclerView.adapter = urunAdapter

        swipeRefreshLayout.setOnRefreshListener {
            fetchProductsFromFirebase()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                urunAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                urunAdapter.filter.filter(newText)
                return false
            }
        })

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
        if (requestCode == permissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
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
        val categorySpinner: Spinner = view.findViewById(R.id.spinnerProductCategory)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Ekle") { dialog, _ ->
                val name = nameEditText.text.toString().trim()
                val price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0
                val info = infoEditText.text.toString().trim()
                val storeName = storeNameEditText.text.toString().trim()
                val category = categorySpinner.selectedItem.toString().trim()

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
                swipeRefreshLayout.isRefreshing = false // Yenileme işlemini durdur
            }
        }.addOnFailureListener { e ->
            showError("Resim yüklenirken bir hata oluştu: ${e.message}")
            swipeRefreshLayout.isRefreshing = false // Yenileme işlemini durdur
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
            swipeRefreshLayout.isRefreshing = false // Yenileme işlemini durdur
        }.addOnFailureListener { e ->
            showError("Ürün eklenirken hata oluştu: ${e.message}")
            swipeRefreshLayout.isRefreshing = false // Yenileme işlemini durdur
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
            swipeRefreshLayout.isRefreshing = false // Yenileme işlemini durdur
        }.addOnFailureListener { exception ->
            showError("Error loading products: ${exception.message}")
            swipeRefreshLayout.isRefreshing = false // Yenileme işlemini durdur
        }
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), permissionCode)
    }
}
