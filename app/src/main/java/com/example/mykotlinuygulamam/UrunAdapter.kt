package com.example.mykotlinuygulamam

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UrunAdapter(private val context: Context, private var urunListesi: MutableList<Urun>) :
    RecyclerView.Adapter<UrunAdapter.UrunViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrunViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_urun, parent, false)
        return UrunViewHolder(view)
    }

    override fun onBindViewHolder(holder: UrunViewHolder, position: Int) {
        val urun = urunListesi[position]
        holder.bind(urun)

        // Öğeye tıklama işlevselliğini ekleyin
        holder.itemView.setOnClickListener {
            // Ürün detayları için bir Intent oluşturun
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("id", urun.id) // ID eklenmesi
                putExtra("name", urun.name)
                putExtra("price", urun.price)
                putExtra("description", urun.info)
                putExtra("imageUrl", urun.imageUrl)
                putExtra("category", urun.category)
            }
            // Oluşturulan Intent ile Activity başlatın
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = urunListesi.size

    inner class UrunViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val urunResimImageView: ImageView = view.findViewById(R.id.urunResimImageView)
        private val urunAdiTextView: TextView = view.findViewById(R.id.urunAdiTextView)
        private val urunFiyatiTextView: TextView = view.findViewById(R.id.urunFiyatiTextView)
        private val urunAciklamasiTextView: TextView = view.findViewById(R.id.urunAciklamasiTextView)

        fun bind(urun: Urun) {
            Glide.with(context).load(urun.imageUrl).into(urunResimImageView)
            urunAdiTextView.text = urun.name
            urunFiyatiTextView.text = "${urun.price} TL"
            urunAciklamasiTextView.text = urun.info
        }
    }

    fun updateUrunler(newList: MutableList<Urun>) {
        urunListesi.clear()
        urunListesi.addAll(newList)
        notifyDataSetChanged()
    }
}
