package com.example.mykotlinuygulamam

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class SifreOnayActivity : AppCompatActivity() {
    private lateinit var authInstance: FirebaseAuth
    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnChangePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sifre_onay)

        authInstance = FirebaseAuth.getInstance()
        etCurrentPassword = findViewById(R.id.etCurrentPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnChangePassword = findViewById(R.id.btnChangePassword)

        btnChangePassword.setOnClickListener {
            val currentPassword = etCurrentPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmNewPassword = etConfirmNewPassword.text.toString()

            if (newPassword == confirmNewPassword) {
                if (currentPassword == newPassword) {
                    Toast.makeText(this, "Yeni şifre eski şifre ile aynı olamaz", Toast.LENGTH_LONG).show()
                } else {
                    val user = authInstance.currentUser
                    val email = user?.email

                    if (email != null && user != null) {
                        val credential = EmailAuthProvider.getCredential(email, currentPassword)

                        user.reauthenticate(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(this, "Şifre başarıyla güncellendi", Toast.LENGTH_LONG).show()
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Şifre güncellenirken hata oluştu: ${updateTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this, "Mevcut şifre yanlış", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Yeni şifreler uyuşmuyor", Toast.LENGTH_LONG).show()
            }
        }
    }
}
