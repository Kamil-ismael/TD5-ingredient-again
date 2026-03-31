package com.hei.td5ingredientagain.service;

import com.hei.td5ingredientagain.dto.StockInfo;
import com.hei.td5ingredientagain.entity.Ingredient;
import com.hei.td5ingredientagain.entity.MovementTypeEnum;
import com.hei.td5ingredientagain.entity.Unit;
import com.hei.td5ingredientagain.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Integer id) {
        return ingredientRepository.findById(id);
    }

    public StockInfo getCurrentStock(Integer ingredientId, Instant at, Unit unit) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId);

        double totalQuantity = ingredient.getStockMovementList().stream()
                .filter(movement -> movement.getValue().getUnit() == unit)
                .filter(movement -> !movement.getCreationDatetime().isAfter(at))
                .mapToDouble(movement -> {
                    double quantity = movement.getValue().getQuantity();
                    return movement.getType() == MovementTypeEnum.IN ? quantity : -quantity;
                })
                .sum();

        return new StockInfo(unit, totalQuantity);
    }
}

