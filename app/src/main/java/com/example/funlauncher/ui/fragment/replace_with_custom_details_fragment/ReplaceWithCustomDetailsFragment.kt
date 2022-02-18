package com.example.funlauncher.ui.fragment.replace_with_custom_details_fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.funlauncher.R
import com.example.funlauncher.databinding.FragmentReplaceWithCustomDetailsBinding
import com.example.funlauncher.model.FinalAppModel
import com.example.funlauncher.room.model.AppDbModel
import com.example.funlauncher.ui.shared_view_models.AppsDbViewModel
import com.example.funlauncher.ui.shared_view_models.ShowAppsViewModel
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "ReplaceWithCustomFragment"

@AndroidEntryPoint
class ReplaceWithCustomDetailsFragment : Fragment() {

    private var _binding: FragmentReplaceWithCustomDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ReplaceWithCustomDetailsFragmentArgs>()
    private val showAppsViewModel: ShowAppsViewModel by activityViewModels()
    private val appsDbViewModel: AppsDbViewModel by activityViewModels()

    private var finalAppModel: FinalAppModel? = null
    private var bitmap: Bitmap? = null

    private var launchGalleryActivityForImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUrl: Uri? = result.data?.data
                // your operation...
                imageUrl?.let {
                    bitmap = when {
                        Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            it
                        )
                        else -> {
                            val source =
                                ImageDecoder.createSource(requireContext().contentResolver, it)
                            ImageDecoder.decodeBitmap(source)
                        }
                    }

                    binding.customAppIconImg.setImageBitmap(bitmap)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReplaceWithCustomDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        finalAppModel = showAppsViewModel.getFinalAppModelFromPackageName(args.packageName)

        if (finalAppModel == null) {
            findNavController().popBackStack()
            return
        }
        setUpDefaultData()
        openGalleryForIcon()
        btnClick()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Log.d(TAG, "onViewCreated: called")
            findNavController().popBackStack()
        }
    }

    private fun btnClick() {
        binding.customAppIconImg.setOnLongClickListener {
            binding.customAppIconImg.setImageResource(R.drawable.ic_error_placeholder)
            bitmap = null
            true
        }
        binding.saveBtn.setOnClickListener {
            val appDbModel = AppDbModel(
                appPackageName = args.packageName,
                customAppName = binding.customAppNameTxt.editText?.text.toString(),
                customAppIcon = bitmap
            )
            appsDbViewModel.addAppDbModel(appDbModel)
            findNavController().navigate(R.id.action_replaceWithCustomDetailsFragment_to_appsFragment)
        }

        binding.cancelBtn.setOnClickListener {
            findNavController().navigate(R.id.action_replaceWithCustomDetailsFragment_to_appsFragment)
        }
    }

    private fun openGalleryForIcon() {
        binding.customAppIconImg.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivityForImage.launch(intent)
        }
    }

    private fun setUpDefaultData() {
        binding.apply {
            originalAppIconImg.setImageDrawable(finalAppModel?.appIcon)
            originalAppNameTxt.editText?.setText(finalAppModel?.appName)

            finalAppModel?.customAppIcon?.let {
                customAppIconImg.setImageBitmap(it)
                bitmap = it
            }
            finalAppModel?.customAppName?.let {
                customAppNameTxt.editText?.setText(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}