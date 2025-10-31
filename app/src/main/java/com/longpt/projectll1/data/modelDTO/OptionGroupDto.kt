package com.longpt.projectll1.data.modelDTO

data class OptionGroupDto(
    val groupName: String = "",
    val maxChoose: Int = 1,
    val require: Boolean = false,
    val optionItem: List<OptionItemDto> = emptyList()
)