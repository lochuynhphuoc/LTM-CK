package com.ltm.controller;

import com.ltm.dao.UserDAO;
import com.ltm.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password =  request.getParameter("password");
        String fullName = request.getParameter("fullName");

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);

        if (userDAO.register(user)) {
            response.sendRedirect("login.jsp?success=true");
        } else {
            request.setAttribute("error", "Registration failed. Username might be taken.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}
