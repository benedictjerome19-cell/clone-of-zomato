package com.yourname.yournameeats.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import com.yourname.yournameeats.dao.CartDAO;
import com.yourname.yournameeats.dao.CartDAOImpl;
import com.yourname.yournameeats.util.JsonUtil;

@WebServlet("/api/v1/cart")
public class CartServlet extends HttpServlet {

    private final CartDAO cartDAO = new CartDAOImpl();

    private int getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (int) session.getAttribute("userId");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        List<Map<String, Object>> items = cartDAO.findDetailedByUser(getUserId(req));

        // Wrap in {items: [...]} — this is the exact shape home.jsp's
        // loadCart() checks for (result.data.items).
        JsonObject data = new JsonObject();
        data.add("items", JsonUtil.GSON.toJsonTree(items));

        JsonObject envelope = new JsonObject();
        envelope.addProperty("success", true);
        envelope.add("data", data);
        envelope.add("error", null);
        resp.getWriter().write(JsonUtil.GSON.toJson(envelope));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        int menuItemId = Integer.parseInt(req.getParameter("menuItemId"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        cartDAO.addOrUpdate(getUserId(req), menuItemId, quantity);

        JsonObject envelope = new JsonObject();
        envelope.addProperty("success", true);
        envelope.add("data", null);
        envelope.add("error", null);
        resp.getWriter().write(JsonUtil.GSON.toJson(envelope));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        int menuItemId = Integer.parseInt(req.getParameter("menuItemId"));

        cartDAO.remove(getUserId(req), menuItemId);

        JsonObject envelope = new JsonObject();
        envelope.addProperty("success", true);
        envelope.add("data", null);
        envelope.add("error", null);
        resp.getWriter().write(JsonUtil.GSON.toJson(envelope));
    }
}