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
        String bedNumber = request.getParameter("bedNumber");
        String reason = request.getParameter("reason");

        response.setContentType("text/html;charset=UTF-8");

        Connection con = null;

        try {

            // Basic validation
            if (studentId == null || studentId.trim().isEmpty()
                    || roomId == null || roomId.trim().isEmpty()
                    || bedNumber == null || bedNumber.trim().isEmpty()
                    || reason == null || reason.trim().isEmpty()) {

                response.getWriter().println(
                    "<html><body>"
                );

                response.getWriter().println(
                    "<h2>Invalid Request</h2>"
                );

                response.getWriter().println(
                    "<p>Please fill all the required fields.</p>"
                );

                response.getWriter().println(
                    "<a href='allocation.html'>Go Back</a>"
                );

                response.getWriter().println(
                    "</body></html>"
                );

                return;
            }

            int roomIdValue = Integer.parseInt(roomId);
            int bedNumberValue = Integer.parseInt(bedNumber);

            con = DatabaseConnection.getConnection();

            if (con == null) {

                response.getWriter().println(
                    "<h2>Database Connection Failed</h2>"
                );

                return;
            }

            // STEP 1:
            // Check whether student already has
            // Pending or Approved allocation

            String checkStudentSql =
                    "SELECT status FROM allocations "
                  + "WHERE student_id = ? "
                  + "AND status IN ('Pending', 'Approved')";

            PreparedStatement checkStudentPs =
                    con.prepareStatement(checkStudentSql);

            checkStudentPs.setString(1, studentId.trim());

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


            // STEP 2:
            // Check room details

            String roomSql =
                    "SELECT room_number, capacity, occupied, status "
                  + "FROM rooms "
                  + "WHERE room_id = ?";

            PreparedStatement roomPs =
                    con.prepareStatement(roomSql);

            roomPs.setInt(1, roomIdValue);

            ResultSet roomRs =
                    roomPs.executeQuery();

            if (!roomRs.next()) {

                roomRs.close();
                roomPs.close();
                con.close();

                response.getWriter().println(
                    "<html><body>"
                );

                response.getWriter().println(
                    "<h2>Room Not Found</h2>"
                );

                response.getWriter().println(
                    "<p>The selected room does not exist.</p>"
                );

                response.getWriter().println(
                    "<a href='allocation.html'>Go Back</a>"
                );

                response.getWriter().println(
                    "</body></html>"
                );

                return;
            }

            String roomNumber =
                    roomRs.getString("room_number");

            int capacity =
                    roomRs.getInt("capacity");

            int occupied =
                    roomRs.getInt("occupied");

            String status =
                    roomRs.getString("status");

            roomRs.close();
            roomPs.close();


            // STEP 3:
            // Check whether room is full

            if (occupied >= capacity
                    || "Full".equalsIgnoreCase(status)) {

                con.close();

                response.getWriter().println(
                    "<html><body>"
                );

                response.getWriter().println(
                    "<h2>Room Full</h2>"
                );

                response.getWriter().println(
                    "<p>Sorry, Room "
                    + roomNumber
                    + " is already full.</p>"
                );

                response.getWriter().println(
                    "<a href='RoomsServlet'>"
                    + "Choose Another Room</a>"
                );

                response.getWriter().println(
                    "</body></html>"
                );

                return;
            }


            // STEP 4:
            // Validate bed number

            if (bedNumberValue <= occupied
                    || bedNumberValue > capacity) {

                con.close();

                response.getWriter().println(
                    "<html><body>"
                );

                response.getWriter().println(
                    "<h2>Invalid Bed Number</h2>"
                );

                response.getWriter().println(
                    "<p>Please select an available bed.</p>"
                );

                response.getWriter().println(
                    "<a href='RoomsServlet'>"
                    + "Back to Rooms</a>"
                );

                response.getWriter().println(
                    "</body></html>"
                );

                return;
            }


            // STEP 5:
            // Check whether selected bed is already
            // Pending or Approved for another student

            String checkBedSql =
                    "SELECT allocation_id FROM allocations "
                  + "WHERE room_id = ? "
                  + "AND bed_number = ? "
                  + "AND status IN ('Pending', 'Approved')";

            PreparedStatement checkBedPs =
                    con.prepareStatement(checkBedSql);

            checkBedPs.setInt(1, roomIdValue);
            checkBedPs.setInt(2, bedNumberValue);

            ResultSet bedRs =
                    checkBedPs.executeQuery();

            if (bedRs.next()) {

                bedRs.close();
                checkBedPs.close();
                con.close();

                response.getWriter().println(
                    "<html><body>"
                );

                response.getWriter().println(
                    "<h2>Bed Already Requested</h2>"
                );

                response.getWriter().println(
                    "<p>The selected bed is already "
                    + "allocated or requested.</p>"
                );

                response.getWriter().println(
                    "<a href='RoomsServlet'>"
                    + "Choose Another Room</a>"
                );

                response.getWriter().println(
                    "</body></html>"
                );

                return;
            }

            bedRs.close();
            checkBedPs.close();


            // STEP 6:
            // Insert allocation request

            String sql =
                    "INSERT INTO allocations "
                  + "(student_id, room_id, bed_number, reason, status) "
                  + "VALUES (?, ?, ?, ?, 'Pending')";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, studentId.trim());
            ps.setInt(2, roomIdValue);
            ps.setInt(3, bedNumberValue);
            ps.setString(4, reason.trim());

            int result =
                    ps.executeUpdate();

            ps.close();
            con.close();


            // STEP 7:
            // Show result

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
                    "<p><strong>Room:</strong> "
                    + roomNumber
                    + "</p>"
                );

                response.getWriter().println(
                    "<p><strong>Bed:</strong> "
                    + bedNumberValue
                    + "</p>"
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

        } catch (NumberFormatException e) {

            response.getWriter().println(
                "<html><body>"
            );

            response.getWriter().println(
                "<h2>Invalid Room or Bed</h2>"
            );

            response.getWriter().println(
                "<p>Please select a valid room and bed.</p>"
            );

            response.getWriter().println(
                "<a href='RoomsServlet'>"
                + "Back to Rooms</a>"
            );

            response.getWriter().println(
                "</body></html>"
            );

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