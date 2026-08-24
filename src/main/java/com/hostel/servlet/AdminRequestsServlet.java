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

@WebServlet("/AdminRequestsServlet")
public class AdminRequestsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String sql = "SELECT allocation_id, student_id, room_id, reason, status "
                   + "FROM allocations "
                   + "WHERE status = 'Pending'";

        try {

            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            response.getWriter().println("<!DOCTYPE html>");
            response.getWriter().println("<html>");
            response.getWriter().println("<head>");
            response.getWriter().println("<title>Allocation Requests</title>");

            response.getWriter().println(
                "<style>" +
                "body{font-family:Arial;background:#f4f8fc;padding:30px;}" +
                ".container{max-width:1000px;margin:auto;background:white;padding:30px;border-radius:10px;}" +
                "h1{text-align:center;color:#1e3a5f;}" +
                "table{width:100%;border-collapse:collapse;margin-top:25px;}" +
                "th,td{padding:12px;border:1px solid #ddd;text-align:center;}" +
                "th{background:#2196f3;color:white;}" +
                ".approve{background:#28a745;color:white;border:none;padding:8px 14px;border-radius:5px;cursor:pointer;}" +
                ".reject{background:#dc3545;color:white;border:none;padding:8px 14px;border-radius:5px;cursor:pointer;}" +
                "</style>"
            );

            response.getWriter().println("</head>");
            response.getWriter().println("<body>");

            response.getWriter().println("<div class='container'>");

            response.getWriter().println(
                "<h1>Hostel Allocation Requests</h1>"
            );

            response.getWriter().println("<table>");

            response.getWriter().println(
                "<tr>" +
                "<th>Allocation ID</th>" +
                "<th>Student ID</th>" +
                "<th>Room ID</th>" +
                "<th>Reason</th>" +
                "<th>Status</th>" +
                "<th>Action</th>" +
                "</tr>"
            );

            boolean found = false;

            while (rs.next()) {

                found = true;

                int allocationId =
                    rs.getInt("allocation_id");

                String studentId =
                    rs.getString("student_id");

                int roomId =
                    rs.getInt("room_id");

                String reason =
                    rs.getString("reason");

                String status =
                    rs.getString("status");

                response.getWriter().println("<tr>");

                response.getWriter().println(
                    "<td>" + allocationId + "</td>"
                );

                response.getWriter().println(
                    "<td>" + studentId + "</td>"
                );

                response.getWriter().println(
                    "<td>" + roomId + "</td>"
                );

                response.getWriter().println(
                    "<td>" + reason + "</td>"
                );

                response.getWriter().println(
                    "<td>" + status + "</td>"
                );

                response.getWriter().println(
                    "<td>" +

                    "<form action='ApproveServlet' method='post' style='display:inline;'>" +

                    "<input type='hidden' name='allocationId' value='" +
                    allocationId + "'>" +

                    "<button type='submit' class='approve'>Approve</button>" +

                    "</form>" +

                    "&nbsp;" +

                    "<form action='RejectServlet' method='post' style='display:inline;'>" +

                    "<input type='hidden' name='allocationId' value='" +
                    allocationId + "'>" +

                    "<button type='submit' class='reject'>Reject</button>" +

                    "</form>" +

                    "</td>"
                );

                response.getWriter().println("</tr>");
            }

            if (!found) {

                response.getWriter().println(
                    "<tr><td colspan='6'>No pending requests</td></tr>"
                );
            }

            response.getWriter().println("</table>");

            response.getWriter().println(
                "<br><a href='admin-dashboard.html'>← Back to Admin Dashboard</a>"
            );

            response.getWriter().println("</div>");

            response.getWriter().println("</body>");
            response.getWriter().println("</html>");

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "<h2>Unable to load allocation requests</h2>"
            );

            response.getWriter().println(
                "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}