package com.example.mykotlinuygulamam.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val orderDate: Long = 0L,
    val city: String = "",
    val district: String = "",
    val neighborhood: String = "",
    val buildingNumber: String = ""
)
