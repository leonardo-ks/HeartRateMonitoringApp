package com.example.heartratemonitoringapp.app.dashboard.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.heartratemonitoringapp.R
import com.example.heartratemonitoringapp.app.MainActivity
import com.example.heartratemonitoringapp.app.auth.login.LoginActivity
import com.example.heartratemonitoringapp.app.dashboard.profile.editpassword.EditPasswordActivity
import com.example.heartratemonitoringapp.app.dashboard.profile.editpassword.EditPasswordViewModel
import com.example.heartratemonitoringapp.app.dashboard.profile.editprofile.EditProfileActivity
import com.example.heartratemonitoringapp.data.Resource
import com.example.heartratemonitoringapp.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfileObserver()
        lifecycleScope.launch {
            val bearer = viewModel.getBearer().first()
            viewModel.getProfile(bearer.toString())
        }

        binding.layoutProfile.btnChangeProfile.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        binding.layoutProfile.btnChangePassword.setOnClickListener {
            startActivity(Intent(context, EditPasswordActivity::class.java))
        }

        binding.layoutProfile.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                val bearer = viewModel.getBearer().first()
                if (!bearer.isNullOrBlank()) {
                    logout(viewModel.getBearer().first().toString())
                }
            }
        }
    }

    private fun userProfileObserver() {
        lifecycleScope.launch {
            viewModel.profile.collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        binding.layoutLoading.root.z = 10F
                        binding.layoutLoading.root.visibility = View.VISIBLE
                        binding.layoutProfile.root.visibility = View.INVISIBLE
                    }
                    is Resource.Success -> {
                        binding.layoutProfile.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Glide.with(requireContext())
                            .load(res.data?.profile)
                            .centerCrop()
                            .placeholder(R.drawable.ic_account)
                            .into(binding.layoutProfile.ivProfile)
                        binding.layoutProfile.tvEmail.text = res.data?.email
                        binding.layoutProfile.tvName.text = res.data?.name
                    }
                    is Resource.Error -> {
                        binding.layoutProfile.root.visibility = View.VISIBLE
                        binding.layoutLoading.root.visibility = View.GONE
                        Snackbar.make(binding.root, res.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun logout(bearer: String) {
        lifecycleScope.launch {
            viewModel.logout(bearer).collect {
                when (it) {
                    is Resource.Loading -> binding.layoutProfile.btnLogout.isEnabled = false
                    is Resource.Success -> startActivity(Intent(requireContext(), LoginActivity::class.java))
                    is Resource.Error -> {
                        binding.layoutProfile.btnLogout.isEnabled = true
                        Toast.makeText(requireContext(), getString(R.string.logout_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}