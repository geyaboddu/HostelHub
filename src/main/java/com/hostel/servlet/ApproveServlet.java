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

@WebServlet("/ApproveServlet")
public class ApproveServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String allocationId = request.getParameter("allocationId");

        Connection con = null;

        try {

            con = DatabaseConnection.getConnection();

            // Get the room ID for this allocation
            String getRoomSQL =
                    "SELECT room_id FROM allocations WHERE allocation_id = ?";

            PreparedStatement getRoom =
                    con.prepareStatement(getRoomSQL);

            getRoom.setInt(1, Integer.parseInt(allocationId));

            ResultSet rs = getRoom.executeQuery();

            if (rs.next()) {

                int roomId = rs.getInt("room_id");

                rs.close();
                getRoom.close();

                // Approve the allocation
                String approveSQL =
                        "UPDATE allocations "
                      + "SET status = 'Approved' "
                      + "WHERE allocation_id = ?";

                PreparedStatement approve =
                        con.prepareStatement(approveSQL);

                approve.setInt(1,
                        Integer.parseInt(allocationId));

                int updated = approve.executeUpdate();

                if (updated == 0) {
                    throw new Exception("Allocation ID not found: " + allocationId);
                }

                approve.close();

                // Increase occupied count
                String roomSQL =
                        "UPDATE rooms "
                      + "SET occupied = occupied + 1 "
                      + "WHERE room_id = ?";

                PreparedStatement roomUpdate =
                        con.prepareStatement(roomSQL);

                roomUpdate.setInt(1, roomId);

                int roomUpdated = roomUpdate.executeUpdate();

                if (roomUpdated == 0) {
                    throw new Exception("Room ID not found: " + roomId);
                }

                roomUpdate.close();

                // Check whether room is full
                String statusSQL =
                        "UPDATE rooms "
                      + "SET status = CASE "
                      + "WHEN occupied >= capacity THEN 'Full' "
                      + "ELSE 'Available' "
                      + "END "
                      + "WHERE room_id = ?";

                PreparedStatement statusUpdate =
                        con.prepareStatement(statusSQL);

                statusUpdate.setInt(1, roomId);

                statusUpdate.executeUpdate();

                statusUpdate.close();

                response.sendRedirect("AdminRequestsServlet");

            } else {

                rs.close();
                getRoom.close();

                response.setContentType("text/html");

                response.getWriter().println(
                    "<h2>No allocation request found.</h2>"
                );

                response.getWriter().println(
                    "<a href='AdminRequestsServlet'>Go Back</a>"
                );
            }

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html");

            response.getWriter().println("<h2>Approval Failed</h2>");

            response.getWriter().println(
                "<p><b>Error:</b> " + e.toString() + "</p>"
            );

            response.getWriter().println(
                "<p><b>Message:</b> " + e.getMessage() + "</p>"
            );
        }
    }
}