package com.example.heartratemonitoringapp.dashboard.profile.contact.add

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.data.Resource
import com.example.heartratemonitoringapp.dashboard.profile.contact.adapter.ContactAdapter
import com.example.heartratemonitoringapp.databinding.ActivityAddContactBinding
import com.example.heartratemonitoringapp.util.hideSoftKeyboard
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private val viewModel: AddContactViewModel by viewModel()
    private var mAdapter = ContactAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (supportActionBar != null) {
            this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            this.supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.layoutAddContact.tidtName.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                val param = binding.layoutAddContact.tidtName.text.toString()
                if (param.isNotEmpty()) {
                    hideSoftKeyboard()
                    lifecycleScope.launch {
                        viewModel.search(viewModel.getBearer().first().toString(), param)
                        searchObserver()
                    }
                }
            }
            true
        }

        mAdapter.onItemClick = {
            lifecycleScope.launch {
                it.id?.let { it1 ->
                    viewModel.addContacts(viewModel.getBearer().first().toString(),
                        it1
                    )
                }
                addContactObserver()
            }
        }
    }

    private fun addContactObserver() {
        lifecycleScope.launch {
            viewModel.addContacts.collect { res ->
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
                        binding.layoutAddContact.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@AddContactActivity, res.data.toString(), Toast.LENGTH_SHORT).show()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                    is Resource.Error -> {
                        binding.layoutAddContact.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@AddContactActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            }
        }
    }

    private fun searchObserver() {
        lifecycleScope.launch {
            viewModel.search.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.layoutAddContact.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        res.data?.let {
                            mAdapter.setData(it)
                            mAdapter.setButtonState(false)
                        }
                        showRecycleView()
                    }
                    is Resource.Error -> {
                        binding.layoutAddContact.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Toast.makeText(this@AddContactActivity, res.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showRecycleView() {
        binding.layoutAddContact.rvContacts.apply {
            layoutManager = LinearLayoutManager(this@AddContactActivity)
            !hasFixedSize()
            adapter = mAdapter
        }
    }
}