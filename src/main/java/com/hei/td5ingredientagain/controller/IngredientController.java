package com.hei.td5ingredientagain.controller;

import com.hei.td5ingredientagain.dto.StockInfo;
import com.hei.td5ingredientagain.entity.Ingredient;
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
}

