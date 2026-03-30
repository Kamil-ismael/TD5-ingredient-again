package com.hei.td5ingredientagain.service;

import com.hei.td5ingredientagain.entity.Ingredient;
import com.hei.td5ingredientagain.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository IngredientrRepository;
    public IngredientService(IngredientRepository IngredientrRepository) {
        this.IngredientrRepository = IngredientrRepository;
    }
    public List<Ingredient> getAllIngredients() {
        return IngredientrRepository.findAll();
    }
}
