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
import com.longpt.projectll1.data.repositoryImpl.RatingRepositoryImpl
import com.longpt.projectll1.data.sharedPref.UserStorage
import com.longpt.projectll1.databinding.BottomSheetAddupRatingBinding
import com.longpt.projectll1.domain.model.Rating
import com.longpt.projectll1.domain.usecase.AddUpRatingUC
import com.longpt.projectll1.domain.usecase.GetRatingByUserIdUC
import com.longpt.projectll1.domain.usecase.GetRatingListByFoodIdUC
import com.longpt.projectll1.presentation.factory.RatingViewModelFactory
import com.longpt.projectll1.presentation.viewModel.RatingOrderViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class BottomSheetRatingOrder : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetAddupRatingBinding
    lateinit var ratingViewModel: RatingOrderViewModel
    lateinit var foodId: String
    lateinit var userRating: Rating

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
        val repoRating = RatingRepositoryImpl(FirestoreDataSource())
        val addUpRatingUC = AddUpRatingUC(repoRating)
        val getRatingListByFoodIdUC = GetRatingListByFoodIdUC(repoRating)
        val getRatingByUserIdUC = GetRatingByUserIdUC(repoRating)
        val ratingFactory = RatingViewModelFactory(
            addUpRatingUC, getRatingListByFoodIdUC, getRatingByUserIdUC
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
        val foodId = arguments?.getString(ARG_FOOD_ID) ?: ""
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        ratingViewModel.getRatingByUserId(userId, foodId)
        lifecycleScope.launch {
            ratingViewModel.rating.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Error -> {
                        binding.ratingBar.rating = 5f
                        binding.edtComment.setText("")
                    }

                    is TaskResult.Success -> {
                        userRating = res.data
                        binding.ratingBar.rating = userRating.rating.toFloat()
                        binding.edtComment.setText(userRating.comment)
                    }
                }
            }
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            val ratingStar = binding.ratingBar.rating.toDouble()
            val comment = binding.edtComment.text.toString()
            val username = UserStorage.getUserName(requireContext())
            val avatarUser = UserStorage.getAvatar(requireContext())
            val rating = Rating(userId, username, avatarUser, ratingStar, comment)
            ratingViewModel.addUpRating(rating, foodId, userId)
        }
        lifecycleScope.launch {
            ratingViewModel.addUpRatingResult.collect { res ->
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