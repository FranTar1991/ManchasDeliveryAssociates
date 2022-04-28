package com.manchasdelivery_associates.main_fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.manchasdelivery_associates.R
import com.manchasdelivery_associates.databinding.FragmentMainBinding
import com.manchasdelivery_associates.utils.*

class MainFragment : Fragment() {
    private lateinit var requestInUserNodeRef: DatabaseReference
    private lateinit var currentRequestId: String
    private lateinit var currentOwnerOfRequestId: String
    private var viewModel: MainFragmentViewModel? = null

    private lateinit var dbReferenceForUsers: DatabaseReference
    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            viewModel?.setIsLocationPermissionGranted(isGranted)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentMainBinding.inflate(inflater, container, false)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val baseReference = FirebaseDatabase.getInstance().reference.child("servers")


        userId?.let {
            val requestInServerNodeRef = baseReference.child(userId).child("current")
            dbReferenceForUsers = FirebaseDatabase.getInstance().reference.child("users")
            val repo = MainFragmentRepository(requestInServerNodeRef)
            val application = requireNotNull(this.activity).application
            val factory = MainFragmentViewModelFactory(application, repo)
            viewModel = ViewModelProvider(this, factory)[MainFragmentViewModel::class.java]




        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        binding.logInLogOutBtn.setOnClickListener {
            viewModel?.setServerInDb(baseReference.child(userId))
        }

        viewModel?.callBack?.observe(viewLifecycleOwner) {
            if (it == STATUSES.success) {
                showSnackbar(binding.root, getString(R.string.long_status_changed))
            }
        }

        binding.locationSw.setOnCheckedChangeListener { compoundButton, b ->
            viewModel?.setIsChecked(b)
            if (checkGps(context) && b) {
                getLocationPermission()
            } else if (!checkGps(context)) {
                showSnackbar(binding.root, getString(R.string.turn_on_gps))
                viewModel?.setIsChecked(false)
            }
        }

        viewModel?.isChecked?.observe(viewLifecycleOwner) {
            if (it == false) {
                stopLocationUpdateService()
            }
        }

        viewModel?.pendingRequestInServer?.observe(viewLifecycleOwner) { remoteRequestPartialInfo ->

            remoteRequestPartialInfo?.let { it ->
                currentOwnerOfRequestId = remoteRequestPartialInfo.userId.toString()
                currentRequestId = it.requestId!!
                val reference = dbReferenceForUsers.child(it.userId!!).child("requests")
                requestInUserNodeRef = reference.child(it.requestId)
                viewModel?.listenForPendingRequestDetails(reference, it.requestId)
            }
        }

        viewModel?.isLocationPermissionGranted?.observe(viewLifecycleOwner, Observer { isGranted ->
            if (isGranted) {
                callLocationUpdateService(currentOwnerOfRequestId, currentRequestId)
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }

        })

            viewModel?.navigateToProfileFragment?.observe(viewLifecycleOwner){
                if (it){
                    this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToProfileFragment())
                    viewModel?.setNavigateToProfileFragment(false)
                }
            }

            binding.toolBarDetails.apply {
                setNavigationOnClickListener(View.OnClickListener {
                    viewModel?.setNavigateToProfileFragment(true)
                })
            }

            viewModel?.checkServerStatusInDb(baseReference.child(userId))

            binding.markCompleteBtn.setOnClickListener {
                showAlertDialog(getString(R.string.alert),getString(R.string.want_mark_complete),activity){
                    viewModel?.changeRequestStatus(requestInUserNodeRef,requestInServerNodeRef, STATUS.Finished.name)
                }?.show()
            }

            viewModel?.requestStatusChanged?.observe(viewLifecycleOwner){
                showSnackbar(binding.root,getString(R.string.request_status_changed_to, it))
            }

            binding.markCanceledBtn.setOnClickListener {
                showAlertDialog(getString(R.string.alert),getString(R.string.want_mark_canceled),activity){
                    viewModel?.changeRequestStatus(requestInUserNodeRef,requestInServerNodeRef, STATUS.Canceled.name)
                }?.show()
            }

    }

        return binding.root
    }

    private fun stopLocationUpdateService() {
        context?.stopService(Intent(activity,LocationUpdateService::class.java))
    }

    private fun callLocationUpdateService(userId: String, currentRequestId: String) {

        context?.let {
            val intent = Intent(it, LocationUpdateService::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("requestId", currentRequestId)

            ContextCompat.startForegroundService(it,intent)
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            viewModel?.setIsLocationPermissionGranted(true)

        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

}