package com.hei.td5ingredientagain.repository;

import com.hei.td5ingredientagain.entity.*;
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
public class StockMovementRepository {

    private final DataSource dataSource;

    public List<StockMovement> findByIngredientId(Integer ingredientId) {
        List<StockMovement> stockMovements = new ArrayList<>();
        String sql = """
            SELECT id, quantity, unit, type, creation_datetime
            FROM stock_movement
            WHERE id_ingredient = ?;
        """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockValue stockValue = new StockValue(
                            rs.getDouble("quantity"),
                            Unit.valueOf(rs.getString("unit"))
                    );
                    StockMovement stockMovement = new StockMovement(
                            rs.getInt("id"),
                            MovementTypeEnum.valueOf(rs.getString("type")),
                            rs.getTimestamp("creation_datetime").toInstant(),
                            stockValue
                    );
                    stockMovements.add(stockMovement);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stockMovements;
    }
}

