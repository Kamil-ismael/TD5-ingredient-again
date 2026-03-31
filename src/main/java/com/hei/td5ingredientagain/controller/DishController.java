package com.hei.td5ingredientagain.controller;

import com.hei.td5ingredientagain.dto.DishIngredientUpdate;
import com.hei.td5ingredientagain.entity.Dish;
import com.hei.td5ingredientagain.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    public List<Dish> getAllDishes() {
        return dishService.getAllDishes();
    }

    @PutMapping("/{id}/ingredients")
    public ResponseEntity<Void> updateDishIngredients(
            @PathVariable Integer id,
            @RequestBody(required = false) List<DishIngredientUpdate> ingredients) {

        if (ingredients == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        dishService.updateDishIngredients(id, ingredients);
        return ResponseEntity.ok().build();
    }
}
