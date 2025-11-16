package com.longpt.projectll1.data.mapper

import com.longpt.projectll1.data.modelDTO.RatingDto
import com.longpt.projectll1.domain.model.Rating

object RatingMapper {
    fun fromDtoToDomain(ratingDto: RatingDto): Rating {
        return Rating(
            userId = ratingDto.userId,
            userName = ratingDto.userName,
            avatarUrl = ratingDto.avatarUrl,
            rating = ratingDto.rating,
            comment = ratingDto.comment,
            updatedAt = ratingDto.updatedAt
        )
    }

    fun fromDomainToDto(rating: Rating): RatingDto {
        return RatingDto(
            userId = rating.userId,
            userName = rating.userName,
            avatarUrl = rating.avatarUrl,
            rating = rating.rating,
            comment = rating.comment,
            updatedAt = rating.updatedAt
        )
    }
}