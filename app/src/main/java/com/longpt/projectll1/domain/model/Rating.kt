package com.longpt.projectll1.domain.model

import com.google.firebase.Timestamp

class Rating(
    val userId: String = "",
    val userName: String = "",
    val avatarUrl: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val updatedAt: Timestamp = Timestamp.now()
)