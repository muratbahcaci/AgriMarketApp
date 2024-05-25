package com.example.mykotlinuygulamam

import CartItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartItemAdapter(
    private val cartItems: List<CartItem>,
    private val itemClickListener: (CartItem) -> Unit,
    private val deleteClickListener: (CartItem) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cart_item_layout, parent, false)
        return CartItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val currentItem = cartItems[position]
        holder.bind(currentItem, itemClickListener, deleteClickListener)
    }

    override fun getItemCount() = cartItems.size

    class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.textViewProductName)
        private val itemPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
        private val itemDescription: TextView = itemView.findViewById(R.id.textViewProductDescription)
        private val itemImage: ImageView = itemView.findViewById(R.id.imageViewProductImage)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteProduct)

        fun bind(cartItem: CartItem, itemClickListener: (CartItem) -> Unit, deleteClickListener: (CartItem) -> Unit) {
            itemName.text = cartItem.name
            itemPrice.text = cartItem.price.toString()
            itemDescription.text = cartItem.description
            Glide.with(itemView.context).load(cartItem.imageUrl).into(itemImage)

            itemView.setOnClickListener { itemClickListener(cartItem) }
            deleteButton.setOnClickListener { deleteClickListener(cartItem) }
        }
    }
}
