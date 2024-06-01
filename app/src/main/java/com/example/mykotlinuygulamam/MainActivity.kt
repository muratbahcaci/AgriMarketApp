package com.example.mykotlinuygulamam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mykotlinuygulamam.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences'den kaydedilen bilgileri al
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)

        // Kullanıcı bilgileri kaydedilmişse, doğrudan diğer aktiviteye yönlendir
        if (savedEmail != null && savedPassword != null) {
            signIn(savedEmail, savedPassword)
        } else {
            // Kullanıcı bilgileri yoksa, giriş ekranını göster
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            auth = FirebaseAuth.getInstance()
            supportActionBar?.hide()

            binding.signInButton.setOnClickListener {
                val email = binding.emailAddressEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    signIn(email, password)
                } else {
                    Toast.makeText(this, "E-posta ve şifre alanlarını doldurunuz.", Toast.LENGTH_SHORT).show()
                }
            }

            binding.buttonForgotPassword.setOnClickListener {
                val email = binding.emailAddressEditText.text.toString().trim()

                if (email.isNotEmpty()) {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Şifre sıfırlama linki e-posta adresinize gönderildi.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Bir hata oluştu: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "E-posta adresini giriniz.", Toast.LENGTH_SHORT).show()
                }
            }

            binding.buttonRegister.setOnClickListener {
                val intentToRegister = Intent(this, MainActivity2::class.java)
                startActivity(intentToRegister)
            }

            binding.switchShowHidePassword.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("email", email)
                    putString("password", password)
                    apply()
                }
                Toast.makeText(this, "Giriş başarılı.", Toast.LENGTH_SHORT).show()
                val intentToMain3 = Intent(this, MainActivity3::class.java)
                startActivity(intentToMain3)
                finish()
            } else {
                Toast.makeText(this, "Giriş başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
