package com.example.heartratemonitoringapp.app.dashboard.profile.editprofile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.databinding.ActivityEditProfileBinding
import com.example.heartratemonitoringapp.util.findIndex
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var gender: Array<String>
    private val viewModel: EditProfileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        gender = resources.getStringArray(R.array.genders)

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
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
                val localDate = LocalDateTime.of(unformatted[2].toInt(), unformatted[1].toInt(), unformatted[0].toInt(), 0,0,0)
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
                val date = unformattedDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                binding.layoutEditProfile.tidtDob.setText(date)
            }
        }

        binding.layoutEditProfile.submitBtn.setOnClickListener {
            lifecycleScope.launch {
                val bearer = viewModel.getBearer().first().toString()
                val name = binding.layoutEditProfile.tidtName.text.toString()
                val email = binding.layoutEditProfile.tidtEmail.text.toString()
                val unformatted = binding.layoutEditProfile.tidtDob.text.toString().split("-")
                val localDate = LocalDateTime.of(unformatted[2].toInt(), unformatted[1].toInt(), unformatted[0].toInt(), 0,0,0)
                val dob = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val gender = findIndex(gender, binding.layoutEditProfile.tidtGender.text.toString())
                viewModel.updateProfile(bearer, name, email, dob, gender)
            }
        }

        lifecycleScope.launch {
            val bearer = viewModel.getBearer().first().toString()
            viewModel.getProfile(bearer)
        }

        userProfileObserver()
        updateProfileObserver()
    }

    private fun userProfileObserver() {
        lifecycleScope.launch {
            viewModel.profile.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutEditProfile.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        binding.layoutEditProfile.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        binding.layoutEditProfile.tidtEmail.setText(res.data?.email.toString())
                        binding.layoutEditProfile.tidtName.setText(res.data?.name.toString())
                        binding.layoutEditProfile.tidtGender.setText(gender[res.data?.gender!!])
                        binding.layoutEditProfile.tidtDob.setText(LocalDate.parse(res.data?.dob.toString()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                    }
                    is Resource.Error -> {
                        binding.layoutEditProfile.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@EditProfileActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
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
                        binding.layoutEditProfile.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        binding.layoutEditProfile.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(baseContext, getString(R.string.success_change_profile), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        binding.layoutEditProfile.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(baseContext, res.message.toString(), Toast.LENGTH_SHORT).show()                    }
                }
            }
        }
    }
}