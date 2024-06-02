package com.example.mykotlinuygulamam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mykotlinuygulamam.model.Order

class OrderAdapter(private var orderList: MutableList<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orderList.size

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvOrderID: TextView = view.findViewById(R.id.tvOrderID)
        private val tvOrderPrice: TextView = view.findViewById(R.id.tvOrderPrice)
        private val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)

        fun bind(order: Order) {
            tvOrderID.text = order.id
            tvOrderPrice.text = itemView.context.getString(R.string.order_price, order.totalPrice)
            tvOrderDate.text = order.orderDate.toString() // Formatlama gerekebilir
        }
    }

    fun updateOrders(newOrders: List<Order>) {
        orderList.clear()
        orderList.addAll(newOrders)
        notifyDataSetChanged()
    }
}
