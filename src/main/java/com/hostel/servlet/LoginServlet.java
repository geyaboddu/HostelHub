package com.hostel.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("studentId");
        String password = request.getParameter("password");

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String sql = "SELECT * FROM students "
                   + "WHERE student_id = ? AND password = ?";

        try {

            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, studentId);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String name = rs.getString("name");
                String email = rs.getString("email");

                // Create student session
                HttpSession session = request.getSession();

                session.setAttribute("studentId", studentId);
                session.setAttribute("studentName", name);
                session.setAttribute("studentEmail", email);

                // Open dynamic dashboard
                response.sendRedirect("StudentDashboardServlet");

            } else {

                out.println("<html>");
                out.println("<body>");

                out.println("<h2>Login Failed</h2>");
                out.println("<p>Invalid Student ID or Password.</p>");

                out.println("<a href='login.html'>");
                out.println("Try Again");
                out.println("</a>");

                out.println("</body>");
                out.println("</html>");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<html>");
            out.println("<body>");

            out.println("<h2>Login Error</h2>");
            out.println("<p>" + e.getMessage() + "</p>");

            out.println("<a href='login.html'>");
            out.println("Go Back");
            out.println("</a>");

            out.println("</body>");
            out.println("</html>");
        }
    }
}