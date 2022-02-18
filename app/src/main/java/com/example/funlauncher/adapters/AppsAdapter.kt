package com.example.funlauncher.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.funlauncher.databinding.ItemAppBinding
import com.example.funlauncher.model.FinalAppModel
import javax.inject.Inject

class AppsAdapter @Inject constructor() :
    ListAdapter<FinalAppModel, AppsAdapter.AppsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
        val binding =
            ItemAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AppsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class AppsViewHolder(private val binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FinalAppModel) {
            binding.appNameTxt.text =
                item.customAppName ?: item.appName

            if (item.customAppIcon == null)
                binding.appIconImg.setImageDrawable(item.appIcon)
            else
                binding.appIconImg.setImageBitmap(item.customAppIcon)

            binding.appNameTxt.setOnClickListener {
                onAppNameClickListener?.let {
                    it(item)
                }
            }
            binding.appNameTxt.setOnLongClickListener {
                onAppNameLongClickListener?.let {
                    it(item)
                }
                true
            }
        }
    }

    private var onAppNameClickListener: ((FinalAppModel) -> Unit)? = null
    fun setAppNameClickListener(appInfo: (FinalAppModel) -> Unit) {
        onAppNameClickListener = appInfo
    }

    private var onAppNameLongClickListener: ((FinalAppModel) -> Unit)? = null
    fun setAppNameLongClickListener(appInfo: (FinalAppModel) -> Unit) {
        onAppNameLongClickListener = appInfo
    }

//    private var onAppIconLongClickListener: ((AppInfoModel) -> Unit)? = null
//    fun setAppIconLongClickListener(appInfo: (AppInfoModel) -> Unit) {
//        onAppIconLongClickListener = appInfo
//    }

    class DiffCallback : DiffUtil.ItemCallback<FinalAppModel>() {
        override fun areItemsTheSame(oldItem: FinalAppModel, newItem: FinalAppModel) =
            oldItem.appPackageName == newItem.appPackageName

        override fun areContentsTheSame(oldItem: FinalAppModel, newItem: FinalAppModel) =
            oldItem == newItem
    }
}