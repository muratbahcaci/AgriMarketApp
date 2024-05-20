package com.example.mykotlinuygulamam

data class CartItem(
    val productId: String, // productId alanı eklendi
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val quantity: Int
) {
    constructor() : this("", "", 0.0, "", "", 0) // Firestore için boş constructor
}
