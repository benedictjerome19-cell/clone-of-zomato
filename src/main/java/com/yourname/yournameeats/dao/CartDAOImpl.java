package com.yourname.yournameeats.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yourname.yournameeats.listener.AppContextListener;
import com.yourname.yournameeats.model.CartItem;

public class CartDAOImpl implements CartDAO {

    @Override
    public void addOrUpdate(int userId, int menuItemId, int quantity) {
        String checkSql = "SELECT id FROM cart_items WHERE user_id = ? AND menu_item_id = ?";
        String updateSql = "UPDATE cart_items SET quantity = ? WHERE id = ?";
        String insertSql = "INSERT INTO cart_items (user_id, menu_item_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = AppContextListener.getDataSource().getConnection()) {

            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setInt(1, userId);
                check.setInt(2, menuItemId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        int cartItemId = rs.getInt("id");
                        try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                            update.setInt(1, quantity);
                            update.setInt(2, cartItemId);
                            update.executeUpdate();
                        }
                        return;
                    }
                }
            }

            try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                insert.setInt(1, userId);
                insert.setInt(2, menuItemId);
                insert.setInt(3, quantity);
                insert.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add/update cart item", e);
        }
    }

    @Override
    public void remove(int userId, int menuItemId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ? AND menu_item_id = ?";
        try (Connection conn = AppContextListener.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, menuItemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove cart item", e);
        }
    }

    @Override
    public List<CartItem> findByUser(int userId) {
        String sql = "SELECT * FROM cart_items WHERE user_id = ?";
        List<CartItem> results = new ArrayList<>();
        try (Connection conn = AppContextListener.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setMenuItemId(rs.getInt("menu_item_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    results.add(item);
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch cart", e);
        }
    }

    /**
     * Joins cart_items with menu_items so the frontend gets name/price
     * directly, without a second round-trip per item.
     */
    @Override
    public List<Map<String, Object>> findDetailedByUser(int userId) {
        String sql = "SELECT ci.id AS cart_item_id, ci.menu_item_id, ci.quantity, " +
                     "mi.name, mi.price " +
                     "FROM cart_items ci " +
                     "JOIN menu_items mi ON ci.menu_item_id = mi.id " +
                     "WHERE ci.user_id = ?";
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = AppContextListener.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("cartItemId", rs.getInt("cart_item_id"));
                    row.put("menuItemId", rs.getInt("menu_item_id"));
                    row.put("name", rs.getString("name"));
                    row.put("price", rs.getBigDecimal("price"));
                    row.put("quantity", rs.getInt("quantity"));
                    results.add(row);
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch detailed cart", e);
        }
    }

    @Override
    public void clearCart(int userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = AppContextListener.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear cart", e);
        }
    }
}
