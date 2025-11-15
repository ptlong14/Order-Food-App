package com.longpt.projectll1.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.longpt.projectll1.R
import com.longpt.projectll1.core.TaskResult
import com.longpt.projectll1.data.remote.CloudinaryService
import com.longpt.projectll1.data.remote.FirestoreDataSource
import com.longpt.projectll1.data.repositoryImpl.UserRepositoryImpl
import com.longpt.projectll1.databinding.ActivityUserInformationBinding
import com.longpt.projectll1.domain.usecase.GetUserInfoUC
import com.longpt.projectll1.domain.usecase.UpdateAvatarUser
import com.longpt.projectll1.domain.usecase.UpdateUserInforUC
import com.longpt.projectll1.presentation.factory.UserViewModelFactory
import com.longpt.projectll1.presentation.viewModel.UserViewModel
import com.longpt.projectll1.utils.showToast
import kotlinx.coroutines.launch

class UserInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserInformationBinding
    private lateinit var userViewModel: UserViewModel
    private val currentUser get() = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser!!.uid

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val selectedImgUri = result.data!!.data!!
                userViewModel.observerUpdateAvatar(userId, selectedImgUri)
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Init ViewModel
        val repoUser = UserRepositoryImpl(FirestoreDataSource(), CloudinaryService())
        val userFactory = UserViewModelFactory(
            GetUserInfoUC(repoUser), UpdateUserInforUC(repoUser), UpdateAvatarUser(repoUser)
        )
        userViewModel = ViewModelProvider(this, userFactory)[UserViewModel::class.java]

        lifecycleScope.launch {
            userViewModel.userInfor.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {}
                    is TaskResult.Error -> {
                        res.exception.message?.showToast(this@UserInformationActivity)
                    }

                    is TaskResult.Success -> {
                        val user = res.data
                        binding.edtFullName.text = "Họ và tên: ${user.name}"
                        binding.edtBio.text = "Bio: ${user.bio.ifEmpty { "Chưa có mô tả" }}"
                        binding.edtEmail.text = "Email: ${user.email}"
                        binding.edtCreatedAt.text = "Ngày tạo: ${user.createdAt.toDate()}"

                        Glide.with(binding.imgAvatar)
                            .load(user.avatarUrl)
                            .placeholder(R.drawable.im_loading)
                            .error(R.drawable.im_avatar_err)
                            .into(binding.imgAvatar)
                    }
                }
            }
        }

        lifecycleScope.launch {
            userViewModel.updateAvatarState.collect { res ->
                when (res) {
                    is TaskResult.Loading -> {
                        binding.containerUserInfo.visibility= View.INVISIBLE
                        binding.progressBar.visibility= View.VISIBLE
                    }
                    is TaskResult.Error -> {
                        binding.containerUserInfo.visibility= View.VISIBLE
                        binding.progressBar.visibility= View.INVISIBLE
                        res.exception.message?.showToast(this@UserInformationActivity)
                    }

                    is TaskResult.Success -> {
                        binding.containerUserInfo.visibility= View.VISIBLE
                        binding.progressBar.visibility= View.INVISIBLE
                        "Cập nhật ảnh đại diện thành công".showToast(this@UserInformationActivity)
                    }

                    null -> {}
                }
            }
        }

        binding.imgAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        binding.edtFullName.setOnClickListener {
            val field = "name"
            val oldValue = binding.edtFullName.text.toString().substringAfter(": ").trim()
            showBTS(field, oldValue)
        }

        binding.edtBio.setOnClickListener {
            val field = "bio"
            val oldValue = binding.edtBio.text.toString().substringAfter(": ").trim()
            showBTS(field, oldValue)
        }

        binding.iBtnBack.setOnClickListener {
            finish()
        }

        // Start observing user info
        userViewModel.observerUserInfos(userId)
    }

    private fun showBTS(field: String, oldValue: String) {
        val fragment = BottomSheetUpdateUserInfor.newInstance(field, oldValue)
        fragment.show(supportFragmentManager, "BottomSheetUpdateUserInfor")
    }
}
