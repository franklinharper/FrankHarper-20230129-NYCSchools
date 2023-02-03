package com.franklinharper.jpmc.nycschools.feature.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.franklinharper.jpmc.nycschools.common.NA
import com.franklinharper.jpmc.nycschools.data.domain.HighSchoolWithSatScores
import com.franklinharper.jpmc.nycschools.databinding.ItemSchoolBinding


class MainAdapter(private val onItemClick: (
    (HighSchoolWithSatScores) -> Unit)
) : ListAdapter<HighSchoolWithSatScores, MainAdapter.HighSchoolItemViewHolder>(DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighSchoolItemViewHolder {
        val binding = ItemSchoolBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            /* attachToParent = */ false,
        )
        return HighSchoolItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HighSchoolItemViewHolder, position: Int) {
        // Delegating binding to the ViewHolder makes it easier to scale when there are
        // multiple types of ViewHolders.
        //
        // Another solution is to use something like Epoxy. It uses code generation to
        // simplify implementing complex RecyclerViews.
        // For details see: https://github.com/airbnb/epoxy
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HighSchoolWithSatScores>() {

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

    inner class HighSchoolItemViewHolder(
        private val binding: ItemSchoolBinding,
        private val onItemClick: ((HighSchoolWithSatScores) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(adapterPosition))
                }
            }
        }

        fun bind(school: HighSchoolWithSatScores) {
            binding.schoolName.text = school.name
            binding.satTakerPercentageValue.text = school.satTakerPercentage()
            binding.mathValue.text = school.mathSatAverageScore?.toString() ?: NA
            binding.writingValue.text = school.writingSatAverageScore?.toString() ?: NA
            binding.readingValue.text = school.readingSatAverageScore?.toString() ?: NA
        }
    }
}
