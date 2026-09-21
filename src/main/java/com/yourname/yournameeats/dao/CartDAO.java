package com.yourname.yournameeats.dao;

import java.util.List;
import java.util.Map;

import com.yourname.yournameeats.model.CartItem;

public interface CartDAO {
    void addOrUpdate(int userId, int menuItemId, int quantity);
    void remove(int userId, int menuItemId);
    List<CartItem> findByUser(int userId);
    List<Map<String, Object>> findDetailedByUser(int userId);
    void clearCart(int userId);
}