package com.franklinharper.jpmc.nycschools.feature.detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.franklinharper.jpmc.nycschools.R
import com.franklinharper.jpmc.nycschools.common.NA
import com.franklinharper.jpmc.nycschools.databinding.FragmentDetailBinding
import com.laimiux.lce.fold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val viewModel: DetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DetailFragmentArgs by navArgs()
        val binding = FragmentDetailBinding.bind(view)
        viewModel.loadData(args.dbn)
        viewModel.schoolResult.observe(viewLifecycleOwner) { event ->
            event.fold(
                onLoading = {
                    binding.spinner.isVisible = true
                    binding.errorMessage.isVisible = false
                    binding.details.isVisible = false
                },
                onError = {
                    binding.spinner.isVisible = false
                    binding.errorMessage.isVisible = true
                    binding.details.isVisible = false
                },
                onContent = { school ->
                    binding.spinner.isVisible = false
                    binding.errorMessage.isVisible = false
                    binding.details.isVisible = true
                    binding.schoolName.text = school.name
                    binding.websiteValue.text = school.website
                    binding.mathValue.text = school.mathSatAverageScore?.toString() ?: NA
                    binding.writingValue.text = school.writingSatAverageScore?.toString() ?: NA
                    binding.readingValue.text = school.readingSatAverageScore?.toString() ?: NA
                }
            )
        }
    }
}