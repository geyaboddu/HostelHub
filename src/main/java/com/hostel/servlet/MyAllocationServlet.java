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

@WebServlet("/MyAllocationServlet")
public class MyAllocationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // ==========================================
        // CHECK LOGIN SESSION
        // ==========================================

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("studentId") == null) {

            response.sendRedirect("login.html");
            return;
        }

        // Get student ID from login session
        String studentId =
                (String) session.getAttribute("studentId");


        // ==========================================
        // GET LATEST ALLOCATION
        // Pending / Approved / Rejected
        // ==========================================

        String sql =
                "SELECT a.allocation_id, a.student_id, "
              + "a.room_id, a.status, "
              + "r.room_number, r.block, "
              + "r.room_type, r.capacity, r.occupied "
              + "FROM allocations a "
              + "JOIN rooms r ON a.room_id = r.room_id "
              + "WHERE a.student_id = ? "
              + "ORDER BY a.allocation_id DESC "
              + "LIMIT 1";


        try {

            Connection con =
                    DatabaseConnection.getConnection();

            if (con == null) {

                response.getWriter().println(
                    "<h2>Database Connection Failed</h2>"
                );

                return;
            }


            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, studentId);

            ResultSet rs =
                    ps.executeQuery();


            // ==========================================
            // HTML START
            // ==========================================

            response.getWriter().println(
                "<!DOCTYPE html>"
            );

            response.getWriter().println(
                "<html>"
            );

            response.getWriter().println(
                "<head>"
            );

            response.getWriter().println(
                "<meta charset='UTF-8'>"
            );

            response.getWriter().println(
                "<meta name='viewport' " +
                "content='width=device-width, initial-scale=1.0'>"
            );

            response.getWriter().println(
                "<title>My Allocation | HostelHub</title>"
            );


            // ==========================================
            // CSS
            // ==========================================

            response.getWriter().println(
                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#f4f8fc;" +
                "padding:40px;" +
                "}" +

                ".container{" +
                "max-width:600px;" +
                "margin:auto;" +
                "background:white;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "box-shadow:0 5px 20px rgba(0,0,0,0.1);" +
                "}" +

                "h1{" +
                "text-align:center;" +
                "color:#1e3a5f;" +
                "}" +

                ".box{" +
                "margin-top:25px;" +
                "}" +

                ".row{" +
                "padding:12px;" +
                "border-bottom:1px solid #ddd;" +
                "}" +

                ".label{" +
                "font-weight:bold;" +
                "color:#555;" +
                "}" +

                ".approved{" +
                "color:green;" +
                "font-weight:bold;" +
                "}" +

                ".rejected{" +
                "color:#dc3545;" +
                "font-weight:bold;" +
                "}" +

                ".pending{" +
                "color:#f39c12;" +
                "font-weight:bold;" +
                "}" +

                ".message{" +
                "padding:15px;" +
                "margin-top:20px;" +
                "border-radius:6px;" +
                "background:#f8f9fa;" +
                "}" +

                ".back{" +
                "display:block;" +
                "text-align:center;" +
                "margin-top:25px;" +
                "color:#2196f3;" +
                "text-decoration:none;" +
                "}" +

                "</style>"
            );


            response.getWriter().println(
                "</head>"
            );

            response.getWriter().println(
                "<body>"
            );


            response.getWriter().println(
                "<div class='container'>"
            );


            response.getWriter().println(
                "<h1>My Room Allocation</h1>"
            );


            // ==========================================
            // DISPLAY ALLOCATION
            // ==========================================

            if (rs.next()) {

                int allocationId =
                        rs.getInt("allocation_id");

                String status =
                        rs.getString("status");

                String roomNumber =
                        rs.getString("room_number");

                String block =
                        rs.getString("block");

                String roomType =
                        rs.getString("room_type");

                int capacity =
                        rs.getInt("capacity");

                int occupied =
                        rs.getInt("occupied");


                response.getWriter().println(
                    "<div class='box'>"
                );


                // Student ID

                response.getWriter().println(
                    "<div class='row'>" +
                    "<span class='label'>Student ID:</span> "
                    + studentId +
                    "</div>"
                );


                // Allocation ID

                response.getWriter().println(
                    "<div class='row'>" +
                    "<span class='label'>Allocation ID:</span> "
                    + allocationId +
                    "</div>"
                );


                // Room Number

                response.getWriter().println(
                    "<div class='row'>" +
                    "<span class='label'>Room Number:</span> "
                    + roomNumber +
                    "</div>"
                );


                // Block

                response.getWriter().println(
                    "<div class='row'>" +
                    "<span class='label'>Block:</span> "
                    + block +
                    "</div>"
                );


                // Room Type

                response.getWriter().println(
                    "<div class='row'>" +
                    "<span class='label'>Room Type:</span> "
                    + roomType +
                    "</div>"
                );


                // Capacity

                response.getWriter().println(
                    "<div class='row'>" +
                    "<span class='label'>Capacity:</span> "
                    + capacity +
                    "</div>"
                );


                // Occupied

                response.getWriter().println(
                    "<div class='row'>" +
                    "<span class='label'>Occupied:</span> "
                    + occupied +
                    "</div>"
                );


                // ==========================================
                // STATUS
                // ==========================================

                String statusClass = "";

                if ("Approved".equalsIgnoreCase(status)) {

                    statusClass = "approved";

                } else if ("Rejected".equalsIgnoreCase(status)) {

                    statusClass = "rejected";

                } else {

                    statusClass = "pending";
                }


                response.getWriter().println(
                    "<div class='row'>" +
                    "<span class='label'>Allocation Status:</span> " +
                    "<span class='" +
                    statusClass +
                    "'>" +
                    status +
                    "</span>" +
                    "</div>"
                );


                // ==========================================
                // STATUS MESSAGE
                // ==========================================

                if ("Approved".equalsIgnoreCase(status)) {

                    response.getWriter().println(
                        "<div class='message'>" +
                        "<strong>Congratulations!</strong><br>" +
                        "Your room allocation has been approved." +
                        "</div>"
                    );

                } else if ("Rejected".equalsIgnoreCase(status)) {

                    response.getWriter().println(
                        "<div class='message'>" +
                        "<strong>Request Rejected</strong><br>" +
                        "Your room allocation request was rejected by the admin." +
                        "</div>"
                    );

                } else {

                    response.getWriter().println(
                        "<div class='message'>" +
                        "<strong>Request Pending</strong><br>" +
                        "Your room allocation request is waiting for admin approval." +
                        "</div>"
                    );
                }


                response.getWriter().println(
                    "</div>"
                );

            } else {

                response.getWriter().println(
                    "<div class='message'>" +
                    "<h3>No Room Allocation Request</h3>" +
                    "<p>You have not submitted any room allocation request yet.</p>" +
                    "</div>"
                );
            }


            // ==========================================
            // BACK TO DASHBOARD
            // ==========================================

            response.getWriter().println(
                "<a class='back' " +
                "href='StudentDashboardServlet'>" +
                "← Back to Dashboard</a>"
            );


            response.getWriter().println(
                "</div>"
            );

            response.getWriter().println(
                "</body>"
            );

            response.getWriter().println(
                "</html>"
            );


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