package com.example.heartratemonitoringapp.dashboard.profile.contact

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.data.Resource
import com.example.core.domain.usecase.model.UserDataDomain
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.dashboard.profile.contact.adapter.ContactAdapter
import com.example.heartratemonitoringapp.dashboard.profile.contact.add.AddContactActivity
import com.example.heartratemonitoringapp.dashboard.profile.editpassword.EditPasswordActivity
import com.example.heartratemonitoringapp.dashboard.profile.editpassword.EditPasswordViewModel
import com.example.heartratemonitoringapp.databinding.ActivityContactBinding
import com.example.heartratemonitoringapp.databinding.ActivityEditPasswordBinding
import com.example.heartratemonitoringapp.databinding.ActivityEditProfileBinding
import com.example.heartratemonitoringapp.form.adapter.FormAdapter
import com.example.heartratemonitoringapp.monitoring.ble.BLE
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactBinding
    private val viewModel: ContactViewModel by viewModel()
    private var mAdapter = ContactAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        lifecycleScope.launch {
            val bearer = viewModel.getBearer().first()
            viewModel.getContacts(bearer.toString())
            getContactObserver()
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.toolbar.apply {
            binding.btnAddContact.setOnClickListener {
                startActivity(Intent(context, AddContactActivity::class.java))
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }

        mAdapter.onDeleteClick = {
            lifecycleScope.launch {
                viewModel.deleteContact(viewModel.getBearer().first().toString(), it)
                deleteContactObserver()
            }
        }
    }

    private fun refresh() {
        lifecycleScope.launch {
            val bearer = viewModel.getBearer().first()
            viewModel.getContacts(bearer.toString())
            getContactObserver()
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun deleteContactObserver() {
        lifecycleScope.launch {
            viewModel.deleteContact.collect { res ->
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
                        binding.layoutContacts.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@ContactActivity, res.data.toString(), Toast.LENGTH_SHORT).show()
                        refresh()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                    is Resource.Error -> {
                        binding.layoutContacts.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@ContactActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            }
        }
    }

    private fun getContactObserver() {
        lifecycleScope.launch {
            viewModel.contacts.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        if (!res.data.isNullOrEmpty()) {
                            binding.layoutContacts.root.visibility = View.VISIBLE
                            binding.layoutEmptyContact.root.visibility = View.GONE
                            binding.layoutLoading.root.visibility = View.GONE
                            mAdapter.setData(res.data!!)
                            mAdapter.setButtonState(true)
                            showRecycleView()
                        } else {
                            binding.layoutEmptyContact.root.visibility = View.VISIBLE
                            binding.layoutContacts.root.visibility = View.GONE
                            binding.layoutLoading.root.visibility = View.GONE
                        }
                    }
                    is Resource.Error -> {
                        binding.layoutContacts.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@ContactActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showRecycleView() {
        binding.layoutContacts.rvContacts.apply {
            layoutManager = LinearLayoutManager(this@ContactActivity)
            !hasFixedSize()
            adapter = mAdapter
        }
    }
}