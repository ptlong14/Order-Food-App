package com.longpt.projectll1.data.mapper

import com.longpt.projectll1.data.modelDTO.FoodDto
import com.longpt.projectll1.data.modelDTO.OptionGroupDto
import com.longpt.projectll1.data.modelDTO.OptionItemDto
import com.longpt.projectll1.domain.model.Food
import com.longpt.projectll1.domain.model.OptionGroup
import com.longpt.projectll1.domain.model.OptionItem

object FoodMapper {

    fun fromDtoToDomain(dto: FoodDto): Food{
        val optionGroup = dto.optionGroup.map {g->
            OptionGroup(
                groupName = g.groupName,
                maxChoose = g.maxChoose,
                require = g.require,
                optionItem = g.optionItem.map {o->
                    OptionItem(
                        optionName = o.optionName,
                        extraCost = o.extraCost,
                        isChecked = o.isChecked,
                        isEnabled = o.isEnabled
                    )
                }
            )
        }
        return Food(
            id= dto.id,
            name = dto.name,
            price = dto.price,
            rating = dto.rating,
            imgUrl = dto.imgUrl ?: "",
            category = dto.category,
            description = dto.description,
            optionGroup = optionGroup,
            sold = dto.sold,
            createdAt = dto.createdAt
        )
    }
    fun fromDomainToDto(domain: Food): FoodDto{
        val optionGroup = domain.optionGroup.map {g->
            OptionGroupDto(
                groupName = g.groupName,
                maxChoose = g.maxChoose,
                require = g.require,
                optionItem = g.optionItem.map {o->
                    OptionItemDto(
                        optionName = o.optionName,
                        extraCost = o.extraCost,
                        isChecked = o.isChecked,
                        isEnabled = o.isEnabled
                    )
                }
            )
        }
        return FoodDto(
            id= domain.id,
            name = domain.name,
            price = domain.price,
            rating = domain.rating,
            imgUrl = domain.imgUrl,
            category = domain.category,
            description = domain.description,
            optionGroup = optionGroup,
            sold = domain.sold,
            createdAt = domain.createdAt
        )
    }
}