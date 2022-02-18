package com.example.funlauncher.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.funlauncher.databinding.ItemAppBinding
import com.example.funlauncher.databinding.ItemCustomizedAppBinding
import com.example.funlauncher.model.FinalAppModel
import javax.inject.Inject

class CustomAppListAdapter @Inject constructor() :
    ListAdapter<FinalAppModel, CustomAppListAdapter.AppsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
        val binding =
            ItemCustomizedAppBinding.inflate(
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

    inner class AppsViewHolder(private val binding: ItemCustomizedAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FinalAppModel) {

            binding.apply {
                appIconImg.setImageDrawable(item.appIcon)
                appNameTxt.setText(item.appName)

                if (item.customAppIcon == null) {
                    customAppIconImg.setImageDrawable(item.appIcon)
                } else {
                    customAppIconImg.setImageBitmap(item.customAppIcon)
                }

                if (item.customAppName == null) {
                    customAppNameTxt.text = item.appName
                } else {
                    customAppNameTxt.text = item.customAppName
                }

                parentLayout.setOnLongClickListener {
                    onLongClickListener?.let {
                        it(item)
                    }
                    true
                }
            }
        }
    }

    private var onLongClickListener: ((FinalAppModel) -> Unit)? = null
    fun setOnLongClickListener(finalAppModel: (FinalAppModel) -> Unit) {
        onLongClickListener = finalAppModel
    }

    class DiffCallback : DiffUtil.ItemCallback<FinalAppModel>() {
        override fun areItemsTheSame(oldItem: FinalAppModel, newItem: FinalAppModel) =
            oldItem.appPackageName == newItem.appPackageName

        override fun areContentsTheSame(oldItem: FinalAppModel, newItem: FinalAppModel) =
            oldItem == newItem
    }
}