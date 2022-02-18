package com.example.funlauncher.ui.fragment.custom_apps_list_fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.funlauncher.R
import com.example.funlauncher.adapters.CustomAppListAdapter
import com.example.funlauncher.databinding.FragmentCustomAppsListBinding
import com.example.funlauncher.room.model.AppDbModel
import com.example.funlauncher.ui.fragment.app_fragment.TAG
import com.example.funlauncher.ui.shared_view_models.AppsDbViewModel
import com.example.funlauncher.ui.shared_view_models.ShowAppsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomAppsListFragment : Fragment() {

    private var _binding: FragmentCustomAppsListBinding? = null
    private val binding get() = _binding!!

    private val showAppsViewModel: ShowAppsViewModel by activityViewModels()
    private val appsDbViewModel: AppsDbViewModel by viewModels()
    private var isItemDeleted = false

    @Inject
    lateinit var customAppListAdapter: CustomAppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomAppsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        showAppsViewModel.defaultAndCustomAvailableApps.observe(viewLifecycleOwner) {
            if (isItemDeleted) {
                if (it.first != null && it.second != null && it.first!!.isNotEmpty()) {
                    showAppsViewModel.getFinalAppModel(it.first!!, it.second!!.toMutableList())
                }
                isItemDeleted = false
            }
        }

        showAppsViewModel.finalAppList.observe(viewLifecycleOwner) {
            customAppListAdapter.submitList(it.filter { finalAppModel -> finalAppModel.isModified })
        }
    }

    private fun setUpRecyclerView() {
        binding.customAppListRecyclerView.adapter = customAppListAdapter
        customAppListAdapter.setOnLongClickListener {
            appsDbViewModel.deleteAppDbModel(
                AppDbModel(
                    it.appPackageName,
                    it.customAppName,
                    it.customAppIcon
                )
            )

            isItemDeleted = true
            Toast.makeText(
                requireContext(),
                "Custom data removed from that app",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}