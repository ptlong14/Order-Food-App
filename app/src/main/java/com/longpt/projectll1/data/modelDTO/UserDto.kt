package com.longpt.projectll1.data.modelDTO

import com.google.firebase.Timestamp

data class UserDto(
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val createdAt: Timestamp = Timestamp.now(),
) {
}