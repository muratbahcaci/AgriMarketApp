package com.example.mykotlinuygulamam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoriUrunlerActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriUrunAdapter
    private val favoriUrunlerListesi = mutableListOf<FavoriUrun>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favori_urunler)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView = findViewById(R.id.recyclerViewFavoriUrunler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        supportActionBar?.hide()
        adapter = FavoriUrunAdapter(favoriUrunlerListesi) { favoriUrun ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                FirebaseFirestore.getInstance()
                    .collection("UserFavorites")
                    .document(userId)
                    .collection("Favorites")
                    .document(favoriUrun.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Ürün favorilerden kaldırıldı", Toast.LENGTH_SHORT).show()
                        fetchFavoriUrunler()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Silme işlemi başarısız: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            fetchFavoriUrunler()
        }

        fetchFavoriUrunler()
    }

    private fun fetchFavoriUrunler() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            swipeRefreshLayout.isRefreshing = true
            FirebaseFirestore.getInstance()
                .collection("UserFavorites")
                .document(userId)
                .collection("Favorites")
                .get()
                .addOnSuccessListener { documents ->
                    favoriUrunlerListesi.clear()
                    for (document in documents) {
                        val favoriUrun = document.toObject(FavoriUrun::class.java)
                        favoriUrunlerListesi.add(favoriUrun)
                    }
                    adapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Hata: ${exception.message}", Toast.LENGTH_SHORT).show()
                    swipeRefreshLayout.isRefreshing = false
                }
        }
    }
}
