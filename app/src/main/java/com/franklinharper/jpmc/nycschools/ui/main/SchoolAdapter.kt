package com.franklinharper.jpmc.nycschools.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.franklinharper.jpmc.nycschools.HighSchoolWithSatScores
import com.franklinharper.jpmc.nycschools.databinding.ItemSchoolBinding

class SchoolAdapter :
    ListAdapter<HighSchoolWithSatScores, HighSchoolItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighSchoolItemViewHolder {
        val binding = ItemSchoolBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            /* attachToParent */
            false,
        )
        return HighSchoolItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HighSchoolItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HighSchoolWithSatScores>() {

            override fun areItemsTheSame(
                oldItem: HighSchoolWithSatScores,
                newItem: HighSchoolWithSatScores
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HighSchoolWithSatScores,
                newItem: HighSchoolWithSatScores
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class HighSchoolItemViewHolder(
    private val binding: ItemSchoolBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(school: HighSchoolWithSatScores) {
        binding.schoolName.text = school.name
        binding.websiteValue.text = school.website
        binding.mathValue.text = school.mathSatAverageScore ?: "N/A"
        binding.writingValue.text = school.writingSatAverageScore ?: "N/A"
        binding.readingValue.text = school.writingSatAverageScore ?: "N/A"
    }
}