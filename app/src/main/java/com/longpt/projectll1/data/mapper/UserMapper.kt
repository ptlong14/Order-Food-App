package com.longpt.projectll1.data.mapper

import com.longpt.projectll1.data.modelDTO.UserDto
import com.longpt.projectll1.domain.model.User

object UserMapper {
    fun fromDtoToDomain(userDto: UserDto): User {
        return User(
            avatarUrl = userDto.avatarUrl,
            email = userDto.email,
            name = userDto.name,
            bio = userDto.bio,
            createdAt = userDto.createdAt
        )
    }
    fun fromDomainToDto(user: User): UserDto {
        return UserDto(
            avatarUrl = user.avatarUrl,
            email = user.email,
            name = user.name,
            bio = user.bio,
            createdAt = user.createdAt
        )
    }
}

