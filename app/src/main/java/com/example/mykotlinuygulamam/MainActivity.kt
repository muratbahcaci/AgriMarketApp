package com.example.mykotlinuygulamam

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
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            val email = binding.emailAddressEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Giriş başarılı.", Toast.LENGTH_SHORT).show()
                        // Giriş başarılı olduğunda MainActivity3'e yönlendirme yapılıyor
                        val intentToMain3 = Intent(this, MainActivity3::class.java)
                        startActivity(intentToMain3)
                        finish() // Kullanıcıyı MainActivity3'e götürdükten sonra bu aktiviteyi kapat
                    } else {
                        Toast.makeText(this, "Giriş başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
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
            // Kayıt aktivitesine yönlendirme yapılıyor
            val intentToRegister = Intent(this, MainActivity2::class.java)
            startActivity(intentToRegister)
        }

        // Şifre görünürlüğü toggle'ı
        binding.switchShowHidePassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Şifreyi göster
                binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Şifreyi gizle
                binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Toggle sonrasında metnin sonuna imleci taşı
            binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
        }
    }
}
