package com.longpt.projectll1.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.FoodRepositoryImpl
import com.longpt.projectll1.databinding.ActivityDetailFoodBinding
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.usecase.AddToFavoriteUC
import com.longpt.projectll1.domain.usecase.GetFavFoodsUC
import com.longpt.projectll1.domain.usecase.GetFoodByIdUC
import com.longpt.projectll1.domain.usecase.RemoveFromFavoriteUC
import com.longpt.projectll1.presentation.factory.FavFoodsViewModelFactory
import com.longpt.projectll1.presentation.factory.FoodDetailViewModelFactory
import com.longpt.projectll1.presentation.viewModel.FavoriteFoodViewModel
import com.longpt.projectll1.presentation.viewModel.FoodDetailViewModel
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.ShareScreenshot
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DetailFoodActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailFoodBinding
    private lateinit var tvDescription: TextView
    private lateinit var fullText: String

    private lateinit var detailViewModel: FoodDetailViewModel
    private lateinit var favViewModel: FavoriteFoodViewModel
    private val currentUser get() = FirebaseAuth.getInstance().currentUser

    private lateinit var foodId: String
    private lateinit var food: Food

    companion object {
        private const val EXTRA_FOOD_ID = "foodId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        foodId = intent.getStringExtra(EXTRA_FOOD_ID) ?: ""

        val repo = FoodRepositoryImpl(FirestoreDataSource())
        val getFoodByIdUC = GetFoodByIdUC(repo)
        val getFavFoodsUC = GetFavFoodsUC(repo)
        val removeFavoriteUC = RemoveFromFavoriteUC(repo)
        val addToFavoriteUC = AddToFavoriteUC(repo)
        val detailFactory = FoodDetailViewModelFactory(getFoodByIdUC)
        val favFactory = FavFoodsViewModelFactory(getFavFoodsUC, addToFavoriteUC, removeFavoriteUC)

        detailViewModel = ViewModelProvider(this, detailFactory)[FoodDetailViewModel::class.java]
        favViewModel = ViewModelProvider(this, favFactory)[FavoriteFoodViewModel::class.java]

        detailViewModel.getFoodById(foodId)
        currentUser?.uid?.let { favViewModel.observeFavFoods(it) }

        setupObservers()
        setupActions()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_right)
    }


    @SuppressLint("SetTextI18n")
    private fun setupObservers() {

        lifecycleScope.launch {
            combine(
                detailViewModel.food,
                favViewModel.favoriteFoods,
            ) { foodResult, favResult ->
                foodResult to foodResult
            }.collect { (foodResult, favResult) ->
                when (foodResult) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Success -> {
                        food = foodResult.data

                        binding.tvFoodName.text = food.name
                        binding.tvPrice.text = FormatUtil.moneyFormat(food.price)
                        Glide.with(binding.imgFood.context).load(food.imgUrl).into(binding.imgFood)
                        binding.tvCntSold.text = "Đã bán: ${food.sold}"
                        tvDescription = binding.tvDescription
                        fullText = food.description
                        setCollapsedDescription()

                        if (favResult is TaskResult.Success) {
                            val isFav = favViewModel.isFoodFav(food.id)
                            binding.iBtnFavorite.setImageResource(
                                if (isFav) R.drawable.im_fav else R.drawable.im_not_fav
                            )
                        }
                    }

                    is TaskResult.Error -> {
                        foodResult.exception.message?.showToast(this@DetailFoodActivity)
                    }
                }
            }
        }

        lifecycleScope.launch {
            favViewModel.addFavoriteState.collect { result ->
                when (result) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Success -> ("Đã thêm vào yêu thích").showToast(this@DetailFoodActivity)
                    is TaskResult.Error -> ("Thêm thất bại: ${result.exception.message}").showToast(this@DetailFoodActivity)
                }
            }
        }

        lifecycleScope.launch {
            favViewModel.removeFavoriteState.collect { result ->
                when (result) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Success -> ("Đã xóa khỏi yêu thích").showToast(this@DetailFoodActivity)
                    is TaskResult.Error -> ("Xóa thất bại: ${result.exception.message}").showToast(this@DetailFoodActivity)
                }
            }
        }
    }

    private fun setupActions() {
        binding.iBtnFavorite.setOnClickListener {
            if(currentUser==null){
                "Vui lòng đăng nhập để sử dụng chức năng này".showToast(this)
                return@setOnClickListener
            }
            val userId = currentUser!!.uid
            val isFav = favViewModel.isFoodFav(food.id)
            if (isFav) {
                favViewModel.removeFavorite(foodId, userId)
            } else {
                favViewModel.addFavorite(food, userId)
            }
        }

        binding.iBtnBack.setOnClickListener { finish() }

        binding.iBtnShare.setOnClickListener {
            ShareScreenshot.captureAndShare(this, binding.root)
        }

        binding.iBtnAddToCart.setOnClickListener {
            val bts = BottomSheetFood.newInstance(food.id)
            bts.show(supportFragmentManager, "BottomSheetFood")
        }
    }

    private fun setCollapsedDescription() {
        val maxChars = 120
        if (fullText.length <= maxChars) {
            tvDescription.text = fullText
            return
        }
        val truncated = fullText.substring(0, maxChars).trim() + "... Xem thêm"
        val spannable = SpannableString(truncated)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) = setExpandedDescription()
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = tvDescription.context.getColor(android.R.color.holo_blue_dark)
                ds.isUnderlineText = false
            }
        }
        spannable.setSpan(
            clickableSpan,
            truncated.length - "Xem thêm".length,
            truncated.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvDescription.text = spannable
        tvDescription.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setExpandedDescription() {
        val expandedText = "$fullText Thu gọn"
        val spannable = SpannableString(expandedText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) = setCollapsedDescription()
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = tvDescription.context.getColor(android.R.color.holo_blue_dark)
                ds.isUnderlineText = false
            }
        }
        spannable.setSpan(
            clickableSpan,
            expandedText.length - "Thu gọn".length,
            expandedText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvDescription.text = spannable
        tvDescription.movementMethod = LinkMovementMethod.getInstance()
    }
}