package com.manchasdelivery_associates.profile_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.manchasdelivery_associates.R
import com.manchasdelivery_associates.databinding.FragmentProfileBinding
import com.manchasdelivery_associates.utils.saveStringToSharedPreferences
import com.manchasdelivery_associates.utils.showSnackbar

class ProfileFragment : Fragment() {

    private var viewModel: ProfileFragmentViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentProfileBinding.inflate(inflater,container,false)
        val app = requireNotNull(this.activity).application
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let {
            val factory = ProfileFragmentViewModelFactory(app, it)
            viewModel = ViewModelProvider(this,factory)[ProfileFragmentViewModel::class.java]
        }
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.executePendingBindings()

        viewModel?.navigateToProfileFragment?.observe(viewLifecycleOwner){
            if (it){
                this.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToMainFragment())
                viewModel?.setNavigateToMainFragment(false)
            }
        }

        binding.saveProfileBtn.setOnClickListener {
            if (binding.nameEt.text.toString() ==""){
                showSnackbar(binding.root.rootView, getString(R.string.something_wrong))
                viewModel?.setNavigateToMainFragment(false)
            }else{
                context?.let { ctx ->
                    saveStringToSharedPreferences(getString(R.string.user_name), binding.nameEt.text.toString(), ctx)
                }
                viewModel?.setNavigateToMainFragment(true)
            }
        }

        return binding.root
    }


}