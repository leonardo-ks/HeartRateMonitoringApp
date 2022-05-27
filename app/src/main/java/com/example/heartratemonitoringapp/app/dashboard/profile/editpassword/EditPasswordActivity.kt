package com.example.heartratemonitoringapp.app.dashboard.profile.editpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.app.dashboard.profile.editprofile.EditProfileViewModel
import com.example.heartratemonitoringapp.databinding.ActivityEditPasswordBinding
import com.example.heartratemonitoringapp.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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
                setButtonEnable(it)
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
        binding.layoutEditPassword.saveBtn.isEnabled = isValid
    }
}