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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.FoodRepositoryImpl
import com.longpt.projectll1.databinding.FragmentFavoriteFoodsBinding
import com.longpt.projectll1.domain.usecase.AddToFavoriteUC
import com.longpt.projectll1.domain.usecase.GetFavFoodsUC
import com.longpt.projectll1.domain.usecase.RemoveFromFavoriteUC
import com.longpt.projectll1.presentation.adapter.FavFoodsAdapter
import com.longpt.projectll1.presentation.factory.FavFoodsViewModelFactory
import com.longpt.projectll1.presentation.viewModel.FavoriteFoodViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {
    lateinit var binding: FragmentFavoriteFoodsBinding
    lateinit var favViewModel: FavoriteFoodViewModel
    private val currentUser get() = FirebaseAuth.getInstance().currentUser

    private val userId = currentUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repoFood = FoodRepositoryImpl(FirestoreDataSource())
        val getFavFoodsUC = GetFavFoodsUC(repoFood)
        val addToFavoriteUC = AddToFavoriteUC(repoFood)
        val removeFavoriteUC = RemoveFromFavoriteUC(repoFood)

        val factory = FavFoodsViewModelFactory(getFavFoodsUC, addToFavoriteUC, removeFavoriteUC)
        favViewModel = ViewModelProvider(requireActivity(), factory)[FavoriteFoodViewModel::class.java]
        favViewModel.observeFavFoods(userId)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteFoodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favFoodsAdapter = FavFoodsAdapter(favFoods = mutableListOf(), onClickFavFood = { food ->
            val context = requireContext()
            val intent = Intent(context, DetailFoodActivity::class.java).apply {
                putExtra("foodId", food.id)
            }
            val options = ActivityOptions.makeCustomAnimation(
                context, R.anim.slide_in_right, R.anim.fade_out
            )
            context.startActivity(intent, options.toBundle())
        }, onClickFavIcon = { food ->

                val userId = currentUser?.uid ?: return@FavFoodsAdapter
                val isFav = favViewModel.isFoodFav(food.id)
                if (isFav) {
                    favViewModel.removeFavorite(food.id, userId)
                } else {
                    favViewModel.addFavorite(food, userId)
                }
        })
        binding.recyclerViewFavorites.adapter = favFoodsAdapter
        binding.recyclerViewFavorites.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        binding.swipeRefreshFavorites.setOnRefreshListener {
            favViewModel.observeFavFoods(currentUser!!.uid)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            favViewModel.favoriteFoods.collect { result ->
                when (result){
                    is TaskResult.Loading -> {
                        binding.swipeRefreshFavorites.isRefreshing = true
                    }
                    is TaskResult.Success -> {
                        binding.swipeRefreshFavorites.isRefreshing = false
                        favFoodsAdapter.updateData(result.data)
                    }
                    is TaskResult.Error -> {
                        binding.swipeRefreshFavorites.isRefreshing = false
                        favFoodsAdapter.updateData(emptyList())
                        Toast.makeText(requireContext(), result.exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            favViewModel.addFavoriteState.collect { result ->
                when (result) {
                    is TaskResult.Loading -> ("Loading").showToast(requireContext())
                    is TaskResult.Success -> ("Đã thêm vào yêu thích").showToast(requireContext())
                    is TaskResult.Error -> ("Thêm thất bại").showToast(requireContext())
                }
            }
        }

        lifecycleScope.launch {
            favViewModel.removeFavoriteState.collect { result ->
                when (result) {
                    is TaskResult.Loading -> ("Loading").showToast(requireContext())
                    is TaskResult.Success -> ("Đã xóa khỏi yêu thích").showToast(requireContext())
                    is TaskResult.Error -> ("Xóa thất bại").showToast(requireContext())
                }
            }
        }
    }
}
