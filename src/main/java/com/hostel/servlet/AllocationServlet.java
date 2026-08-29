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

@WebServlet("/AllocationServlet")
public class AllocationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String studentId = request.getParameter("studentId");
        String roomId = request.getParameter("roomId");
        String reason = request.getParameter("reason");

        response.setContentType("text/html");

        Connection con = null;

        try {

            con = DatabaseConnection.getConnection();

            if (con == null) {
                response.getWriter().println(
                    "<h2>Database Connection Failed</h2>"
                );
                return;
            }

            // STEP 1: Check whether student already has
            // Pending or Approved allocation
            String checkStudentSql =
                    "SELECT status FROM allocations "
                  + "WHERE student_id = ? "
                  + "AND status IN ('Pending', 'Approved')";

            PreparedStatement checkStudentPs =
                    con.prepareStatement(checkStudentSql);

            checkStudentPs.setString(1, studentId);

            ResultSet studentRs =
                    checkStudentPs.executeQuery();

            if (studentRs.next()) {

                String existingStatus =
                        studentRs.getString("status");

                studentRs.close();
                checkStudentPs.close();

                response.getWriter().println(
                    "<html><body>"
                );

                response.getWriter().println(
                    "<h2>Request Already Exists</h2>"
                );

                response.getWriter().println(
                    "<p>You already have a "
                    + existingStatus.toLowerCase()
                    + " room allocation request.</p>"
                );

                response.getWriter().println(
                    "<p>You cannot submit another request.</p>"
                );

                response.getWriter().println(
                    "<a href='student-dashboard.html'>"
                    + "Go to Dashboard</a>"
                );

                response.getWriter().println(
                    "</body></html>"
                );

                con.close();

                return;
            }

            studentRs.close();
            checkStudentPs.close();

            // STEP 2: Check room availability
            String roomSql =
                    "SELECT capacity, occupied, status "
                  + "FROM rooms "
                  + "WHERE room_id = ?";

            PreparedStatement roomPs =
                    con.prepareStatement(roomSql);

            roomPs.setInt(1, Integer.parseInt(roomId));

            ResultSet roomRs =
                    roomPs.executeQuery();

            if (!roomRs.next()) {

                roomRs.close();
                roomPs.close();
                con.close();

                response.getWriter().println(
                    "<h2>Room Not Found</h2>"
                );

                response.getWriter().println(
                    "<p>The selected room does not exist.</p>"
                );

                response.getWriter().println(
                    "<a href='allocation.html'>"
                    + "Go Back</a>"
                );

                return;
            }

            int capacity =
                    roomRs.getInt("capacity");

            int occupied =
                    roomRs.getInt("occupied");

            String status =
                    roomRs.getString("status");

            roomRs.close();
            roomPs.close();

            // STEP 3: Check whether room is full
            if (occupied >= capacity ||
                    "Full".equalsIgnoreCase(status)) {

                con.close();

                response.getWriter().println(
                    "<h2>Room Full</h2>"
                );

                response.getWriter().println(
                    "<p>Sorry, this room is already full.</p>"
                );

                response.getWriter().println(
                    "<a href='allocation.html'>"
                    + "Choose Another Room</a>"
                );

                return;
            }

            // STEP 4: Insert new request
            String sql =
                    "INSERT INTO allocations "
                  + "(student_id, room_id, reason, status) "
                  + "VALUES (?, ?, ?, 'Pending')";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, studentId);
            ps.setInt(2, Integer.parseInt(roomId));
            ps.setString(3, reason);

            int result =
                    ps.executeUpdate();

            ps.close();
            con.close();

            if (result > 0) {

                response.getWriter().println(
                    "<html><body>"
                );

                response.getWriter().println(
                    "<h2>Request Submitted Successfully!</h2>"
                );

                response.getWriter().println(
                    "<p>Your room allocation request "
                    + "is now pending.</p>"
                );

                response.getWriter().println(
                    "<a href='student-dashboard.html'>"
                    + "Go to Dashboard</a>"
                );

                response.getWriter().println(
                    "</body></html>"
                );

            } else {

                response.getWriter().println(
                    "<h2>Request Submission Failed</h2>"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            response.getWriter().println(
                "<h2>Allocation Failed</h2>"
            );

            response.getWriter().println(
                "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}