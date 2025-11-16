package com.longpt.projectll1.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.FoodRepositoryImpl
import com.longpt.projectll1.data.sharedPref.UserStorage
import com.longpt.projectll1.databinding.BottomSheetAddupRatingBinding
import com.longpt.projectll1.domain.model.Rating
import com.longpt.projectll1.domain.usecase.AddRatingUC
import com.longpt.projectll1.domain.usecase.GetRatingListByFoodIdUC
import com.longpt.projectll1.presentation.factory.RatingViewModelFactory
import com.longpt.projectll1.presentation.viewModel.RatingOrderViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class BottomSheetRatingOrder : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetAddupRatingBinding
    lateinit var ratingViewModel: RatingOrderViewModel
    lateinit var foodId: String

    companion object {
        private const val ARG_FOOD_ID = "foodId"

        fun newInstance(foodId: String): BottomSheetRatingOrder {
            val fragment = BottomSheetRatingOrder()
            val args = Bundle()
            args.putString(ARG_FOOD_ID, foodId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repoRating = FoodRepositoryImpl(FirestoreDataSource())
        val addRatingUC = AddRatingUC(repoRating)
        val getRatingListByFoodIdUC = GetRatingListByFoodIdUC(repoRating)
        val ratingFactory = RatingViewModelFactory(
            addRatingUC, getRatingListByFoodIdUC
        )
        ratingViewModel = ViewModelProvider(this, ratingFactory)[RatingOrderViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetAddupRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ratingBar.rating = 5f
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            val ratingStar = binding.ratingBar.rating.toDouble()
            val comment = binding.edtComment.text.toString()
            val foodId = arguments?.getString(ARG_FOOD_ID) ?: ""
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val username = UserStorage.getUserName(requireContext())
            val avatarUser = UserStorage.getAvatar(requireContext())
            val rating = Rating(userId, username, avatarUser, ratingStar, comment)
            ratingViewModel.addRating(rating, foodId, userId)
        }
        lifecycleScope.launch {
            ratingViewModel.addRatingResult.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Error -> res.exception.message?.showToast(requireContext())

                    is TaskResult.Success -> {
                        "Đánh giá thành công".showToast(requireContext())
                        dismiss()
                    }
                }
            }
        }
    }
}