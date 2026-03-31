package com.hei.td5ingredientagain.service;

import com.hei.td5ingredientagain.dto.DishIngredientUpdate;
import com.hei.td5ingredientagain.entity.Dish;
import com.hei.td5ingredientagain.entity.DishIngredient;
import com.hei.td5ingredientagain.entity.Ingredient;
import com.hei.td5ingredientagain.repository.DishRepository;
import com.hei.td5ingredientagain.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final IngredientRepository ingredientRepository;

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public void updateDishIngredients(Integer dishId, List<DishIngredientUpdate> updates) {

        Dish dish = dishRepository.findById(dishId);

        List<DishIngredient> newIngredients = updates.stream()
                .map(update -> {
                    try {
                        Ingredient ingredient = ingredientRepository.findById(update.getIngredientId());
                        return new DishIngredient(ingredient, dish, update.getQuantity(), update.getUnit());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        dishRepository.updateDishIngredients(dishId, newIngredients);
    }
}

