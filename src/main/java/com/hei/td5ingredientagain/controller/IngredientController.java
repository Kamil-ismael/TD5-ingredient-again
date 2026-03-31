package com.hei.td5ingredientagain.controller;

import com.hei.td5ingredientagain.dto.StockInfo;
import com.hei.td5ingredientagain.dto.StockMovementCreation;
import com.hei.td5ingredientagain.entity.Ingredient;
import com.hei.td5ingredientagain.entity.StockMovement;
import com.hei.td5ingredientagain.entity.Unit;
import com.hei.td5ingredientagain.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    public Ingredient getIngredientById(@PathVariable Integer id) {
        return ingredientService.getIngredientById(id);
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getIngredientStock(
            @PathVariable Integer id,
            @RequestParam(name = "at") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<Instant> at,
            @RequestParam(name = "unit") Optional<Unit> unit) {

        if (at.isEmpty() || unit.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Either mandatory query parameter `at` or `unit` is not provided.");
        }

        StockInfo stockInfo = ingredientService.getCurrentStock(id, at.get(), unit.get());
        return ResponseEntity.ok(stockInfo);
    }

    @GetMapping("/{id}/stockMovements")
    public List<StockMovement> getStockMovements(
            @PathVariable Integer id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return ingredientService.getStockMovementsForIngredient(id, from, to);
    }

    @PostMapping("/{id}/stockMovements")
    public ResponseEntity<List<StockMovement>> createStockMovements(
            @PathVariable Integer id,
            @RequestBody List<StockMovementCreation> movements) {
        List<StockMovement> createdMovements = ingredientService.addStockMovementsToIngredient(id, movements);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovements);
    }
}

