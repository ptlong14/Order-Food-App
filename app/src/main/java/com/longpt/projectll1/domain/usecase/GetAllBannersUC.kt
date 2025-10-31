package com.longpt.projectll1.domain.usecase

import com.longpt.projectll1.domain.repository.BannerRepository

class GetAllBannersUC(val bannerRepository: BannerRepository) {
    operator fun invoke() = bannerRepository.getBannerList()
}