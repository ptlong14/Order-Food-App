package com.longpt.projectll1.presentation.modelUI

enum class SectionType {
    BEST_SELLER,
    NEW,
    TRENDING,
    TOP_RATED
}

val SectionType.description: String
    get() = when (this) {
        SectionType.BEST_SELLER -> "Món này hot cực, ai cũng săn lùng!"
        SectionType.TOP_RATED -> "Ngon hết nấc, cả xóm khen ngon!"
        SectionType.TRENDING -> "Trend ẩm thực mới, ăn thử liền nha!"
        SectionType.NEW -> "Món mới siêu chất, thử là mê ngay!"
    }
val SectionType.title: String
    get() = when (this) {
        SectionType.BEST_SELLER -> "Sản phẩm bán chạy"
        SectionType.TOP_RATED -> "Sản phẩm tốt nhất"
        SectionType.TRENDING -> "Trend ẩm thực mới"
        SectionType.NEW -> "Sản phẩm mới"
    }