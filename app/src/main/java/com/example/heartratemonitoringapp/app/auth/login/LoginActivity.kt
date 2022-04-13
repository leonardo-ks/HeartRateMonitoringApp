package com.example.heartratemonitoringapp.app.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.heartratemonitoringapp.app.MainActivity
import com.example.heartratemonitoringapp.app.auth.AuthState
import com.example.heartratemonitoringapp.app.auth.register.RegisterActivity
import com.example.heartratemonitoringapp.databinding.ActivityLoginBinding
import com.example.heartratemonitoringapp.util.hideSoftKeyboard
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            if (viewModel.getLoginState().first()) {
                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }

        if (intent.extras != null) {
            val email = intent.extras?.getString("email") ?: ""
            binding.loginForm.loginTidtEmail.setText(email)
            viewModel.setEmailValue(email)
        }
        lifecycleScope.launch {
            viewModel.loginState.collect {
                when (it) {
                    is AuthState.Loading -> {
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                    }
                    is AuthState.Success -> startActivity(Intent(baseContext, MainActivity::class.java))
                    is AuthState.Fail -> {
                        binding.layoutLoading.root.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Snackbar.make(binding.root, it.message, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                    }
                }
            }
        }

        validateForm()

        binding.loginForm.loginBtn.setOnClickListener {
            this.hideSoftKeyboard()
            val email = binding.loginForm.loginTidtEmail.text.toString()
            val password = binding.loginForm.loginTidtPassword.text.toString()
            viewModel.signIn(email, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }



    private fun validateForm() {
        binding.loginForm.loginTidtEmail.addTextChangedListener {
            if (it.isNullOrBlank()) {
                viewModel.setEmailValue(null)
            } else {
                viewModel.setEmailValue(it.toString().trim())
            }
        }
        emailObserver()

        binding.loginForm.loginTidtPassword.addTextChangedListener {
            if (it.isNullOrBlank()) {
                viewModel.setPasswordValue(null)
            } else {
                viewModel.setPasswordValue(it.toString())
            }
        }
        passwordObserver()


        lifecycleScope.launch {
            viewModel.validateFieldStream.collect {
                setButtonEnable(it)
            }
        }
    }

    private fun emailObserver() {
        lifecycleScope.launch {
            viewModel.validateEmail.collect {
                if (it != null) {
                    binding.loginForm.loginTilEmail.apply {
                        isErrorEnabled = it
                        error = if (it) "Email tidak valid" else null
                    }
                }
            }
        }

    }

    private fun passwordObserver() {
        lifecycleScope.launch {
            viewModel.validatePassword.collect {
                if (it != null) {
                    binding.loginForm.loginTilPassword.apply {
                        isErrorEnabled = it
                        error = if (it) "Password tidak valid" else null
                    }
                }
            }
        }
    }

    private fun setButtonEnable(isValid: Boolean) {
        binding.loginForm.loginBtn.isEnabled = isValid
    }
}