package com.hostel.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hostel.util.DatabaseConnection;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String adminId = request.getParameter("adminId");
        String password = request.getParameter("password");

        String sql =
                "SELECT name FROM admins " +
                "WHERE admin_id = ? AND password = ?";

        try {

            Connection con =
                    DatabaseConnection.getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, adminId);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String name = rs.getString("name");

                HttpSession session =
                        request.getSession();

                session.setAttribute("adminId", adminId);
                session.setAttribute("adminName", name);

                response.sendRedirect(
                        "AdminDashboardServlet"
                );

            } else {

                response.setContentType("text/html");

                response.getWriter().println(
                        "<h2>Admin Login Failed</h2>"
                );

                response.getWriter().println(
                        "<p>Invalid Admin ID or Password.</p>"
                );

                response.getWriter().println(
                        "<a href='admin-login.html'>" +
                        "Try Again</a>"
                );
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html");

            response.getWriter().println(
                    "<h2>Admin Login Error</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}