package com.example.mykotlinuygulamam

data class Product(
    var id: String = "",
    var name: String = "",
    var description: String = "", // Bu alanın tanımlı olduğundan emin olun
    var price: Double = 0.0,
    var imageUrl: String = "",
    var storeName: String = "",
    var category: String = ""
)
