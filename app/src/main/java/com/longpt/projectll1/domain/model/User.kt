package com.longpt.projectll1.domain.model

import com.google.firebase.Timestamp

data class User(
    val avatarUrl: String = "",
    val name: String = "",
    val bio: String = "",
    val email: String = "",
    val createdAt: Timestamp = Timestamp.now(),
)