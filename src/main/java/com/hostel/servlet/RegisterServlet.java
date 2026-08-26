package com.hostel.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hostel.util.DatabaseConnection;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String studentId = request.getParameter("studentId");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String course = request.getParameter("course");
        String year = request.getParameter("year");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        if (!password.equals(confirmPassword)) {

            out.println("<h2>Registration Failed</h2>");
            out.println("<p>Passwords do not match.</p>");
            out.println("<a href='register.html'>Go Back</a>");

            return;
        }

        String name = firstName + " " + lastName;

        String sql = "INSERT INTO students "
                + "(student_id, name, email, phone, branch, year, password) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {

            Connection con = DatabaseConnection.getConnection();

            if (con == null) {
                out.println("<h2>Database Connection Failed</h2>");
                out.println("<p>Could not connect to PostgreSQL.</p>");
                return;
            }

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, studentId);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, course);
            ps.setInt(6, Integer.parseInt(year));
            ps.setString(7, password);

            int result = ps.executeUpdate();

            if (result > 0) {

                out.println("<h1>Registration Successful!</h1>");
                out.println("<p>Welcome, " + name + "!</p>");
                out.println("<p>Student ID: " + studentId + "</p>");
                out.println("<a href='login.html'>Go to Login</a>");

            }

            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<h2>Registration Failed</h2>");
            out.println("<p>Error: " + e.getMessage() + "</p>");
            out.println("<a href='register.html'>Go Back</a>");
        }
    }
}