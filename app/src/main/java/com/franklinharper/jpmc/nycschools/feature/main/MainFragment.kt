package com.franklinharper.jpmc.nycschools.feature.main

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.franklinharper.jpmc.nycschools.R
import com.franklinharper.jpmc.nycschools.databinding.FragmentMainBinding
import com.franklinharper.jpmc.nycschools.data.domain.HighSchoolWithSatScores
import com.laimiux.lce.fold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMainBinding.bind(view)
        val schoolAdapter = MainAdapter(
            onItemClick = { highSchoolWithSatScores: HighSchoolWithSatScores ->
                findNavController()
                    .navigate(
                        MainFragmentDirections.mainFragmentToDetailFragment(
                            highSchoolWithSatScores.dbn
                        )
                    )
            }
        )
        binding.list.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                /* reverseLayout = */ false
            )
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = schoolAdapter
        }
        viewModel.schoolResults.observe(viewLifecycleOwner) { event ->
            event.fold(
                onLoading = {
                    binding.spinner.isVisible = true
                    binding.errorMessage.isVisible = false
                    binding.list.isVisible = false
                },
                onError = {
                    binding.spinner.isVisible = false
                    binding.errorMessage.isVisible = true
                    binding.list.isVisible = false
                },
                onContent = { highSchoolsWithSatScoresList ->
                    // The School data set is static; so I will skip implementing an "empty View"
                    binding.spinner.isVisible = false
                    binding.errorMessage.isVisible = false
                    binding.list.isVisible = true
                    schoolAdapter.submitList(highSchoolsWithSatScoresList)
                }
            )
        }
    }
}