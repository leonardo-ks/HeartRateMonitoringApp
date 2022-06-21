package com.example.heartratemonitoringapp.app.dashboard.profile.editpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.app.dashboard.profile.editprofile.EditProfileViewModel
import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.databinding.ActivityEditPasswordBinding
import com.example.heartratemonitoringapp.databinding.ActivityEditProfileBinding
import com.example.heartratemonitoringapp.util.hideSoftKeyboard
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPasswordBinding
    private val viewModel: EditPasswordViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        validateForm()

        binding.layoutEditPassword.submitBtn.setOnClickListener {
            lifecycleScope.launch {
                hideSoftKeyboard()
                val bearer = viewModel.getBearer().first().toString()
                val old = binding.layoutEditPassword.tidtOldPassword.text.toString()
                val new = binding.layoutEditPassword.tidtNewPassword.text.toString()
                val confirmation = binding.layoutEditPassword.tidtRetypePassword.text.toString()
                viewModel.changePassword(bearer, old, new, confirmation)
                changePasswordObserver()
            }
        }
    }

    private fun validateForm() {
        binding.layoutEditPassword.tidtOldPassword.addTextChangedListener {
            if (it.isNullOrBlank()) {
                viewModel.setOldPasswordValue(null)
            } else {
                viewModel.setOldPasswordValue(it.toString())
            }
        }
        oldPasswordObserver()

        binding.layoutEditPassword.tidtNewPassword.addTextChangedListener {
            if (it.isNullOrBlank()) {
                viewModel.setNewPasswordValue(null)
            } else {
                viewModel.setNewPasswordValue(it.toString().trim())
            }
        }
        newPasswordObserver()

        binding.layoutEditPassword.tidtRetypePassword.addTextChangedListener {
            if (it.isNullOrBlank()) {
                viewModel.setRepasswordValue(null, null)
            } else {
                viewModel.setRepasswordValue(it.toString(), binding.layoutEditPassword.tidtNewPassword.text.toString())
            }
        }
        repasswordObserver()

        lifecycleScope.launch {
            viewModel.validateFieldStream.collect {
                Log.d("validate", it.toString())
                setButtonEnable(it)
            }
        }
    }

    private fun changePasswordObserver() {
        lifecycleScope.launch {
            viewModel.changePassword.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutEditPassword.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        binding.layoutEditPassword.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@EditPasswordActivity, res.data.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        binding.layoutEditPassword.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@EditPasswordActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun oldPasswordObserver() {
        lifecycleScope.launch {
            viewModel.validateOldPassword.collect {
                if (it != null) {
                    binding.layoutEditPassword.tilOldPassword.apply {
                        isErrorEnabled = it
                        error = if (it) context.getString(R.string.password_not_valid) else null
                    }
                }
            }
        }
    }

    private fun newPasswordObserver() {
        lifecycleScope.launch {
            viewModel.validateNewPassword.collect {
                if (it != null) {
                    binding.layoutEditPassword.tilNewPassword.apply {
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
                    binding.layoutEditPassword.tilRetypePassword.apply {
                        isErrorEnabled = it
                        error = if (it) context.getString(R.string.password_not_same) else null
                    }
                }
            }
        }
    }

    private fun setButtonEnable(isValid: Boolean) {
        binding.layoutEditPassword.submitBtn.isEnabled = isValid
    }
}