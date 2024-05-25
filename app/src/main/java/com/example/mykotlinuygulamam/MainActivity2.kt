package com.example.mykotlinuygulamam

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mykotlinuygulamam.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class MainActivity2 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()

        binding.switchShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Şifre göster
                binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                // Şifre gizle
                binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Kullanıcının imleci şifre alanının sonuna taşıyın
            binding.passwordEditText.setSelection(binding.passwordEditText.length())
            binding.confirmPasswordEditText.setSelection(binding.confirmPasswordEditText.length())
        }

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show()
                        // Burada başarılı kayıt sonrası işlemler yapılabilir
                    } else {
                        if(task.exception is FirebaseAuthUserCollisionException){
                            Toast.makeText(this, "Bu e-posta adresi ile daha önce bir hesap oluşturulmuş.", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this, "Kayıt başarısız: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                if (password != confirmPassword) {
                    Toast.makeText(this, "Şifreler uyuşmuyor.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Tüm alanları doldurun.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
