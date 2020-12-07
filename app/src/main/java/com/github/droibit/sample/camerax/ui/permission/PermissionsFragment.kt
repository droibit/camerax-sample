package com.github.droibit.sample.camerax.ui.permission

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.droibit.sample.camerax.ui.permission.PermissionsFragmentDirections.Companion.toCameraFragment
import com.github.droibit.sample.camerax.utils.checkCameraPermissionGranted
import com.github.droibit.sample.camerax.utils.showCameraPermissionErrorToast

class PermissionsFragment : Fragment() {

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            findNavController().navigate(toCameraFragment())
        } else {
            showCameraPermissionErrorToast()
            requireActivity().finish()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FrameLayout(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            setBackgroundColor(Color.WHITE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            if (checkCameraPermissionGranted()) {
                findNavController().navigate(toCameraFragment())
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}