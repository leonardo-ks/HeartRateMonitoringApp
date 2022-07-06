package com.example.heartratemonitoringapp.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.auth.AuthState
import com.example.heartratemonitoringapp.auth.login.LoginActivity
import com.example.heartratemonitoringapp.databinding.ActivityRegisterBinding
import com.example.heartratemonitoringapp.util.hideSoftKeyboard
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.registerState.collect{
                when (it) {
                    is AuthState.Loading -> {
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                    }
                    is AuthState.Success -> {
                        val intent = Intent(baseContext, LoginActivity::class.java)
                        intent.putExtra("email", binding.registerForm.registerTidtEmail.text.toString())
                        startActivity(intent)
                    }
                    is AuthState.Fail -> {
                        binding.layoutLoading.root.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                    }
                }
            }
        }

        validateForm()

        binding.registerForm.registerBtn.setOnClickListener {
            this.hideSoftKeyboard()
            val name = binding.registerForm.registerTidtName.text.toString()
            val email = binding.registerForm.registerTidtEmail.text.toString()
            val password = binding.registerForm.registerTidtPassword.text.toString()
            viewModel.register(name, email, password)
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validateForm() {
        binding.registerForm.registerTidtName.addTextChangedListener {
            if (it.isNullOrBlank()) {
                viewModel.setNameValue(null)
            } else {
                viewModel.setNameValue(it.toString())
            }
        }
        nameObserver()

        binding.registerForm.registerTidtEmail.addTextChangedListener {
            if (it.isNullOrBlank()) {
                viewModel.setEmailValue(null)
            } else {
                viewModel.setEmailValue(it.toString().trim())
            }
        }
        emailObserver()

        binding.registerForm.registerTidtPassword.addTextChangedListener {
            if (it.isNullOrBlank()) {
                viewModel.setPasswordValue(null)
            } else {
                viewModel.setPasswordValue(it.toString())
            }
        }
        passwordObserver()

        binding.registerForm.registerTidtRepassword.addTextChangedListener {
            if (it.isNullOrBlank()) {
                viewModel.setRepasswordValue(null, null)
            } else {
                viewModel.setRepasswordValue(it.toString(), binding.registerForm.registerTidtPassword.text.toString())
            }
        }
        repasswordObserver()

        lifecycleScope.launch {
            viewModel.validateFieldStream.collect {
                setButtonEnable(it)
            }
        }
    }

    private fun nameObserver() {
        lifecycleScope.launch {
            viewModel.validateName.collect {
                if (it != null) {
                    binding.registerForm.registerTilName.apply {
                        isErrorEnabled = it
                        error = if (it) context.getString(R.string.name_cannot_empty) else null
                    }
                }
            }
        }
    }

    private fun emailObserver() {
        lifecycleScope.launch {
            viewModel.validateEmail.collect {
                if (it != null) {
                    binding.registerForm.registerTilEmail.apply {
                        isErrorEnabled = it
                        error = if (it) context.getString(R.string.email_not_valid) else null
                    }
                }
            }
        }
    }

    private fun passwordObserver() {
        lifecycleScope.launch {
            viewModel.validatePassword.collect {
                if (it != null) {
                    binding.registerForm.registerTilPassword.apply {
                        isErrorEnabled = it
                        error = if (it) context.getString(R.string.password_not_valid) else null
                    }
                }
            }
        }
    }

    private fun repasswordObserver() {
        lifecycleScope.launch {
            viewModel.validateRepassword.collect {
                if (it != null) {
                    binding.registerForm.registerTilRepassword.apply {
                        isErrorEnabled = it
                        error = if (it) context.getString(R.string.password_not_same) else null
                    }
                }
            }
        }
    }

    private fun setButtonEnable(isValid: Boolean) {
        binding.registerForm.registerBtn.isEnabled = isValid
    }
}