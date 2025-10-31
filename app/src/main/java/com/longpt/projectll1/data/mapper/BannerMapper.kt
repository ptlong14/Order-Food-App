package com.longpt.projectll1.data.mapper

import com.longpt.projectll1.data.modelDTO.BannerDto
import com.longpt.projectll1.domain.model.Banner

object BannerMapper {
    fun fromDtoToDomain(dto: BannerDto): Banner {
        return Banner(
            imgBanner = dto.imgBanner
        )
    }
    fun fromDomainToDto(domain: Banner): BannerDto {
        return BannerDto(
            imgBanner = domain.imgBanner
        )
    }
}
