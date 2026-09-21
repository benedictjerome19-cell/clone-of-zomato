package com.yourname.yournameeats.controller;

import java.io.IOException;
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
import com.yourname.yournameeats.model.Restaurant;
import com.yourname.yournameeats.util.JsonUtil;

@WebServlet("/api/v1/owner/orders/status")
public class OrderStatusServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final RestaurantDAO restaurantDAO = new RestaurantDAOImpl();

    private void writeJson(HttpServletResponse resp, int status, boolean success, String errCode, String errMsg) throws IOException {
        JsonObject envelope = new JsonObject();
        envelope.addProperty("success", success);
        envelope.add("data", null);
        if (!success) {
            JsonObject error = new JsonObject();
            error.addProperty("code", errCode);
            error.addProperty("message", errMsg);
            envelope.add("error", error);
        } else {
            envelope.add("error", null);
        }
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.getWriter().write(JsonUtil.GSON.toJson(envelope));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || !"RESTAURANT_OWNER".equals(session.getAttribute("userRole"))) {
            writeJson(resp, HttpServletResponse.SC_FORBIDDEN, false, "FORBIDDEN", "Restaurant owner access only");
            return;
        }

        int ownerId = (int) session.getAttribute("userId");
        Optional<Restaurant> restaurant = restaurantDAO.findByOwnerId(ownerId);
        if (restaurant.isEmpty()) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, false, "NO_RESTAURANT", "No restaurant found for this owner");
            return;
        }

        String orderIdParam = req.getParameter("orderId");
        if (orderIdParam == null) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, false, "VALIDATION_ERROR", "orderId is required");
            return;
        }

        boolean advanced = orderDAO.advanceStatus(Integer.parseInt(orderIdParam), restaurant.get().getId());
        if (!advanced) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, false, "CANNOT_ADVANCE",
                "Order not found for your restaurant, or is already DELIVERED");
            return;
        }

        writeJson(resp, HttpServletResponse.SC_OK, true, null, null);
    }
}