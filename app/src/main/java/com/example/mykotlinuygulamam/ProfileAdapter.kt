package com.example.mykotlinuygulamam

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProfileAdapter(
    private val products: MutableList<Product>,
    private val onDeleteProduct: (Product) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewProductName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewProductDescription)
        val priceTextView: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val productImageView: ImageView = itemView.findViewById(R.id.imageViewProductImage)
        val deleteButton: Button = itemView.findViewById(R.id.btnDeleteProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.nameTextView.text = product.name
        holder.descriptionTextView.text = product.description
        holder.priceTextView.text = product.price.toString()
        Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.productImageView)

        holder.deleteButton.setOnClickListener {
            ProductUtils.deleteProduct(product.id, {
                products.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
                Toast.makeText(holder.itemView.context, "Ürün başarıyla silindi.", Toast.LENGTH_SHORT).show()
            }, { e ->
                Toast.makeText(holder.itemView.context, "Ürün silinirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateData(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }
}
