package com.sonicmaster.locationtracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sonicmaster.locationtracker.SharedPrefs.MOBILE
import com.sonicmaster.locationtracker.SharedPrefs.NAME
import com.sonicmaster.locationtracker.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPrefs.init(requireContext())
        if (SharedPrefs.read(NAME) != null) {
            findNavController().navigate(R.id.action_loginFragment_to_locationFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.loginBtn.setOnClickListener {
            val name = binding.nameEdt.text?.trim().toString()
            val mobile = binding.mobileEdt.text?.trim().toString()
            if (name.isEmpty()) {
                binding.apply {
                    nameEdt.error = "Please enter name"
                    nameEdt.requestFocus()
                }
            } else if (mobile.isEmpty()) {
                binding.apply {
                    mobileEdt.error = "Please enter mobile"
                    binding.mobileEdt.requestFocus()
                }
            } else {
                SharedPrefs.write(NAME, name)
                SharedPrefs.write(MOBILE, mobile)
                findNavController().navigate(R.id.action_loginFragment_to_locationFragment)
            }
        }
    }
}