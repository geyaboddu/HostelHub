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

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // ==============================
        // ADMIN SESSION CHECK
        // ==============================

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("adminId") == null) {

            response.sendRedirect("admin-login.html");
            return;
        }

        // Prevent browser caching
        response.setHeader("Cache-Control",
                "no-cache, no-store, must-revalidate");

        response.setHeader("Pragma", "no-cache");

        response.setDateHeader("Expires", 0);

        response.setContentType("text/html;charset=UTF-8");

        Connection con = null;

        try {

            con = DatabaseConnection.getConnection();

            // ==============================
            // TOTAL STUDENTS
            // ==============================

            String studentSql =
                    "SELECT COUNT(*) FROM students";

            PreparedStatement studentPs =
                    con.prepareStatement(studentSql);

            ResultSet studentRs =
                    studentPs.executeQuery();

            int totalStudents = 0;

            if (studentRs.next()) {
                totalStudents = studentRs.getInt(1);
            }

            studentRs.close();
            studentPs.close();


            // ==============================
            // TOTAL ROOMS
            // ==============================

            String roomSql =
                    "SELECT COUNT(*) FROM rooms";

            PreparedStatement roomPs =
                    con.prepareStatement(roomSql);

            ResultSet roomRs =
                    roomPs.executeQuery();

            int totalRooms = 0;

            if (roomRs.next()) {
                totalRooms = roomRs.getInt(1);
            }

            roomRs.close();
            roomPs.close();


            // ==============================
            // AVAILABLE ROOMS
            // ==============================

            String availableSql =
                    "SELECT COUNT(*) FROM rooms " +
                    "WHERE status = 'Available'";

            PreparedStatement availablePs =
                    con.prepareStatement(availableSql);

            ResultSet availableRs =
                    availablePs.executeQuery();

            int availableRooms = 0;

            if (availableRs.next()) {
                availableRooms = availableRs.getInt(1);
            }

            availableRs.close();
            availablePs.close();


            // ==============================
            // OCCUPIED ROOMS
            // ==============================

            String occupiedSql =
                    "SELECT COUNT(*) FROM rooms " +
                    "WHERE occupied > 0";

            PreparedStatement occupiedPs =
                    con.prepareStatement(occupiedSql);

            ResultSet occupiedRs =
                    occupiedPs.executeQuery();

            int occupiedRooms = 0;

            if (occupiedRs.next()) {
                occupiedRooms = occupiedRs.getInt(1);
            }

            occupiedRs.close();
            occupiedPs.close();


            // ==============================
            // PENDING REQUESTS
            // ==============================

            String pendingSql =
                    "SELECT COUNT(*) FROM allocations " +
                    "WHERE status = 'Pending'";

            PreparedStatement pendingPs =
                    con.prepareStatement(pendingSql);

            ResultSet pendingRs =
                    pendingPs.executeQuery();

            int pendingRequests = 0;

            if (pendingRs.next()) {
                pendingRequests = pendingRs.getInt(1);
            }

            pendingRs.close();
            pendingPs.close();


            // ==============================
            // ADMIN NAME
            // ==============================

            String adminName =
                    (String) session.getAttribute("adminName");

            if (adminName == null) {
                adminName = "Admin";
            }


            // ==============================
            // HTML PAGE
            // ==============================

            response.getWriter().println(
                "<!DOCTYPE html>"
            );

            response.getWriter().println(
                "<html lang='en'>"
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
                "<title>Admin Dashboard | HostelHub</title>"
            );


            // ==============================
            // CSS
            // ==============================

            response.getWriter().println(
                "<style>" +

                "* {" +
                "margin:0;" +
                "padding:0;" +
                "box-sizing:border-box;" +
                "font-family:Arial,sans-serif;" +
                "}" +

                "body {" +
                "background:#f4f8fc;" +
                "min-height:100vh;" +
                "}" +

                ".navbar {" +
                "background:#1e3a5f;" +
                "color:white;" +
                "padding:18px 40px;" +
                "display:flex;" +
                "justify-content:space-between;" +
                "align-items:center;" +
                "}" +

                ".logo {" +
                "font-size:26px;" +
                "font-weight:bold;" +
                "}" +

                ".logo span {" +
                "color:#2196f3;" +
                "}" +

                ".logout {" +
                "background:#e53935;" +
                "color:white;" +
                "text-decoration:none;" +
                "padding:9px 18px;" +
                "border-radius:6px;" +
                "}" +

                ".logout:hover {" +
                "background:#c62828;" +
                "}" +

                ".container {" +
                "width:90%;" +
                "max-width:1100px;" +
                "margin:40px auto;" +
                "}" +

                "h1 {" +
                "color:#1e3a5f;" +
                "margin-bottom:8px;" +
                "}" +

                ".subtitle {" +
                "color:#666;" +
                "margin-bottom:30px;" +
                "}" +

                ".stats {" +
                "display:flex;" +
                "gap:20px;" +
                "flex-wrap:wrap;" +
                "}" +

                ".card {" +
                "background:white;" +
                "padding:25px;" +
                "border-radius:10px;" +
                "box-shadow:0 5px 15px rgba(0,0,0,0.10);" +
                "flex:1;" +
                "min-width:180px;" +
                "}" +

                ".card h3 {" +
                "color:#555;" +
                "margin-bottom:10px;" +
                "}" +

                ".card h2 {" +
                "font-size:32px;" +
                "color:#2196f3;" +
                "}" +

                ".links {" +
                "margin-top:30px;" +
                "background:white;" +
                "padding:25px;" +
                "border-radius:10px;" +
                "}" +

                ".btn {" +
                "display:inline-block;" +
                "padding:12px 18px;" +
                "margin:8px;" +
                "background:#2196f3;" +
                "color:white;" +
                "text-decoration:none;" +
                "border-radius:6px;" +
                "}" +

                ".btn:hover {" +
                "background:#1769aa;" +
                "}" +

                "</style>"
            );

            response.getWriter().println(
                "</head>"
            );

            response.getWriter().println(
                "<body>"
            );


            // ==============================
            // NAVIGATION BAR
            // ==============================

            response.getWriter().println(
                "<div class='navbar'>"
            );

            response.getWriter().println(
                "<div class='logo'>" +
                "Hostel<span>Hub</span>" +
                "</div>"
            );

            response.getWriter().println(
                "<a href='LogoutServlet' " +
                "class='logout'>Logout</a>"
            );

            response.getWriter().println(
                "</div>"
            );


            // ==============================
            // MAIN CONTAINER
            // ==============================

            response.getWriter().println(
                "<div class='container'>"
            );

            response.getWriter().println(
                "<h1>Admin Dashboard</h1>"
            );

            response.getWriter().println(
                "<p class='subtitle'>" +
                "Welcome, " + adminName +
                "</p>"
            );


            // ==============================
            // STATISTICS
            // ==============================

            response.getWriter().println(
                "<div class='stats'>"
            );


            response.getWriter().println(
                "<div class='card'>" +
                "<h3>Total Students</h3>" +
                "<h2>" + totalStudents + "</h2>" +
                "</div>"
            );


            response.getWriter().println(
                "<div class='card'>" +
                "<h3>Total Rooms</h3>" +
                "<h2>" + totalRooms + "</h2>" +
                "</div>"
            );


            response.getWriter().println(
                "<div class='card'>" +
                "<h3>Available Rooms</h3>" +
                "<h2>" + availableRooms + "</h2>" +
                "</div>"
            );


            response.getWriter().println(
                "<div class='card'>" +
                "<h3>Occupied Rooms</h3>" +
                "<h2>" + occupiedRooms + "</h2>" +
                "</div>"
            );


            response.getWriter().println(
                "<div class='card'>" +
                "<h3>Pending Requests</h3>" +
                "<h2>" + pendingRequests + "</h2>" +
                "</div>"
            );


            response.getWriter().println(
                "</div>"
            );


            // ==============================
            // QUICK ACTIONS
            // ==============================

            response.getWriter().println(
                "<div class='links'>"
            );

            response.getWriter().println(
                "<h2>Quick Actions</h2>"
            );


            response.getWriter().println(
                "<a class='btn' " +
                "href='AdminRequestsServlet'>" +
                "Allocation Requests</a>"
            );


            response.getWriter().println(
                "<a class='btn' " +
                "href='homepage.html'>" +
                "Home</a>"
            );


            response.getWriter().println(
                "</div>"
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


            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            if (con != null) {

                try {
                    con.close();

                } catch (Exception ignored) {
                }
            }

            response.getWriter().println(
                "<h2>Admin Dashboard Error</h2>"
            );

            response.getWriter().println(
                "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}