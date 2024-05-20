package com.example.mykotlinuygulamam

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

object ProductUtils {
    fun deleteProduct(productId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val firestoreInstance = FirebaseFirestore.getInstance()
        firestoreInstance.collection("Products").document(productId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
