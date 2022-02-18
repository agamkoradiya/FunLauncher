package com.example.funlauncher.ui.fragment.option_dialog_fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.funlauncher.R
import com.example.funlauncher.databinding.FragmentOptionsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OptionsBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentOptionsBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<OptionsBottomSheetFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsBottomSheetBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.uninstallTxt.setOnClickListener {
            dismiss()
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:${args.packageName}")
            requireContext().startActivity(intent)
        }

        binding.appInfoTxt.setOnClickListener {
            dismiss()
            val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${args.packageName}")
            requireContext().startActivity(intent)
        }

        binding.changeDetailsTxt.setOnClickListener {
            val action =
                OptionsBottomSheetFragmentDirections.actionOptionsBottomSheetFragmentToReplaceWithCustomDetailsFragment(
                    args.packageName
                )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}