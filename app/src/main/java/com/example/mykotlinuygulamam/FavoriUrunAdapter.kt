package com.example.mykotlinuygulamam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoriUrunAdapter(
    private val favoriUrunlerListesi: MutableList<FavoriUrun>,
    private val onItemDeleted: (FavoriUrun) -> Unit,
    private val onItemClick: (FavoriUrun) -> Unit // Yeni tıklama işleyici
) : RecyclerView.Adapter<FavoriUrunAdapter.FavoriUrunViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriUrunViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favori_urun_item_layout, parent, false)
        return FavoriUrunViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriUrunViewHolder, position: Int) {
        val item = favoriUrunlerListesi[position]
        holder.bind(item)
        holder.deleteButton.setOnClickListener {
            onItemDeleted(item)
            favoriUrunlerListesi.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
        holder.itemView.setOnClickListener {
            onItemClick(item) // Tıklama olayını ilet
        }
    }

    override fun getItemCount(): Int = favoriUrunlerListesi.size

    class FavoriUrunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewProductImage)
        private val nameView: TextView = itemView.findViewById(R.id.textViewProductName)
        private val priceView: TextView = itemView.findViewById(R.id.textViewProductPrice)
        private val descriptionView: TextView = itemView.findViewById(R.id.textViewProductDescription)
        val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteProduct)

        fun bind(favoriUrun: FavoriUrun) {
            nameView.text = favoriUrun.name
            priceView.text = favoriUrun.price.toString()
            descriptionView.text = favoriUrun.description
            Glide.with(itemView.context).load(favoriUrun.imageUrl).into(imageView)
        }
    }
}
