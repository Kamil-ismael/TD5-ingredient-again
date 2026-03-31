package com.hei.td5ingredientagain.repository;

import com.hei.td5ingredientagain.entity.*;
import com.hei.td5ingredientagain.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DishRepository extends BaseRepository {

    private final DataSource dataSource;

    public List<Dish> findAll() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT id, name, selling_price, dish_type FROM dish";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int dishId = rs.getInt("id");
                dishes.add(new Dish(
                        dishId,
                        rs.getString("name"),
                        rs.getDouble("selling_price"),
                        DishTypeEnum.valueOf(rs.getString("dish_type")),
                        findIngredientsByDishId(dishId, conn)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dishes;
    }

    public Dish findById(Integer id) {
        String sql = "SELECT id, name, selling_price, dish_type FROM dish WHERE id = ?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Dish(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("selling_price"),
                        DishTypeEnum.valueOf(rs.getString("dish_type")),
                        findIngredientsByDishId(id, connection)
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new NotFoundException("Dish.id=" + id + " is not found");
    }

    public List<DishIngredient> findIngredientsByDishId(Integer dishId, Connection connection) throws SQLException {
        List<DishIngredient> dishIngredients = new ArrayList<>();
        String sql = """
            SELECT i.id, i.name, i.price, i.category, di.required_quantity, di.unit
            FROM ingredient i
            JOIN dish_ingredient di ON di.id_ingredient = i.id
            WHERE di.id_dish = ?;
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient(
                            rs.getInt("id"),
                            rs.getString("name"),
                            CategoryEnum.valueOf(rs.getString("category")),
                            rs.getDouble("price"),
                            null
                    );
                    DishIngredient dishIngredient = new DishIngredient(
                            ingredient,
                            null,
                            rs.getObject("required_quantity") == null ? null : rs.getDouble("required_quantity"),
                            Unit.valueOf(rs.getString("unit"))
                    );
                    dishIngredients.add(dishIngredient);
                }
            }
        }
        return dishIngredients;
    }

    public void updateDishIngredients(Integer dishId, List<DishIngredient> newIngredients) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                detachAllIngredients(conn, dishId);
                attachIngredients(conn, dishId, newIngredients);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void detachAllIngredients(Connection conn, Integer dishId) throws SQLException {
        String sql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void attachIngredients(Connection conn, Integer dishId, List<DishIngredient> ingredients) throws SQLException {
        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }
        String sql = """
            INSERT INTO dish_ingredient (id, id_ingredient, id_dish, required_quantity, unit)
            VALUES (?, ?, ?, ?, ?::unit)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient dishIngredient : ingredients) {
                ps.setInt(1, getNextSerialValue(conn, "dish_ingredient", "id"));
                ps.setInt(2, dishIngredient.getIngredient().getId());
                ps.setInt(3, dishId);
                ps.setDouble(4, dishIngredient.getQuantity());
                ps.setString(5, dishIngredient.getUnit().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

}

