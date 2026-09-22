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

        HttpSession session = request.getSession(false);

        if (session == null ||
                session.getAttribute("adminId") == null) {

            response.sendRedirect("admin-login.html");
            return;
        }

        response.setHeader("Cache-Control",
                "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        response.setContentType("text/html;charset=UTF-8");

        Connection con = null;

        try {

            con = DatabaseConnection.getConnection();

            if (con == null) {
                throw new Exception("Database connection failed.");
            }

            // ==========================================
            // TOTAL STUDENTS
            // ==========================================

            int totalStudents = 0;

            String studentSql =
                    "SELECT COUNT(*) FROM students";

            PreparedStatement studentPs =
                    con.prepareStatement(studentSql);

            ResultSet studentRs =
                    studentPs.executeQuery();

            if (studentRs.next()) {
                totalStudents = studentRs.getInt(1);
            }

            studentRs.close();
            studentPs.close();


            // ==========================================
            // TOTAL ROOMS
            // ==========================================

            int totalRooms = 0;

            String roomSql =
                    "SELECT COUNT(*) FROM rooms";

            PreparedStatement roomPs =
                    con.prepareStatement(roomSql);

            ResultSet roomRs =
                    roomPs.executeQuery();

            if (roomRs.next()) {
                totalRooms = roomRs.getInt(1);
            }

            roomRs.close();
            roomPs.close();


            // ==========================================
            // AVAILABLE ROOMS
            // ==========================================

            int availableRooms = 0;

            String availableSql =
                    "SELECT COUNT(*) FROM rooms " +
                    "WHERE status = 'Available'";

            PreparedStatement availablePs =
                    con.prepareStatement(availableSql);

            ResultSet availableRs =
                    availablePs.executeQuery();

            if (availableRs.next()) {
                availableRooms = availableRs.getInt(1);
            }

            availableRs.close();
            availablePs.close();


            // ==========================================
            // OCCUPIED ROOMS
            // ==========================================

            int occupiedRooms = 0;

            String occupiedSql =
                    "SELECT COUNT(*) FROM rooms " +
                    "WHERE occupied > 0";

            PreparedStatement occupiedPs =
                    con.prepareStatement(occupiedSql);

            ResultSet occupiedRs =
                    occupiedPs.executeQuery();

            if (occupiedRs.next()) {
                occupiedRooms = occupiedRs.getInt(1);
            }

            occupiedRs.close();
            occupiedPs.close();


            // ==========================================
            // PENDING REQUESTS
            // ==========================================

            int pendingRequests = 0;

            String pendingSql =
                    "SELECT COUNT(*) FROM allocations " +
                    "WHERE LOWER(TRIM(status)) = 'pending'";

            PreparedStatement pendingPs =
                    con.prepareStatement(pendingSql);

            ResultSet pendingRs =
                    pendingPs.executeQuery();

            if (pendingRs.next()) {
                pendingRequests = pendingRs.getInt(1);
            }

            pendingRs.close();
            pendingPs.close();


            // ==========================================
            // APPROVED ALLOCATIONS
            // ==========================================

            int approvedRequests = 0;

            String approvedSql =
                    "SELECT COUNT(*) FROM allocations " +
                    "WHERE LOWER(TRIM(status)) = 'approved'";

            PreparedStatement approvedPs =
                    con.prepareStatement(approvedSql);

            ResultSet approvedRs =
                    approvedPs.executeQuery();

            if (approvedRs.next()) {
                approvedRequests = approvedRs.getInt(1);
            }

            approvedRs.close();
            approvedPs.close();
            String latestRequestsHtml = "";

            String latestSql =
                    "SELECT allocation_id, student_id, room_id, reason, status " +
                    "FROM allocations " +
                    "ORDER BY allocation_id DESC " +
                    "LIMIT 5";

            PreparedStatement latestPs =
                    con.prepareStatement(latestSql);

            ResultSet latestRs =
                    latestPs.executeQuery();

            while (latestRs.next()) {

                int allocationId =
                        latestRs.getInt("allocation_id");

                String studentId =
                        latestRs.getString("student_id");

                int roomId =
                        latestRs.getInt("room_id");

                String reason =
                        latestRs.getString("reason");

                String status =
                        latestRs.getString("status");

                if (reason == null || reason.trim().isEmpty()) {
                    reason = "No reason provided";
                }

                latestRequestsHtml +=
                        "<tr>" +
                        "<td>" + allocationId + "</td>" +
                        "<td>" + studentId + "</td>" +
                        "<td>Room " + roomId + "</td>" +
                        "<td>" + reason + "</td>" +
                        "<td><b>" + status + "</b></td>" +
                        "</tr>";
            }

            latestRs.close();
            latestPs.close();

            // ==========================================
            // ROOM OCCUPANCY PERCENTAGE
            // ==========================================

            int occupancyPercentage = 0;

            if (totalRooms > 0) {

                occupancyPercentage =
                        (occupiedRooms * 100) / totalRooms;
            }


            // ==========================================
            // ADMIN NAME
            // ==========================================

            String adminName =
                    (String) session.getAttribute("adminName");

            if (adminName == null ||
                    adminName.trim().isEmpty()) {

                adminName = "Hostel Administrator";
            }


            // ==========================================
            // HTML
            // ==========================================

            StringBuilder html =
                    new StringBuilder();


            html.append("<!DOCTYPE html>");

            html.append("<html lang='en'>");

            html.append("<head>");

            html.append(
                    "<meta charset='UTF-8'>");

            html.append(
                    "<meta name='viewport' " +
                    "content='width=device-width, " +
                    "initial-scale=1.0'>");

            html.append(
                    "<title>Admin Dashboard | HostelHub</title>");


            // ==========================================
            // CSS
            // ==========================================

            html.append("<style>");

            html.append(
                    "*{" +
                    "margin:0;" +
                    "padding:0;" +
                    "box-sizing:border-box;" +
                    "font-family:Arial,sans-serif;" +
                    "}"
            );

            html.append(
                    "body{" +
                    "background:#f4f7fb;" +
                    "color:#263238;" +
                    "min-height:100vh;" +
                    "}"
            );


            // HEADER

            html.append(
                    ".header{" +
                    "height:72px;" +
                    "background:#1e3a5f;" +
                    "display:flex;" +
                    "align-items:center;" +
                    "justify-content:space-between;" +
                    "padding:0 45px;" +
                    "color:white;" +
                    "}"
            );

            html.append(
                    ".logo{" +
                    "font-size:28px;" +
                    "font-weight:bold;" +
                    "}"
            );

            html.append(
                    ".logo span{" +
                    "color:#2196f3;" +
                    "}"
            );

            html.append(
                    ".header-right{" +
                    "display:flex;" +
                    "align-items:center;" +
                    "gap:22px;" +
                    "}"
            );

            html.append(
                    ".admin-label{" +
                    "font-size:15px;" +
                    "color:#e8eef5;" +
                    "}"
            );

            html.append(
                    ".logout{" +
                    "background:#e53935;" +
                    "color:white;" +
                    "text-decoration:none;" +
                    "padding:10px 20px;" +
                    "border-radius:6px;" +
                    "font-weight:bold;" +
                    "}"
            );

            html.append(
                    ".logout:hover{" +
                    "background:#c62828;" +
                    "}"
            );


            // MAIN CONTAINER

            html.append(
                    ".container{" +
                    "width:92%;" +
                    "max-width:1200px;" +
                    "margin:35px auto 50px;" +
                    "}"
            );


            // WELCOME

            html.append(
                    ".welcome{" +
                    "margin-bottom:28px;" +
                    "}"
            );

            html.append(
                    ".welcome h1{" +
                    "font-size:34px;" +
                    "color:#1e3a5f;" +
                    "margin-bottom:7px;" +
                    "}"
            );

            html.append(
                    ".welcome p{" +
                    "font-size:17px;" +
                    "color:#697586;" +
                    "}"
            );


            // STATISTICS

            html.append(
                    ".stats{" +
                    "display:grid;" +
                    "grid-template-columns:repeat(5,1fr);" +
                    "gap:18px;" +
                    "margin-bottom:25px;" +
                    "}"
            );


            html.append(
                    ".stat-card{" +
                    "background:white;" +
                    "border-radius:12px;" +
                    "padding:23px;" +
                    "box-shadow:" +
                    "0 5px 18px rgba(30,58,95,0.08);" +
                    "border:1px solid #edf1f5;" +
                    "}"
            );

            html.append(
                    ".stat-title{" +
                    "font-size:14px;" +
                    "font-weight:bold;" +
                    "color:#697586;" +
                    "margin-bottom:12px;" +
                    "}"
            );

            html.append(
                    ".stat-value{" +
                    "font-size:32px;" +
                    "font-weight:bold;" +
                    "color:#2196f3;" +
                    "}"
            );

            html.append(
                    ".stat-small{" +
                    "font-size:12px;" +
                    "color:#8a94a6;" +
                    "margin-top:6px;" +
                    "}"
            );


            // MAIN GRID

            html.append(
                    ".dashboard-grid{" +
                    "display:grid;" +
                    "grid-template-columns:1.35fr 1fr;" +
                    "gap:22px;" +
                    "}"
            );


            // PANEL

            html.append(
                    ".panel{" +
                    "background:white;" +
                    "border-radius:12px;" +
                    "padding:28px;" +
                    "box-shadow:" +
                    "0 5px 18px rgba(30,58,95,0.08);" +
                    "border:1px solid #edf1f5;" +
                    "}"
            );

            html.append(
                    ".panel h2{" +
                    "font-size:21px;" +
                    "color:#1e3a5f;" +
                    "margin-bottom:8px;" +
                    "}"
            );

            html.append(
                    ".panel-subtitle{" +
                    "color:#7b8794;" +
                    "font-size:14px;" +
                    "margin-bottom:25px;" +
                    "}"
            );


            // OCCUPANCY

            html.append(
                    ".occupancy-number{" +
                    "display:flex;" +
                    "justify-content:space-between;" +
                    "align-items:end;" +
                    "margin-bottom:10px;" +
                    "}"
            );

            html.append(
                    ".percentage{" +
                    "font-size:34px;" +
                    "font-weight:bold;" +
                    "color:#2196f3;" +
                    "}"
            );

            html.append(
                    ".occupancy-text{" +
                    "color:#687587;" +
                    "font-size:14px;" +
                    "}"
            );

            html.append(
                    ".progress{" +
                    "height:14px;" +
                    "background:#e9eef4;" +
                    "border-radius:20px;" +
                    "overflow:hidden;" +
                    "margin:12px 0 25px;" +
                    "}"
            );

            html.append(
                    ".progress-bar{" +
                    "height:100%;" +
                    "width:" +
                    occupancyPercentage +
                    "%;" +
                    "background:#2196f3;" +
                    "border-radius:20px;" +
                    "}"
            );


            // ROOM INFO

            html.append(
                    ".room-info{" +
                    "display:grid;" +
                    "grid-template-columns:1fr 1fr;" +
                    "gap:15px;" +
                    "}"
            );

            html.append(
                    ".room-box{" +
                    "background:#f7f9fc;" +
                    "padding:18px;" +
                    "border-radius:9px;" +
                    "}"
            );

            html.append(
                    ".room-box strong{" +
                    "display:block;" +
                    "font-size:25px;" +
                    "color:#1e3a5f;" +
                    "margin-bottom:4px;" +
                    "}"
            );

            html.append(
                    ".room-box span{" +
                    "font-size:13px;" +
                    "color:#758195;" +
                    "}"
            );


            // QUICK ACTIONS

            html.append(
                    ".actions{" +
                    "display:grid;" +
                    "gap:13px;" +
                    "}"
            );

            html.append(
                    ".action{" +
                    "display:flex;" +
                    "align-items:center;" +
                    "justify-content:space-between;" +
                    "padding:16px 18px;" +
                    "border:1px solid #e4eaf1;" +
                    "border-radius:9px;" +
                    "text-decoration:none;" +
                    "color:#1e3a5f;" +
                    "font-weight:bold;" +
                    "transition:0.2s;" +
                    "}"
            );

            html.append(
                    ".action:hover{" +
                    "background:#f1f7fd;" +
                    "border-color:#2196f3;" +
                    "}"
            );

            html.append(
                    ".action span:last-child{" +
                    "color:#2196f3;" +
                    "font-size:20px;" +
                    "}"
            );


            // PENDING BOX

            html.append(
                    ".pending-box{" +
                    "margin-top:22px;" +
                    "padding:18px;" +
                    "border-radius:9px;" +
                    "background:#fff7e6;" +
                    "border:1px solid #ffe0a3;" +
                    "}"
            );

            html.append(
                    ".pending-box strong{" +
                    "color:#9a6700;" +
                    "font-size:20px;" +
                    "}"
            );

            html.append(
                    ".pending-box p{" +
                    "margin-top:5px;" +
                    "font-size:13px;" +
                    "color:#806b35;" +
                    "}"
            );


            // FOOTER

            html.append(
                    ".footer{" +
                    "text-align:center;" +
                    "margin-top:35px;" +
                    "color:#8a94a6;" +
                    "font-size:13px;" +
                    "}"
            );


            // RESPONSIVE

            html.append(
                    "@media(max-width:1000px){" +
                    ".stats{" +
                    "grid-template-columns:repeat(3,1fr);" +
                    "}" +
                    ".dashboard-grid{" +
                    "grid-template-columns:1fr;" +
                    "}" +
                    "}"
            );

            html.append(
                    "@media(max-width:650px){" +
                    ".header{" +
                    "padding:0 20px;" +
                    "}" +
                    ".admin-label{" +
                    "display:none;" +
                    "}" +
                    ".container{" +
                    "width:94%;" +
                    "}" +
                    ".stats{" +
                    "grid-template-columns:repeat(2,1fr);" +
                    "}" +
                    ".welcome h1{" +
                    "font-size:28px;" +
                    "}" +
                    "}"
            );

            html.append("</style>");

            html.append("</head>");

            html.append("<body>");


            // ==========================================
            // HEADER
            // ==========================================

            html.append(
                    "<div class='header'>");

            html.append(
                    "<div class='logo'>" +
                    "Hostel<span>Hub</span>" +
                    "</div>");

            html.append(
                    "<div class='header-right'>");

            html.append(
                    "<span class='admin-label'>" +
                    "Admin Portal" +
                    "</span>");

            html.append(
                    "<a href='LogoutServlet' " +
                    "class='logout'>" +
                    "Logout" +
                    "</a>");

            html.append("</div>");

            html.append("</div>");


            // ==========================================
            // MAIN
            // ==========================================

            html.append(
                    "<div class='container'>");


            // WELCOME

            html.append(
                    "<div class='welcome'>");

            html.append(
                    "<h1>Admin Dashboard</h1>");

            html.append(
                    "<p>Welcome, " +
                    adminName +
                    ". Manage your hostel operations from here.</p>");

            html.append("</div>");


            // ==========================================
            // STATISTICS
            // ==========================================

            html.append(
                    "<div class='stats'>");


            // STUDENTS

            html.append(
                    "<div class='stat-card'>" +
                    "<div class='stat-title'>" +
                    "TOTAL STUDENTS" +
                    "</div>" +
                    "<div class='stat-value'>" +
                    totalStudents +
                    "</div>" +
                    "<div class='stat-small'>" +
                    "Registered students" +
                    "</div>" +
                    "</div>"
            );


            // ROOMS

            html.append(
                    "<div class='stat-card'>" +
                    "<div class='stat-title'>" +
                    "TOTAL ROOMS" +
                    "</div>" +
                    "<div class='stat-value'>" +
                    totalRooms +
                    "</div>" +
                    "<div class='stat-small'>" +
                    "Hostel rooms" +
                    "</div>" +
                    "</div>"
            );


            // AVAILABLE ROOMS

            html.append(
                    "<div class='stat-card'>" +
                    "<div class='stat-title'>" +
                    "AVAILABLE ROOMS" +
                    "</div>" +
                    "<div class='stat-value'>" +
                    availableRooms +
                    "</div>" +
                    "<div class='stat-small'>" +
                    "Ready for allocation" +
                    "</div>" +
                    "</div>"
            );


            // OCCUPIED ROOMS

            html.append(
                    "<div class='stat-card'>" +
                    "<div class='stat-title'>" +
                    "OCCUPIED ROOMS" +
                    "</div>" +
                    "<div class='stat-value'>" +
                    occupiedRooms +
                    "</div>" +
                    "<div class='stat-small'>" +
                    "Currently in use" +
                    "</div>" +
                    "</div>"
            );


            // PENDING

            html.append(
                    "<a href='AdminRequestsServlet' " +
                    "style='text-decoration:none;color:inherit;'>" +
                    "<div class='stat-card' " +
                    "style='cursor:pointer;'>" +
                    "<div class='stat-title'>" +
                    "PENDING REQUESTS" +
                    "</div>" +
                    "<div class='stat-value'>" +
                    pendingRequests +
                    "</div>" +
                    "<div class='stat-small'>" +
                    "Click to review requests" +
                    "</div>" +
                    "</div>" +
                    "</a>"
            );


            html.append("</div>");


            // ==========================================
            // DASHBOARD GRID
            // ==========================================

            html.append(
                    "<div class='dashboard-grid'>");


            // ==========================================
            // HOSTEL OCCUPANCY
            // ==========================================

            html.append(
                    "<div class='panel'>");

            html.append(
                    "<h2>Hostel Occupancy</h2>");

            html.append(
                    "<p class='panel-subtitle'>" +
                    "Current room utilization" +
                    "</p>");


            html.append(
                    "<div class='occupancy-number'>");

            html.append(
                    "<span class='occupancy-text'>" +
                    occupiedRooms +
                    " of " +
                    totalRooms +
                    " rooms occupied" +
                    "</span>");

            html.append(
                    "<span class='percentage'>" +
                    occupancyPercentage +
                    "%</span>");

            html.append("</div>");


            html.append(
                    "<div class='progress'>");

            html.append(
                    "<div class='progress-bar'></div>");

            html.append("</div>");


            // ROOM INFORMATION

            html.append(
                    "<div class='room-info'>");


            html.append(
                    "<div class='room-box'>" +
                    "<strong>" +
                    availableRooms +
                    "</strong>" +
                    "<span>Available Rooms</span>" +
                    "</div>"
            );


            html.append(
                    "<div class='room-box'>" +
                    "<strong>" +
                    approvedRequests +
                    "</strong>" +
                    "<span>Approved Allocations</span>" +
                    "</div>"
            );


            html.append("</div>");

            html.append("</div>");


            // ==========================================
            // QUICK ACTIONS
            // ==========================================

            html.append(
                    "<div class='panel'>");

            html.append(
                    "<h2>Quick Actions</h2>");

            html.append(
                    "<p class='panel-subtitle'>" +
                    "Common administration tasks" +
                    "</p>");


            html.append(
                    "<div class='actions'>");


            // ALLOCATION REQUESTS

            html.append(
                    "<a class='action' " +
                    "href='AdminRequestsServlet'>" +
                    "<span>Allocation Requests</span>" +
                    "<span>→</span>" +
                    "</a>"
            );


            // HOMEPAGE

            html.append(
                    "<a class='action' " +
                    "href='homepage.html'>" +
                    "<span>Go to Homepage</span>" +
                    "<span>→</span>" +
                    "</a>"
            );


            html.append("</div>");


            // ==========================================
            // PENDING NOTICE
            // ==========================================

            if (pendingRequests > 0) {

                html.append(
                        "<div class='pending-box'>");

                html.append(
                        "<strong>" +
                        pendingRequests +
                        " Pending Request");

                if (pendingRequests != 1) {
                    html.append("s");
                }

                html.append(
                        "</strong>");

                html.append(
                        "<p>" +
                        "Please review the pending allocation " +
                        "requests." +
                        "</p>");

                html.append("</div>");
            }


            html.append("</div>");

            html.append("</div>");

           
            // ==========================================
            // FOOTER
            // ==========================================

            html.append(
                    "<div class='footer'>" +
                    "HostelHub Admin Portal • 2026" +
                    "</div>"
            );


            html.append("</div>");

            html.append("</body>");

            html.append("</html>");


            response.getWriter().print(
                    html.toString()
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

            response.setContentType(
                    "text/html;charset=UTF-8"
            );

            response.getWriter().println(
                    "<h2>Admin Dashboard Error</h2>"
            );

            response.getWriter().println(
                    "<p>" +
                    e.getMessage() +
                    "</p>"
            );
        }
    }
}