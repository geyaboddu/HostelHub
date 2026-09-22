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

@WebServlet("/AllocationServlet")
public class AllocationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // =========================================================
    // GET - Load available beds
    // =========================================================

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        HttpSession session =
                request.getSession(false);

        // -----------------------------------------------------
        // 1. Check login
        // -----------------------------------------------------

        if (session == null ||
                session.getAttribute("studentId") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            out.println(
                    "{\"error\":\"Please login first\"}");

            return;
        }

        String studentId =
                (String) session.getAttribute("studentId");

        Connection con = null;

        try {

            con = DatabaseConnection.getConnection();

            if (con == null) {

                response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                out.println(
                        "{\"error\":\"Database connection failed\"}");

                return;
            }

            // -------------------------------------------------
            // 2. Check whether student already has
            //    Pending or Approved request
            // -------------------------------------------------

            String checkStudentSql =
                    "SELECT status "
                  + "FROM allocations "
                  + "WHERE student_id = ? "
                  + "AND status IN ('Pending', 'Approved') "
                  + "ORDER BY allocation_id DESC "
                  + "LIMIT 1";

            PreparedStatement checkStudent =
                    con.prepareStatement(checkStudentSql);

            checkStudent.setString(1, studentId);

            ResultSet studentRs =
                    checkStudent.executeQuery();

            if (studentRs.next()) {

                String status =
                        studentRs.getString("status");

                studentRs.close();
                checkStudent.close();

                response.setStatus(
                        HttpServletResponse.SC_FORBIDDEN);

                if ("Approved".equalsIgnoreCase(status)) {

                    out.println(
                        "{\"error\":\"You already have an approved room allocation\"}");

                } else {

                    out.println(
                        "{\"error\":\"You already have a pending room request\"}");
                }

                return;
            }

            studentRs.close();
            checkStudent.close();

            // -------------------------------------------------
            // 3. Get room ID
            // -------------------------------------------------

            String roomIdParam =
                    request.getParameter("roomId");

            if (roomIdParam == null ||
                    roomIdParam.trim().isEmpty()) {

                response.setStatus(
                        HttpServletResponse.SC_BAD_REQUEST);

                out.println(
                        "{\"error\":\"Room ID is required\"}");

                return;
            }

            int roomId =
                    Integer.parseInt(roomIdParam);

            // -------------------------------------------------
            // 4. Get room capacity
            // -------------------------------------------------

            String roomSql =
                    "SELECT capacity "
                  + "FROM rooms "
                  + "WHERE room_id = ?";

            PreparedStatement roomStmt =
                    con.prepareStatement(roomSql);

            roomStmt.setInt(1, roomId);

            ResultSet roomRs =
                    roomStmt.executeQuery();

            if (!roomRs.next()) {

                roomRs.close();
                roomStmt.close();

                response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND);

                out.println(
                        "{\"error\":\"Room not found\"}");

                return;
            }

            int capacity =
                    roomRs.getInt("capacity");

            roomRs.close();
            roomStmt.close();

            // -------------------------------------------------
            // 5. Find occupied beds
            // -------------------------------------------------

            String bedSql =
                    "SELECT bed_number "
                  + "FROM allocations "
                  + "WHERE room_id = ? "
                  + "AND status IN ('Pending', 'Approved') "
                  + "ORDER BY bed_number";

            PreparedStatement bedStmt =
                    con.prepareStatement(bedSql);

            bedStmt.setInt(1, roomId);

            ResultSet bedRs =
                    bedStmt.executeQuery();

            StringBuilder occupiedBeds =
                    new StringBuilder();

            occupiedBeds.append("[");

            boolean first = true;

            while (bedRs.next()) {

                if (!first) {
                    occupiedBeds.append(",");
                }

                occupiedBeds.append(
                        bedRs.getInt("bed_number"));

                first = false;
            }

            occupiedBeds.append("]");

            bedRs.close();
            bedStmt.close();

            // -------------------------------------------------
            // 6. Send JSON response
            // -------------------------------------------------

            out.println(
                    "{\"capacity\":"
                    + capacity
                    + ",\"occupiedBeds\":"
                    + occupiedBeds.toString()
                    + "}");

        } catch (NumberFormatException e) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            out.println(
                    "{\"error\":\"Invalid room ID\"}");

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            out.println(
                    "{\"error\":\"Unable to load beds\"}");

        } finally {

            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // =========================================================
    // POST - Submit allocation request
    // =========================================================

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out =
                response.getWriter();

        HttpSession session =
                request.getSession(false);

        // -----------------------------------------------------
        // 1. Check login
        // -----------------------------------------------------

        if (session == null ||
                session.getAttribute("studentId") == null) {

            out.println("<h2>Please login first.</h2>");
            out.println(
                    "<a href='login.html'>Go to Login</a>");

            return;
        }

        String studentId =
                (String) session.getAttribute("studentId");

        String roomIdParam =
                request.getParameter("roomId");

        String bedNumberParam =
                request.getParameter("bedNumber");

        String reason =
                request.getParameter("reason");

        // -----------------------------------------------------
        // 2. Validate input
        // -----------------------------------------------------

        if (roomIdParam == null ||
                bedNumberParam == null ||
                roomIdParam.trim().isEmpty() ||
                bedNumberParam.trim().isEmpty()) {

            out.println("<h2>Invalid room or bed.</h2>");
            out.println(
                    "<a href='allocation.html'>Go Back</a>");

            return;
        }

        Connection con = null;

        try {

            int roomId =
                    Integer.parseInt(roomIdParam);

            int bedNumber =
                    Integer.parseInt(bedNumberParam);

            con = DatabaseConnection.getConnection();

            if (con == null) {

                out.println(
                        "<h2>Database connection failed.</h2>");

                return;
            }

            // -------------------------------------------------
            // 3. Check existing student request
            // -------------------------------------------------

            String checkStudentSql =
                    "SELECT allocation_id, status "
                  + "FROM allocations "
                  + "WHERE student_id = ? "
                  + "AND status IN ('Pending', 'Approved') "
                  + "ORDER BY allocation_id DESC "
                  + "LIMIT 1";

            PreparedStatement checkStudent =
                    con.prepareStatement(checkStudentSql);

            checkStudent.setString(1, studentId);

            ResultSet studentRs =
                    checkStudent.executeQuery();

            if (studentRs.next()) {

                String status =
                        studentRs.getString("status");

                studentRs.close();
                checkStudent.close();

                out.println("<h2>Room Request Already Exists</h2>");

                if ("Approved".equalsIgnoreCase(status)) {

                    out.println(
                        "<p>You already have an approved room allocation.</p>");

                } else {

                    out.println(
                        "<p>You already have a pending room request.</p>");
                }

                out.println(
                        "<a href='StudentDashboardServlet'>Go to Dashboard</a>");

                return;
            }

            studentRs.close();
            checkStudent.close();

            // -------------------------------------------------
            // 4. Check room
            // -------------------------------------------------

            String roomSql =
                    "SELECT capacity, occupied "
                  + "FROM rooms "
                  + "WHERE room_id = ?";

            PreparedStatement roomStmt =
                    con.prepareStatement(roomSql);

            roomStmt.setInt(1, roomId);

            ResultSet roomRs =
                    roomStmt.executeQuery();

            if (!roomRs.next()) {

                roomRs.close();
                roomStmt.close();

                out.println(
                        "<h2>Room not found.</h2>");

                out.println(
                        "<a href='allocation.html'>Go Back</a>");

                return;
            }

            int capacity =
                    roomRs.getInt("capacity");

            int occupied =
                    roomRs.getInt("occupied");

            roomRs.close();
            roomStmt.close();

            // -------------------------------------------------
            // 5. Check room full
            // -------------------------------------------------

            if (occupied >= capacity) {

                out.println(
                        "<h2>Room is already full.</h2>");

                out.println(
                        "<a href='allocation.html'>Go Back</a>");

                return;
            }

            // -------------------------------------------------
            // 6. Validate bed number
            // -------------------------------------------------

            if (bedNumber < 1 ||
                    bedNumber > capacity) {

                out.println(
                        "<h2>Invalid bed number.</h2>");

                out.println(
                        "<a href='allocation.html'>Go Back</a>");

                return;
            }

            // -------------------------------------------------
            // 7. Check whether bed already requested
            // -------------------------------------------------

            String checkBedSql =
                    "SELECT allocation_id "
                  + "FROM allocations "
                  + "WHERE room_id = ? "
                  + "AND bed_number = ? "
                  + "AND status IN ('Pending', 'Approved') "
                  + "LIMIT 1";

            PreparedStatement checkBed =
                    con.prepareStatement(checkBedSql);

            checkBed.setInt(1, roomId);
            checkBed.setInt(2, bedNumber);

            ResultSet bedRs =
                    checkBed.executeQuery();

            if (bedRs.next()) {

                bedRs.close();
                checkBed.close();

                out.println(
                        "<h2>That bed is already occupied or requested.</h2>");

                out.println(
                        "<a href='allocation.html'>Choose another bed</a>");

                return;
            }

            bedRs.close();
            checkBed.close();

            // -------------------------------------------------
            // 8. Insert allocation request
            // -------------------------------------------------

            String insertSql =
                    "INSERT INTO allocations "
                  + "(student_id, room_id, bed_number, reason, status) "
                  + "VALUES (?, ?, ?, ?, 'Pending')";

            PreparedStatement insert =
                    con.prepareStatement(insertSql);

            insert.setString(1, studentId);
            insert.setInt(2, roomId);
            insert.setInt(3, bedNumber);
            insert.setString(4, reason);

            int result =
                    insert.executeUpdate();

            insert.close();

            // -------------------------------------------------
            // 9. Success
            // -------------------------------------------------

            if (result > 0) {

                out.println(
                        "<!DOCTYPE html>");

                out.println(
                        "<html><head><title>Request Submitted</title></head><body>");

                out.println(
                        "<div style='text-align:center;margin-top:80px;'>");

                out.println(
                        "<h1>Room Request Submitted Successfully!</h1>");

                out.println(
                        "<p>Your room allocation request is now <b>Pending</b>.</p>");

                out.println(
                        "<p>Please wait for the admin to approve your request.</p>");

                out.println(
                        "<br>");

                out.println(
                        "<a href='StudentDashboardServlet'>Go to Student Dashboard</a>");

                out.println(
                        "</div>");

                out.println(
                        "</body></html>");

            } else {

                out.println(
                        "<h2>Request could not be submitted.</h2>");

                out.println(
                        "<a href='allocation.html'>Go Back</a>");
            }

        } catch (NumberFormatException e) {

            out.println(
                    "<h2>Invalid room or bed number.</h2>");

            out.println(
                    "<a href='allocation.html'>Go Back</a>");

        } catch (Exception e) {

            e.printStackTrace();

            out.println(
                    "<h2>Allocation Failed</h2>");

            out.println(
                    "<p>Error: "
                    + e.getMessage()
                    + "</p>");

            out.println(
                    "<a href='allocation.html'>Go Back</a>");

        } finally {

            try {

                if (con != null) {
                    con.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}