package com.hei.td5ingredientagain.repository;


import com.hei.td5ingredientagain.entity.CategoryEnum;
import com.hei.td5ingredientagain.entity.Ingredient;
import com.hei.td5ingredientagain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IngredientRepository {

    private final DataSource dataSource;
    private final StockMovementRepository stockMovementRepository;

    public List<Ingredient> findAll() {
        List<Ingredient> list = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement ps = conn.prepareStatement("""
                select id, name, price, category from ingredient
            """);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getDouble("price"),
                        new ArrayList<>()
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
    public Ingredient findById(Integer id) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ingredient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            CategoryEnum.valueOf(rs.getString("category")),
                            rs.getDouble("price"),
                            stockMovementRepository.findByIngredientId(id)
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new NotFoundException("Ingredient.id=" + id + " is not found");
    }

}