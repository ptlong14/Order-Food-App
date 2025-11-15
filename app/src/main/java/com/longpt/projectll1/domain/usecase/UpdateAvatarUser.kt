package com.longpt.projectll1.domain.usecase

import android.net.Uri
import com.longpt.projectll1.domain.repository.UserRepository

class UpdateAvatarUser(val userRepository: UserRepository) {
    operator fun invoke(userId: String, uri: Uri) = userRepository.updateAvatar(userId, uri)
}