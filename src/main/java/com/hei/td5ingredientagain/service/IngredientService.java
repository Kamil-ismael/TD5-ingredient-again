package com.hei.td5ingredientagain.service;

import com.hei.td5ingredientagain.entity.*;
import com.hei.td5ingredientagain.repository.IngredientRepository;
import com.hei.td5ingredientagain.repository.StockMovementRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientrRepository;
    private final StockMovementRepository stockMovementRepository;


    public List<Ingredient> getAllIngredients() {
        return ingredientrRepository.findAll();

    }

    public Ingredient getIngredientById(Integer id) {
        return ingredientrRepository.findById(id);
    }

    public StockValue getStock(Integer id, Instant at, Unit unit) {

        Ingredient ingredient = ingredientRepository.findById(id);

        List<StockMovement> movements =
                stockMovementRepository.findByIngredientId(id);

        double total = movements.stream()
                .filter(m -> m.getCreationDatetime().isBefore(at))
                .mapToDouble(m ->
                        m.getType() == MovementTypeEnum.IN
                                ? m.getValue().getQuantity()
                                : -m.getValue().getQuantity()
                )
                .sum();

        StockValue result = new StockValue();
        result.setQuantity(total);
        result.setUnit(unit);

        return result;
    }

}
