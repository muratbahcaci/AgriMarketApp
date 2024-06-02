package com.example.mykotlinuygulamam

import com.example.mykotlinuygulamam.model.CartItem
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class PaymentActivity : AppCompatActivity() {

    private lateinit var editTextCardNumber: EditText
    private lateinit var editTextCardExpiry: EditText
    private lateinit var editTextCardCVC: EditText
    private lateinit var editTextCity: EditText
    private lateinit var editTextDistrict: EditText
    private lateinit var editTextNeighborhood: EditText
    private lateinit var editTextBuildingNumber: EditText
    private lateinit var buttonPay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        editTextCardNumber = findViewById(R.id.editTextCardNumber)
        editTextCardExpiry = findViewById(R.id.editTextCardExpiry)
        editTextCardCVC = findViewById(R.id.editTextCardCVC)
        editTextCity = findViewById(R.id.editTextCity)
        editTextDistrict = findViewById(R.id.editTextDistrict)
        editTextNeighborhood = findViewById(R.id.editTextNeighborhood)
        editTextBuildingNumber = findViewById(R.id.editTextBuildingNumber)
        buttonPay = findViewById(R.id.buttonPay)

        buttonPay.setOnClickListener {
            processPayment()
        }
    }

    private fun processPayment() {
        val cardNumber = editTextCardNumber.text.toString().trim()
        val cardExpiry = editTextCardExpiry.text.toString().trim()
        val cardCVC = editTextCardCVC.text.toString().trim()
        val city = editTextCity.text.toString().trim()
        val district = editTextDistrict.text.toString().trim()
        val neighborhood = editTextNeighborhood.text.toString().trim()
        val buildingNumber = editTextBuildingNumber.text.toString().trim()

        if (!isValidCardNumber(cardNumber)) {
            Toast.makeText(this, "Geçersiz kart numarası", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidExpiryDate(cardExpiry)) {
            Toast.makeText(this, "Geçersiz son kullanma tarihi", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidCVC(cardCVC)) {
            Toast.makeText(this, "Geçersiz CVC", Toast.LENGTH_SHORT).show()
            return
        }

        if (city.isEmpty() || district.isEmpty() || neighborhood.isEmpty() || buildingNumber.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        // Ödeme işlemini gerçekleştirin (Bu örnekte basit bir Toast mesajı gösteriyoruz)
        Toast.makeText(this, "Ödeme işlemi başarıyla gerçekleştirildi", Toast.LENGTH_SHORT).show()

        // Siparişi kaydet
        saveOrder(city, district, neighborhood, buildingNumber)
    }

    private fun isValidCardNumber(cardNumber: String): Boolean {
        return cardNumber.length == 16 && cardNumber.all { it.isDigit() }
    }

    private fun isValidExpiryDate(cardExpiry: String): Boolean {
        val pattern = Pattern.compile("^\\d{2}/\\d{2}$")
        if (!pattern.matcher(cardExpiry).matches()) {
            return false
        }

        val parts = cardExpiry.split("/")
        val month = parts[0].toIntOrNull()
        val year = parts[1].toIntOrNull()

        return month != null && year != null && month in 1..12
    }

    private fun isValidCVC(cvc: String): Boolean {
        return cvc.length == 3 && cvc.all { it.isDigit() }
    }

    private fun saveOrder(city: String, district: String, neighborhood: String, buildingNumber: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val order = Order(
            userId = userId,
            items = selectedItems,
            totalPrice = selectedItems.sumOf { it.price },
            orderDate = System.currentTimeMillis(),
            city = city,
            district = district,
            neighborhood = neighborhood,
            buildingNumber = buildingNumber
        )

        FirebaseFirestore.getInstance().collection("Orders")
            .add(order)
            .addOnSuccessListener {
                Toast.makeText(this, "Sipariş başarıyla oluşturuldu", Toast.LENGTH_SHORT).show()
                // Sipariş başarıyla oluşturulduktan sonra kullanıcıyı sipariş durumu aktivitesine yönlendirin
                val intent = Intent(this, OrderStatusActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Sipariş oluşturulurken hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        val selectedItems = mutableListOf<CartItem>() // Seçilen ürünler listesi
    }
}

data class Order(
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val orderDate: Long = 0L,
    val city: String = "",
    val district: String = "",
    val neighborhood: String = "",
    val buildingNumber: String = ""
)
