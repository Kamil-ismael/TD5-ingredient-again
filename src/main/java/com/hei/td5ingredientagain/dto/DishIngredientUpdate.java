package com.hei.td5ingredientagain.dto;

import com.hei.td5ingredientagain.entity.Unit;
import lombok.Data;

@Data
public class DishIngredientUpdate {
    private Integer ingredientId;
    private Double quantity;
    private Unit unit;
}

