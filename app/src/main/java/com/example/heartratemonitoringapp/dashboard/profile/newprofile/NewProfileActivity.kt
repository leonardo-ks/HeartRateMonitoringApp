package com.example.heartratemonitoringapp.dashboard.profile.newprofile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.core.data.Resource
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.dashboard.MainActivity
import com.example.heartratemonitoringapp.databinding.ActivityEditProfileBinding
import com.example.heartratemonitoringapp.util.findIndex
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class NewProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var gender: Array<String>
    private val viewModel: NewProfileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        gender = resources.getStringArray(R.array.genders)

        if (intent.extras != null) {
            val email = intent.extras?.getString("email") ?: ""
            val name = intent.extras?.getString("name") ?: ""
            binding.layoutEditProfile.tidtEmail.setText(email)
            binding.layoutEditProfile.tidtName.setText(name)
        }

        binding.layoutEditProfile.tidtGender.setOnClickListener {
            val genderPicker = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.gender))
                .setItems(gender) { _, which ->
                    binding.layoutEditProfile.tidtGender.setText(gender[which])
                }
                .create()
            genderPicker.show()
        }

        binding.layoutEditProfile.tidtDob.setOnClickListener {
            val selection: Long = if (!binding.layoutEditProfile.tidtDob.text.isNullOrBlank()) {
                val unformatted = binding.layoutEditProfile.tidtDob.text.toString().split("-")
                val localDate = LocalDateTime.of(
                    unformatted[2].toInt(),
                    unformatted[1].toInt(),
                    unformatted[0].toInt(),
                    0,
                    0,
                    0
                )
                localDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
            } else {
                MaterialDatePicker.todayInUtcMilliseconds()
            }

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(selection)
                .build()
            datePicker.show(this.supportFragmentManager, datePicker.toString())

            datePicker.addOnPositiveButtonClickListener {
                val unformattedDate = Instant.ofEpochMilli(it).atZone(ZoneId.of("Asia/Jakarta")).toLocalDate()
                val date = unformattedDate.format(DateTimeFormatter.ofPattern("d-MM-yyyy"))
                binding.layoutEditProfile.tidtDob.setText(date)
            }
        }

        binding.layoutEditProfile.submitBtn.setOnClickListener {
            lifecycleScope.launch {
                val bearer = viewModel.getBearer().first().toString()
                val name = binding.layoutEditProfile.tidtName.text.toString()
                val email = binding.layoutEditProfile.tidtEmail.text.toString()
                val unformatted = binding.layoutEditProfile.tidtDob.text.toString().split("-")
                val localDate = LocalDateTime.of(
                    unformatted[2].toInt(),
                    unformatted[1].toInt(),
                    unformatted[0].toInt(),
                    0,
                    0,
                    0
                )
                val dob = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-d"))
                val gender = findIndex(gender, binding.layoutEditProfile.tidtGender.text.toString())
                val height = binding.layoutEditProfile.tidtHeight.text.toString().toInt()
                val weight = binding.layoutEditProfile.tidtWeight.text.toString().toInt()
                viewModel.updateProfile(bearer, name, email, dob, gender, height, weight)
                updateProfileObserver()
            }
        }
    }

    private fun updateProfileObserver() {
        lifecycleScope.launch {
            viewModel.updateProfile.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                    }
                    is Resource.Success -> {
                        binding.layoutEditProfile.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(
                            baseContext,
                            getString(R.string.success_change_profile),
                            Toast.LENGTH_SHORT
                        ).show()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        startActivity(Intent(baseContext, MainActivity::class.java))
                    }
                    is Resource.Error -> {
                        binding.layoutEditProfile.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(baseContext, res.message.toString(), Toast.LENGTH_SHORT).show()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            }
        }
    }
}