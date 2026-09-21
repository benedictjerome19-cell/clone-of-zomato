package com.yourname.yournameeats.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.yourname.yournameeats.dao.OrderDAO;
import com.yourname.yournameeats.dao.OrderDAOImpl;
import com.yourname.yournameeats.dao.RestaurantDAO;
import com.yourname.yournameeats.dao.RestaurantDAOImpl;
import com.yourname.yournameeats.model.OrderSummary;
import com.yourname.yournameeats.model.Restaurant;
import com.yourname.yournameeats.util.JsonUtil;

@WebServlet("/api/v1/orders/history")
public class OrderHistoryServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final RestaurantDAO restaurantDAO = new RestaurantDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"success\":false,\"data\":null,\"error\":{\"code\":\"UNAUTHENTICATED\",\"message\":\"Login required\"}}");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String role = (String) session.getAttribute("userRole");

        List<OrderSummary> orders = new ArrayList<>();

        if ("RESTAURANT_OWNER".equals(role)) {
            Optional<Restaurant> restaurant = restaurantDAO.findByOwnerId(userId);
            if (restaurant.isPresent()) {
                orders = orderDAO.findByRestaurantId(restaurant.get().getId());
            }
        } else {
            orders = orderDAO.findByBuyerId(userId);
        }

        JsonObject envelope = new JsonObject();
        envelope.addProperty("success", true);
        envelope.add("data", JsonUtil.GSON.toJsonTree(orders));
        envelope.add("error", null);
        resp.getWriter().write(JsonUtil.GSON.toJson(envelope));
    }
}