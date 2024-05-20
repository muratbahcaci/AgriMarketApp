package com.example.mykotlinuygulamam

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartItemAdapter(
    private val items: List<CartItem>,
    private val onItemClicked: (CartItem) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return CartItemViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    class CartItemViewHolder(itemView: View, private val onItemClicked: (CartItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewItem)
        private val textViewName: TextView = itemView.findViewById(R.id.textViewItemName)
        private val textViewPrice: TextView = itemView.findViewById(R.id.textViewItemPrice)
        private val textViewQuantity: TextView = itemView.findViewById(R.id.textViewItemQuantity)

        fun bind(item: CartItem) {
            textViewName.text = item.name
            textViewPrice.text = "${item.price} TL"
            textViewQuantity.text = "Adet: ${item.quantity}"
            Glide.with(itemView.context).load(item.imageUrl).into(imageView)

            itemView.setOnClickListener {
                onItemClicked(item)
            }
        }
    }
}
