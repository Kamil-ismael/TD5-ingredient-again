package com.hei.td5ingredientagain.repository;

import com.hei.td5ingredientagain.dto.StockMovementCreation;
import com.hei.td5ingredientagain.entity.MovementTypeEnum;
import com.hei.td5ingredientagain.entity.StockMovement;
import com.hei.td5ingredientagain.entity.StockValue;
import com.hei.td5ingredientagain.entity.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StockMovementRepository extends BaseRepository {

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
                    stockMovements.add(mapResultSetToStockMovement(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stockMovements;
    }

    public List<StockMovement> findByIngredientIdAndDateRange(Integer ingredientId, Instant from, Instant to) {
        List<StockMovement> stockMovements = new ArrayList<>();
        String sql = """
            SELECT id, quantity, unit, type, creation_datetime
            FROM stock_movement
            WHERE id_ingredient = ? AND creation_datetime >= ? AND creation_datetime <= ?;
        """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ps.setTimestamp(2, Timestamp.from(from));
            ps.setTimestamp(3, Timestamp.from(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stockMovements.add(mapResultSetToStockMovement(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stockMovements;
    }

    public List<StockMovement> saveAll(Integer ingredientId, List<StockMovementCreation> movementsToCreate) {
        List<StockMovement> savedMovements = new ArrayList<>();
        String sql = """
            INSERT INTO stock_movement(id, id_ingredient, quantity, type, unit, creation_datetime)
            VALUES (?, ?, ?, ?::movement_type, ?::unit, ?)
            RETURNING id, creation_datetime, quantity, unit, type;
        """;
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (StockMovementCreation creation : movementsToCreate) {
                    ps.setInt(1, getNextSerialValue(conn, "stock_movement", "id"));
                    ps.setInt(2, ingredientId);
                    ps.setDouble(3, creation.getQuantity());
                    ps.setString(4, creation.getType().name());
                    ps.setString(5, creation.getUnit().name());
                    ps.setTimestamp(6, Timestamp.from(Instant.now()));

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            savedMovements.add(mapResultSetToStockMovement(rs));
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return savedMovements;
    }

    private StockMovement mapResultSetToStockMovement(ResultSet rs) throws SQLException {
        StockValue stockValue = new StockValue(
                rs.getDouble("quantity"),
                Unit.valueOf(rs.getString("unit"))
        );
        return new StockMovement(
                rs.getInt("id"),
                MovementTypeEnum.valueOf(rs.getString("type")),
                rs.getTimestamp("creation_datetime").toInstant(),
                stockValue
        );
    }
}


