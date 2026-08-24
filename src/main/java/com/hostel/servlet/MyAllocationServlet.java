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

import com.hostel.util.DatabaseConnection;

@WebServlet("/MyAllocationServlet")
public class MyAllocationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String studentId = request.getParameter("studentId");

        String sql =
                "SELECT a.allocation_id, a.student_id, "
              + "a.room_id, a.status, "
              + "r.room_number, r.block_name, "
              + "r.room_type, r.capacity, r.occupied "
              + "FROM allocations a "
              + "JOIN rooms r ON a.room_id = r.room_id "
              + "WHERE a.student_id = ? "
              + "AND a.status = 'Approved'";

        try {

            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, studentId);

            ResultSet rs = ps.executeQuery();

            response.getWriter().println(
                    "<!DOCTYPE html>"
            );

            response.getWriter().println(
                    "<html><head><title>My Allocation</title>"
            );

            response.getWriter().println(
                    "<style>" +
                    "body{font-family:Arial;background:#f4f8fc;" +
                    "padding:40px;}" +

                    ".container{max-width:600px;margin:auto;" +
                    "background:white;padding:30px;" +
                    "border-radius:12px;" +
                    "box-shadow:0 5px 20px rgba(0,0,0,0.1);}" +

                    "h1{text-align:center;color:#1e3a5f;}" +

                    ".box{margin-top:25px;}" +

                    ".row{padding:12px;" +
                    "border-bottom:1px solid #ddd;}" +

                    ".label{font-weight:bold;color:#555;}" +

                    ".approved{color:green;font-weight:bold;}" +

                    ".back{display:block;text-align:center;" +
                    "margin-top:25px;color:#2196f3;" +
                    "text-decoration:none;}" +
                    "</style></head>"
            );

            response.getWriter().println("<body>");

            response.getWriter().println(
                    "<div class='container'>"
            );

            response.getWriter().println(
                    "<h1>My Room Allocation</h1>"
            );

            if (rs.next()) {

                response.getWriter().println(
                        "<div class='box'>"
                );

                response.getWriter().println(
                        "<div class='row'>" +
                        "<span class='label'>Student ID:</span> "
                        + rs.getString("student_id") +
                        "</div>"
                );

                response.getWriter().println(
                        "<div class='row'>" +
                        "<span class='label'>Room Number:</span> "
                        + rs.getString("room_number") +
                        "</div>"
                );

                response.getWriter().println(
                        "<div class='row'>" +
                        "<span class='label'>Block:</span> "
                        + rs.getString("block_name") +
                        "</div>"
                );

                response.getWriter().println(
                        "<div class='row'>" +
                        "<span class='label'>Room Type:</span> "
                        + rs.getString("room_type") +
                        "</div>"
                );

                response.getWriter().println(
                        "<div class='row'>" +
                        "<span class='label'>Capacity:</span> "
                        + rs.getInt("capacity") +
                        "</div>"
                );

                response.getWriter().println(
                        "<div class='row'>" +
                        "<span class='label'>Occupied:</span> "
                        + rs.getInt("occupied") +
                        "</div>"
                );

                response.getWriter().println(
                        "<div class='row'>" +
                        "<span class='label'>Allocation Status:</span> " +
                        "<span class='approved'>Approved</span>" +
                        "</div>"
                );

                response.getWriter().println(
                        "</div>"
                );

            } else {

                response.getWriter().println(
                        "<h3>No approved room allocation found.</h3>"
                );

                response.getWriter().println(
                        "<p>Your room request may still be pending.</p>"
                );
            }

            response.getWriter().println(
                    "<a class='back' " +
                    "href='student-dashboard.html'>" +
                    "← Back to Dashboard</a>"
            );

            response.getWriter().println("</div>");

            response.getWriter().println("</body></html>");

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                    "<h2>Unable to load allocation</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}