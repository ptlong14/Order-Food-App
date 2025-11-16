package com.longpt.projectll1.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.CartRepositoryImpl
import com.longpt.projectll1.data.repositoryImpl.FoodRepositoryImpl
import com.longpt.projectll1.databinding.BottomSheetFoodBinding
import com.longpt.projectll1.domain.model.CartItem
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.usecase.AddToCartUC
import com.longpt.projectll1.domain.usecase.GetCartUC
import com.longpt.projectll1.domain.usecase.GetFoodByIdUC
import com.longpt.projectll1.domain.usecase.RemoveItemFromCartUC
import com.longpt.projectll1.domain.usecase.UpdateCartItemQuantityUC
import com.longpt.projectll1.presentation.adapter.OptionGroupAdapter
import com.longpt.projectll1.presentation.factory.CartViewModelFactory
import com.longpt.projectll1.presentation.factory.FoodDetailViewModelFactory
import com.longpt.projectll1.presentation.viewModel.CartViewModel
import com.longpt.projectll1.presentation.viewModel.FoodDetailViewModel
import com.longpt.projectll1.utils.AlertUtils
import com.longpt.projectll1.utils.FormatUtil
import com.longpt.projectll1.utils.GenerateUtil
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class BottomSheetFood : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetFoodBinding
    private lateinit var foodId: String
    lateinit var detailViewModel: FoodDetailViewModel
    lateinit var cartViewModel: CartViewModel
    lateinit var optionGroupAdapter: OptionGroupAdapter
    lateinit var food: Food
    private val currentUser get() = FirebaseAuth.getInstance().currentUser

    companion object {
        private const val ARG_FOOD_ID = "foodId"
        fun newInstance(foodId: String): BottomSheetFood {
            val fragment = BottomSheetFood()
            val args = Bundle()
            args.putString(ARG_FOOD_ID, foodId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            foodId = it.getString(ARG_FOOD_ID, "")
        }
        val repoFood = FoodRepositoryImpl(FirestoreDataSource())
        val getFoodByIdUC = GetFoodByIdUC(repoFood)

        val foodFactory = FoodDetailViewModelFactory(
            getFoodByIdUC
        )
        detailViewModel = ViewModelProvider(this, foodFactory)[FoodDetailViewModel::class.java]

        val repoCart = CartRepositoryImpl(FirestoreDataSource())
        val addToCartUC = AddToCartUC(repoCart)
        val removeItemFromCartUC = RemoveItemFromCartUC(repoCart)
        val getCartUC = GetCartUC(repoCart)
        val updateCartItemQuantityUC = UpdateCartItemQuantityUC(repoCart)

        val cartFactory =
            CartViewModelFactory(
                getCartUC,
                addToCartUC,
                removeItemFromCartUC,
                updateCartItemQuantityUC
            )
        cartViewModel = ViewModelProvider(this, cartFactory)[CartViewModel::class.java]

        detailViewModel.getFoodById(foodId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainContainer.visibility = View.INVISIBLE

        arguments?.getString(ARG_FOOD_ID)
        optionGroupAdapter = OptionGroupAdapter(emptyList()) { groupPosition, itemPosition ->
            detailViewModel.toggleOption(groupPosition, itemPosition)
        }
        binding.rcOptionGroup.layoutManager = LinearLayoutManager(requireContext())
        binding.rcOptionGroup.adapter = optionGroupAdapter
        binding.rcOptionGroup.itemAnimator = null

        viewLifecycleOwner.lifecycleScope.launch {
            detailViewModel.food.collect { result ->
                when (result) {
                    is TaskResult.Loading -> {
                    }

                    is TaskResult.Error -> {
                        result.exception.message?.showToast(requireContext())
                    }

                    is TaskResult.Success -> {
                        val newFood = result.data
                        optionGroupAdapter.updateData(newFood.optionGroup)
                        food = newFood
                        binding.tvFoodName.text = food.name
                        Glide.with(binding.imgFood.context).load(food.imgUrl).into(binding.imgFood)
                        binding.tvCategory.text = food.category
                        binding.tvPrice.text = FormatUtil.moneyFormat(food.price)
                        optionGroupAdapter.updateData(result.data.optionGroup)
                        binding.mainContainer.visibility = View.VISIBLE
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            detailViewModel.quantity.collect { quantity ->
                binding.tvQuantity.text = quantity.toString()
                updateQuantityButtons(quantity)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.addCartState.collect { result ->
                when (result) {
                    is TaskResult.Loading -> {
                    }

                    is TaskResult.Success -> {
                        "Đã thêm vào giỏ hàng".showToast(requireContext())
                        dismiss()
                    }

                    is TaskResult.Error -> {
                        "Thêm vào giỏ hàng thất bại: ${result.exception.message}".showToast(
                            requireContext()
                        )
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            detailViewModel.totalPrice.collect { price ->
                binding.tvPrice.text = FormatUtil.moneyFormat(price)
            }
        }
        detailViewModel.canAddToCart.observe(viewLifecycleOwner) { canAddToCart ->
            binding.btnAddToCart.isEnabled = canAddToCart
            binding.btnAddToCart.alpha = if (canAddToCart) 1.0f else 0.3f
        }
        binding.btnDecrease.setOnClickListener {
            detailViewModel.decreaseQuantity()
        }
        binding.btnIncrease.setOnClickListener {
            detailViewModel.increaseQuantity()
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        binding.btnAddToCart.setOnClickListener {
            if (currentUser == null) {
                AlertUtils.showLoginAlert(requireContext()) {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.putExtra("from", "client")
                    startActivity(intent)
                }
            } else {
                val userId = currentUser!!.uid
                if (detailViewModel.canAddToCart.value == true) {
                    val optionList = detailViewModel.getSelectedOptionDescriptions()
                    val optionString =
                        optionList.joinToString(", ") { it.substringAfter(": ").trim() }
                    val cartItemId = GenerateUtil.generateCartItemId(food.id, optionString)
                    val cartItem = CartItem(
                        cartItemId,
                        food.id,
                        food.name,
                        food.imgUrl,
                        detailViewModel.totalPrice.value / detailViewModel.quantity.value,
                        detailViewModel.quantity.value,
                        optionList
                    )
                    cartViewModel.addCart(cartItem, userId)
                } else {
                    "Thêm vào giỏ hàng thất bại".showToast(requireContext())
                }
            }
        }

        binding.btnBuyNow.setOnClickListener {
            if (currentUser == null) {
                AlertUtils.showLoginAlert(requireContext()) {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.putExtra("from", "client")
                    startActivity(intent)
                }
            } else {
                if (detailViewModel.canAddToCart.value == true) {
                    val optionList = detailViewModel.getSelectedOptionDescriptions()
                    val optionString =
                        optionList.joinToString(", ") { it.substringAfter(": ").trim() }
                    val cartItemId = GenerateUtil.generateCartItemId(food.id, optionString)
                    val cartItem = CartItem(
                        cartItemId,
                        food.id,
                        food.name,
                        food.imgUrl,
                        detailViewModel.totalPrice.value / detailViewModel.quantity.value,
                        detailViewModel.quantity.value,
                        optionList
                    )
                    val intent = Intent(requireContext(), CheckOutActivity::class.java)
                    intent.putParcelableArrayListExtra("orderFoodData", arrayListOf(cartItem))
                    startActivity(intent)
                } else {
                    "Không thể mua hàng".showToast(requireContext())
                }
            }
        }
    }

    fun updateQuantityButtons(quantity: Int) {
        val decreaseButton = binding.btnDecrease
        decreaseButton.isEnabled = quantity > 1
        decreaseButton.alpha = if (quantity > 1) 1.0f else 0.3f
    }
}