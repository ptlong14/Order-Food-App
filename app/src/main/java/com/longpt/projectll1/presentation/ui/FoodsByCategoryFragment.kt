package com.longpt.projectll1.presentation.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.FoodRepositoryImpl
import com.longpt.projectll1.databinding.FragmentFoodListBinding
import com.longpt.projectll1.domain.usecase.GetFoodsByCategoryUC
import com.longpt.projectll1.presentation.adapter.FoodListAdapter
import com.longpt.projectll1.presentation.factory.FoodCategoryViewModelFactory
import com.longpt.projectll1.presentation.viewModel.FoodCategoryViewModel
import kotlinx.coroutines.launch

class FoodsByCategoryFragment : Fragment() {
    companion object {
        private const val ARG_CATEGORY = "category"
        fun newInstance(category: String): FoodsByCategoryFragment {
            val fragment = FoodsByCategoryFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY, category)
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var category: String
    lateinit var binding: FragmentFoodListBinding
    private lateinit var foodCategoryAdapter: FoodListAdapter
    lateinit var viewModel: FoodCategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            category = it.getString(ARG_CATEGORY, "")
        }
        val repo = FoodRepositoryImpl(FirestoreDataSource())
        val useCase = GetFoodsByCategoryUC(repo)
        val factory = FoodCategoryViewModelFactory(useCase)
        viewModel = ViewModelProvider(this, factory)[FoodCategoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        foodCategoryAdapter = FoodListAdapter(emptyList()) { food ->
            val context = requireContext()
            val intent = Intent(context, DetailFoodActivity::class.java).apply {
                putExtra("foodId", food.id)
            }

            val options = ActivityOptions.makeCustomAnimation(
                context,
                R.anim.slide_in_right,
                R.anim.fade_out
            )
            context.startActivity(intent, options.toBundle())
        }
        binding.rvListFood.layoutManager = LinearLayoutManager(requireContext())
        binding.rvListFood.adapter = foodCategoryAdapter

        viewModel.getFoodsByCategory(category)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.foods.collect { result ->
                when (result) {
                    is TaskResult.Loading -> {
                        Toast.makeText(requireContext(), "Loading food", Toast.LENGTH_SHORT).show()
                    }

                    is TaskResult.Error -> {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }

                    is TaskResult.Success -> {
                        foodCategoryAdapter.updateData(result.data)
                    }
                }
            }
        }

    }
}