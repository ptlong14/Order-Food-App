package com.longpt.projectll1.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.CloudinaryService
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.UserRepositoryImpl
import com.longpt.projectll1.databinding.BottomSheetUpdateUserInforBinding
import com.longpt.projectll1.domain.usecase.GetUserInfoUC
import com.longpt.projectll1.domain.usecase.UpdateAvatarUser
import com.longpt.projectll1.domain.usecase.UpdateUserInforUC
import com.longpt.projectll1.presentation.factory.UserViewModelFactory
import com.longpt.projectll1.presentation.viewModel.UserViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class BottomSheetUpdateUserInfor : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetUpdateUserInforBinding
    private lateinit var userViewModel: UserViewModel
    private val currentUser get() = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid

    private lateinit var field: String
    private lateinit var value: String

    companion object {
        private const val ARG_FIELD = "field"
        private const val ARG_OLD_VALUE = "value"

        fun newInstance(field: String, value: String): BottomSheetUpdateUserInfor {
            val fragment = BottomSheetUpdateUserInfor()
            val args = Bundle()
            args.putString(ARG_FIELD, field)
            args.putString(ARG_OLD_VALUE, value)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repoUser = UserRepositoryImpl(FirestoreDataSource(), CloudinaryService())
        val getUserInfoUC = GetUserInfoUC(repoUser)
        val updateUserInfoUC = UpdateUserInforUC(repoUser)
        val updateAvatarUser= UpdateAvatarUser(repoUser)
        val userFactory = UserViewModelFactory(
            getUserInfoUC, updateUserInfoUC, updateAvatarUser
        )
        userViewModel = ViewModelProvider(this, userFactory)[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetUpdateUserInforBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        field = arguments?.getString(ARG_FIELD) ?: ""
        value = arguments?.getString(ARG_OLD_VALUE) ?: ""

        when (field) {
            "name" -> {
                binding.tvTitleBts.text = "Sửa tên"
                binding.edtValue.hint = "Nhập tên mới"
            }

            "bio" -> {
                binding.tvTitleBts.text = "Sửa mô tả"
                binding.edtValue.hint = "Nhập mô tả mới"
            }

            else -> {}
        }
        binding.edtValue.setText(value)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            value = binding.edtValue.text.toString()
            if (value.isEmpty()) {
                "Hãy điền đầy đủ thông tin".showToast(requireContext())
                return@setOnClickListener
            }
            userViewModel.updateUserInfos(userId, field, value)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.updateInforState.collect { res ->
                    when (res) {
                        is TaskResult.Loading -> {}
                        is TaskResult.Error -> res.exception.message?.showToast(requireContext())
                        is TaskResult.Success -> {
                            "Cập nhật thông tin thành công".showToast(
                                requireContext()
                            )
                            dismiss()
                        }
                    }
                }
            }
        }
    }
}