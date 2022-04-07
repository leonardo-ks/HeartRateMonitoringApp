package com.example.heartratemonitoringapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.heartratemonitoringapp.databinding.ActivityRegisterBinding
import com.example.heartratemonitoringapp.ui.login.LoginActivity
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isEmpty = true
        var isError = false

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerForm.registerBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.registerForm.registerTidtEmail.doOnTextChanged { text, _, _, _ ->
            if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches() && !text.isNullOrBlank()) {
                binding.registerForm.registerTilEmail.error = "Format email tidak valid"
            } else {
                isEmpty = false
                isError = false
                binding.registerForm.registerTilEmail.error = null
                binding.registerForm.registerTilEmail.isErrorEnabled = false
            }
        }

        binding.registerForm.registerTidtPassword.doOnTextChanged { text, _, _, _ ->
            if (text.toString().length < 8 && !text.isNullOrBlank()) {
                binding.registerForm.registerTilPassword.error = "Panjang password harus lebih dari 8"
            } else {
                isEmpty = false
                isError = false
                binding.registerForm.registerTilPassword.error = null
                binding.registerForm.registerTilPassword.isErrorEnabled = false
            }
        }

        binding.registerForm.registerTidtRepassword.doOnTextChanged { text, _, _, _ ->
            if (text.toString() != binding.registerForm.registerTidtPassword.text.toString() && !text.isNullOrBlank()) {
                binding.registerForm.registerTilRepassword.error = "Password yang dimasukkan tidak sama"
                binding.registerForm.registerTilRepassword.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            } else {
                isEmpty = false
                isError = false
                binding.registerForm.registerTilRepassword.error = null
                binding.registerForm.registerTilRepassword.isErrorEnabled = false
            }
        }
    }
}