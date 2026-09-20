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

import com.hostel.util.DatabaseConnection;

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("studentId");
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // Check whether passwords match
        if (!newPassword.equals(confirmPassword)) {

            out.println("<html><body>");

            out.println("<h2>Passwords Do Not Match</h2>");

            out.println("<p>Please enter the same password in both fields.</p>");

            out.println("<a href='forgot-password.html'>Go Back</a>");

            out.println("</body></html>");

            return;
        }

        try {

            Connection con = DatabaseConnection.getConnection();

            if (con == null) {

                out.println("<h2>Database Connection Failed</h2>");

                return;
            }

            // Check Student ID and Email
            String checkSql =
                    "SELECT student_id FROM students "
                  + "WHERE student_id = ? AND email = ?";

            PreparedStatement checkPs =
                    con.prepareStatement(checkSql);

            checkPs.setString(1, studentId);
            checkPs.setString(2, email);

            ResultSet rs = checkPs.executeQuery();

            if (!rs.next()) {

                out.println("<html><body>");

                out.println("<h2>Details Not Found</h2>");

                out.println(
                    "<p>Student ID or registered email is incorrect.</p>"
                );

                out.println(
                    "<a href='forgot-password.html'>Try Again</a>"
                );

                out.println("</body></html>");

                rs.close();
                checkPs.close();
                con.close();

                return;
            }

            rs.close();
            checkPs.close();

            // Update password
            String updateSql =
                    "UPDATE students SET password = ? "
                  + "WHERE student_id = ? AND email = ?";

            PreparedStatement updatePs =
                    con.prepareStatement(updateSql);

            updatePs.setString(1, newPassword);
            updatePs.setString(2, studentId);
            updatePs.setString(3, email);

            int result = updatePs.executeUpdate();

            updatePs.close();
            con.close();

            if (result > 0) {

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Password Reset | HostelHub</title>");
                out.println("</head>");

                out.println("<body>");

                out.println("<h2>Password Reset Successfully!</h2>");

                out.println(
                    "<p>Your password has been changed successfully.</p>"
                );

                out.println(
                    "<a href='login.html'>Go to Login</a>"
                );

                out.println("</body>");
                out.println("</html>");

            } else {

                out.println("<h2>Password Reset Failed</h2>");

                out.println(
                    "<a href='forgot-password.html'>Try Again</a>"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            out.println("<html>");
            out.println("<body>");

            out.println("<h2>Password Reset Error</h2>");

            out.println("<p>" + e.getMessage() + "</p>");

            out.println(
                "<a href='forgot-password.html'>Go Back</a>"
            );

            out.println("</body>");
            out.println("</html>");
        }
    }
}