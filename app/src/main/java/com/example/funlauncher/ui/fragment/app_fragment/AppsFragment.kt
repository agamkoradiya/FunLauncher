package com.example.funlauncher.ui.fragment.app_fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.funlauncher.R
import com.example.funlauncher.adapters.AppsAdapter
import com.example.funlauncher.broadcast_receiver.PackageReceiver
import com.example.funlauncher.databinding.FragmentAppsBinding
import com.example.funlauncher.ui.shared_view_models.ShowAppsViewModel
import com.example.funlauncher.util.AppCategoryService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

const val TAG = "AppsFragment"

@AndroidEntryPoint
class AppsFragment : Fragment() {

    private var _binding: FragmentAppsBinding? = null
    private val binding get() = _binding!!

    private val showAppsViewModel: ShowAppsViewModel by activityViewModels()

    @Inject
    lateinit var appsAdapter: AppsAdapter

    private val appCategoryService = AppCategoryService()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timeTxt.setOnClickListener {
            Log.d(TAG, "onViewCreated: time btn clicked")
        }
        PackageReceiver.isRefreshNeeded.observe(viewLifecycleOwner) {
            Log.d(TAG, "onViewCreated: isRefreshNeeded - $it")
            if (it)
                showAppsViewModel.getInstalledAppData()
        }

        binding.totalAppsCountTxt.setOnLongClickListener {
            findNavController().navigate(R.id.action_appsFragment_to_customAppsListFragment)
            true
        }

        setUpViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        showAppsViewModel.defaultAvailableApps.observe(viewLifecycleOwner) {
//            appsAdapter.submitList(it.sortedBy { appInfo -> appInfo.appName })
//            binding.totalAppsCountTxt.text = it.size.toString()
        }

        showAppsViewModel.currentTime.observe(viewLifecycleOwner) {
            binding.timeTxt.text = getFormatTimeWithTZ(it)
        }

        showAppsViewModel.defaultAndCustomAvailableApps.observe(viewLifecycleOwner) {
            Log.d(
                TAG,
                "----------- observeViewModel: defaultAndCustomAvailableApps observed -------------"
            )
            Log.d(TAG, "observeViewModel: ${it.first?.size} &  ${it.second?.size}")
            Log.d(TAG, it.first.toString())
            Log.d(TAG, it.second.toString())

            if (it.first != null && it.second != null && it.first!!.isNotEmpty()) {
                showAppsViewModel.getFinalAppModel(it.first!!, it.second!!.toMutableList())
            }
        }

        showAppsViewModel.finalAppList.observe(viewLifecycleOwner) {
            Log.d(TAG, "~~~~~~~~~~~~~~~~~~~~FinalApp List~~~~~~~~~~~~~~~~~~~~~~~ ")
            Log.d(TAG, "observeViewModel: ${it.toString()}")
            Log.d(TAG, "observeViewModel: ${it.size.toString()}")

            it.forEach { finalAppModel ->

                lifecycleScope.launch {
                    val appCategory = appCategoryService.fetchCategory(finalAppModel.appPackageName)
                    Log.d(
                        TAG,
                        "observeViewModel: Category is ${finalAppModel.appName}  - ${appCategory.name}"
                    )
                }
            }

            binding.totalAppsCountTxt.text = it.size.toString()
            appsAdapter.submitList(it.sortedBy { finalAppModel -> finalAppModel.appName })
        }
    }

    private fun getFormatTimeWithTZ(currentTime: Date): String {
        val timeZoneDate = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeZoneDate.format(currentTime)
    }

    private fun setUpViews() {
        binding.appsListRecyclerView.adapter = appsAdapter

        appsAdapter.setAppNameClickListener { finalAppModel ->
            Log.d(TAG, "setUpViews: Clicked On : ${finalAppModel.appName}")

            try {
                requireContext().startActivity(
                    requireContext().packageManager.getLaunchIntentForPackage(
                        finalAppModel.appPackageName
                    )
                )
            } catch (e: Exception) {
//                requireContext().startActivity(
//                    requireContext().packageManager.getLaunchIntentForPackage("com.example.funlauncher")
//                )
                showAppsViewModel.getInstalledAppData()
            }
        }

        appsAdapter.setAppNameLongClickListener { finalAppModel ->
            val action =
                AppsFragmentDirections.actionAppsFragmentToOptionsBottomSheetFragment(finalAppModel.appPackageName)
            findNavController().navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}