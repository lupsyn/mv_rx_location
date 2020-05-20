package com.example.challange.features.postcode

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.airbnb.mvrx.*
import com.example.challange.R
import com.example.challange.utility.location.PermissionState
import com.example.challange.utility.permissions.PermissionHandler
import com.example.challange.utility.permissions.PermissionModule
import com.google.android.material.button.MaterialButton

class PostCodeFragment : BaseMvRxFragment() {

    private val permissionHandler = PermissionModule.permissionHandler()

    private val viewModel: PostCodeViewModel by fragmentViewModel()

    private lateinit var postCode: TextView
    private lateinit var coord: TextView
    private lateinit var refreshLocation: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_code, container, false)
            .apply {
                progressBar = findViewById(R.id.progress_bar)
                postCode = findViewById(R.id.postcode)
                coord = findViewById(R.id.coordinates)
                refreshLocation = findViewById(R.id.refresh)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasLocationPermissions()) {
            requestPermission()
        }

        refreshLocation.setOnClickListener { viewModel.requestLocation() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionHandler.handlePermissionsResult(
            PermissionHandler.PermissionsData(
                requestCode,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                permissions,
                grantResults
            ),
            { viewModel.onPermissionChange(PermissionState.Granted) },
            ::handlePermissionDeniedWithNeverAskAgain,
            ::handlePermissionDeny
        )
    }

    private fun handlePermissionDeny(permission: String): Boolean {
        viewModel.onPermissionChange(PermissionState.Denied)
        return false
    }

    private fun handlePermissionDeniedWithNeverAskAgain(permission: String) {
        permissionHandler.executeNeverAskAgainActionIfNeeded(permission) {
            viewModel.onPermissionChange(PermissionState.DeniedForever)
        }
    }

    private fun requestPermission() {
        permissionHandler.requestPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) {}
    }


    override fun invalidate() {
        withState(viewModel) { state ->
            when (state.uiState) {
                is Uninitialized -> {
                    if (hasLocationPermissions()) {
                        viewModel.requestLocation()
                    }
                }
                is Loading -> {
                    showLoader(true)
                    setCoordinatesLabel(getString(R.string.unknown), getString(R.string.unknown))
                    setPostCodeLabel(getString(R.string.unknown))
                }
                is Success -> {
                    showLoader(false)
                    state.uiState.run {
                        setPostCodeLabel(invoke().postCode.postCodeValue.orEmpty())
                        setCoordinatesLabel(
                            invoke().coordinates.latitude.toString(),
                            invoke().coordinates.longitude.toString()
                        )
                    }
                }
                is Fail -> {
                    Toast.makeText(
                        requireContext(),
                        state.uiState.error.message,
                        Toast.LENGTH_LONG
                    ).show()
                    showLoader(false)
                    setPostCodeLabel(getString(R.string.unknown))
                    setCoordinatesLabel(getString(R.string.unknown), getString(R.string.unknown))
                }
            }
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return permissionHandler.hasPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun setPostCodeLabel(input: String) {
        postCode.text = String.format(resources.getString(R.string.postCode), input)
    }

    private fun setCoordinatesLabel(lat: String, long: String) {
        coord.text = String.format(resources.getString(R.string.coordinates), lat, long)
    }

    private fun showLoader(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    companion object {
        val TAG = PostCodeFragment::class.java.canonicalName
    }
}

