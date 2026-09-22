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

@WebServlet("/AdminRequestsServlet")
public class AdminRequestsServlet extends HttpServlet {

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

            if (con == null) {
                throw new Exception("Database connection failed.");
            }

            // ==============================
            // PENDING REQUESTS
            // ==============================

            String sql =
                    "SELECT a.allocation_id, " +
                    "a.student_id, " +
                    "a.room_id, " +
                    "a.bed_number, " +
                    "a.reason, " +
                    "a.status, " +
                    "COALESCE(s.name, 'Student') AS student_name, " +
                    "COALESCE(r.room_number, 'Room ' || a.room_id) AS room_number " +
                    "FROM allocations a " +
                    "LEFT JOIN students s " +
                    "ON a.student_id = s.student_id " +
                    "LEFT JOIN rooms r " +
                    "ON a.room_id = r.room_id " +
                    "WHERE LOWER(TRIM(a.status)) = 'pending' " +
                    "ORDER BY a.allocation_id DESC";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();


            // ==============================
            // BUILD HTML
            // ==============================

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
                    "<title>Allocation Requests | HostelHub</title>");


            // ==============================
            // CSS
            // ==============================

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
                    "gap:20px;" +
                    "}"
            );

            html.append(
                    ".portal{" +
                    "font-size:14px;" +
                    "color:#dce7f3;" +
                    "}"
            );

            html.append(
                    ".logout{" +
                    "background:#e53935;" +
                    "color:white;" +
                    "text-decoration:none;" +
                    "padding:10px 18px;" +
                    "border-radius:6px;" +
                    "font-weight:bold;" +
                    "}"
            );

            html.append(
                    ".logout:hover{" +
                    "background:#c62828;" +
                    "}"
            );


            // CONTAINER

            html.append(
                    ".container{" +
                    "width:92%;" +
                    "max-width:1200px;" +
                    "margin:35px auto 50px;" +
                    "}"
            );


            // PAGE TITLE

            html.append(
                    ".page-top{" +
                    "display:flex;" +
                    "justify-content:space-between;" +
                    "align-items:center;" +
                    "margin-bottom:25px;" +
                    "}"
            );

            html.append(
                    ".page-title h1{" +
                    "color:#1e3a5f;" +
                    "font-size:32px;" +
                    "margin-bottom:7px;" +
                    "}"
            );

            html.append(
                    ".page-title p{" +
                    "color:#758195;" +
                    "font-size:15px;" +
                    "}"
            );


            // BACK BUTTON

            html.append(
                    ".back-btn{" +
                    "text-decoration:none;" +
                    "background:white;" +
                    "color:#1e3a5f;" +
                    "border:1px solid #d8e0e8;" +
                    "padding:10px 17px;" +
                    "border-radius:7px;" +
                    "font-weight:bold;" +
                    "}"
            );

            html.append(
                    ".back-btn:hover{" +
                    "background:#eef5fc;" +
                    "}"
            );


            // SUMMARY

            html.append(
                    ".summary{" +
                    "background:white;" +
                    "border-radius:12px;" +
                    "padding:22px 25px;" +
                    "display:flex;" +
                    "align-items:center;" +
                    "gap:18px;" +
                    "box-shadow:" +
                    "0 5px 18px rgba(30,58,95,0.08);" +
                    "border:1px solid #edf1f5;" +
                    "margin-bottom:22px;" +
                    "}"
            );

            html.append(
                    ".summary-icon{" +
                    "width:48px;" +
                    "height:48px;" +
                    "border-radius:10px;" +
                    "background:#fff4d8;" +
                    "display:flex;" +
                    "align-items:center;" +
                    "justify-content:center;" +
                    "font-size:24px;" +
                    "}"
            );

            html.append(
                    ".summary h3{" +
                    "color:#1e3a5f;" +
                    "font-size:18px;" +
                    "margin-bottom:4px;" +
                    "}"
            );

            html.append(
                    ".summary p{" +
                    "color:#7a8797;" +
                    "font-size:13px;" +
                    "}"
            );


            // TABLE PANEL

            html.append(
                    ".table-panel{" +
                    "background:white;" +
                    "border-radius:12px;" +
                    "box-shadow:" +
                    "0 5px 18px rgba(30,58,95,0.08);" +
                    "border:1px solid #edf1f5;" +
                    "overflow:hidden;" +
                    "}"
            );

            html.append(
                    ".table-wrapper{" +
                    "overflow-x:auto;" +
                    "}"
            );

            html.append(
                    "table{" +
                    "width:100%;" +
                    "border-collapse:collapse;" +
                    "}"
            );

            html.append(
                    "th{" +
                    "background:#f7f9fc;" +
                    "color:#667386;" +
                    "font-size:12px;" +
                    "text-transform:uppercase;" +
                    "letter-spacing:.4px;" +
                    "padding:16px 14px;" +
                    "text-align:left;" +
                    "border-bottom:1px solid #e8edf3;" +
                    "}"
            );

            html.append(
                    "td{" +
                    "padding:17px 14px;" +
                    "border-bottom:1px solid #edf1f5;" +
                    "color:#465466;" +
                    "font-size:14px;" +
                    "vertical-align:middle;" +
                    "}"
            );

            html.append(
                    "tr:hover{" +
                    "background:#fafcff;" +
                    "}"
            );


            // STUDENT

            html.append(
                    ".student-name{" +
                    "font-weight:bold;" +
                    "color:#1e3a5f;" +
                    "margin-bottom:4px;" +
                    "}"
            );

            html.append(
                    ".student-id{" +
                    "font-size:12px;" +
                    "color:#8793a3;" +
                    "}"
            );


            // ROOM

            html.append(
                    ".room{" +
                    "font-weight:bold;" +
                    "color:#1e3a5f;" +
                    "}"
            );

            html.append(
                    ".bed{" +
                    "font-size:12px;" +
                    "color:#8793a3;" +
                    "margin-top:4px;" +
                    "}"
            );


            // REASON

            html.append(
                    ".reason{" +
                    "max-width:230px;" +
                    "line-height:1.45;" +
                    "color:#596779;" +
                    "}"
            );


            // STATUS

            html.append(
                    ".status{" +
                    "display:inline-block;" +
                    "padding:6px 11px;" +
                    "border-radius:20px;" +
                    "background:#fff4d8;" +
                    "color:#9a6700;" +
                    "font-size:12px;" +
                    "font-weight:bold;" +
                    "}"
            );


            // ACTIONS

            html.append(
                    ".actions{" +
                    "display:flex;" +
                    "gap:8px;" +
                    "align-items:center;" +
                    "}"
            );

            html.append(
                    ".approve," +
                    ".reject{" +
                    "border:none;" +
                    "padding:8px 13px;" +
                    "border-radius:6px;" +
                    "font-weight:bold;" +
                    "font-size:12px;" +
                    "cursor:pointer;" +
                    "}"
            );

            html.append(
                    ".approve{" +
                    "background:#2e9d59;" +
                    "color:white;" +
                    "}"
            );

            html.append(
                    ".approve:hover{" +
                    "background:#237d45;" +
                    "}"
            );

            html.append(
                    ".reject{" +
                    "background:#e53935;" +
                    "color:white;" +
                    "}"
            );

            html.append(
                    ".reject:hover{" +
                    "background:#c62828;" +
                    "}"
            );


            // EMPTY STATE

            html.append(
                    ".empty{" +
                    "text-align:center;" +
                    "padding:55px 20px;" +
                    "}"
            );

            html.append(
                    ".empty-icon{" +
                    "font-size:45px;" +
                    "margin-bottom:15px;" +
                    "}"
            );

            html.append(
                    ".empty h2{" +
                    "color:#1e3a5f;" +
                    "margin-bottom:8px;" +
                    "}"
            );

            html.append(
                    ".empty p{" +
                    "color:#7b8794;" +
                    "font-size:14px;" +
                    "}"
            );


            // FOOTER

            html.append(
                    ".footer{" +
                    "text-align:center;" +
                    "margin-top:30px;" +
                    "color:#8a94a6;" +
                    "font-size:13px;" +
                    "}"
            );


            // RESPONSIVE

            html.append(
                    "@media(max-width:700px){" +
                    ".header{" +
                    "padding:0 20px;" +
                    "}" +
                    ".portal{" +
                    "display:none;" +
                    "}" +
                    ".container{" +
                    "width:94%;" +
                    "}" +
                    ".page-top{" +
                    "align-items:flex-start;" +
                    "gap:15px;" +
                    "}" +
                    ".page-title h1{" +
                    "font-size:26px;" +
                    "}" +
                    ".back-btn{" +
                    "white-space:nowrap;" +
                    "}" +
                    "}"
            );

            html.append("</style>");

            html.append("</head>");

            html.append("<body>");


            // ==============================
            // HEADER
            // ==============================

            html.append(
                    "<div class='header'>");

            html.append(
                    "<div class='logo'>" +
                    "Hostel<span>Hub</span>" +
                    "</div>");

            html.append(
                    "<div class='header-right'>");

            html.append(
                    "<span class='portal'>" +
                    "Admin Portal" +
                    "</span>");

            html.append(
                    "<a href='LogoutServlet' " +
                    "class='logout'>Logout</a>");

            html.append("</div>");

            html.append("</div>");


            // ==============================
            // MAIN
            // ==============================

            html.append(
                    "<div class='container'>");


            html.append(
                    "<div class='page-top'>");

            html.append(
                    "<div class='page-title'>" +
                    "<h1>Allocation Requests</h1>" +
                    "<p>Review and manage student room allocation requests.</p>" +
                    "</div>");

            html.append(
                    "<a href='AdminDashboardServlet' " +
                    "class='back-btn'>" +
                    "← Dashboard" +
                    "</a>");

            html.append("</div>");

            String message = request.getParameter("message");

            if ("approved".equals(message)) {

                html.append(
                        "<div style='background:#e8f5e9;" +
                        "color:#2e7d32;" +
                        "border:1px solid #a5d6a7;" +
                        "padding:14px 18px;" +
                        "border-radius:8px;" +
                        "margin-bottom:18px;" +
                        "font-weight:bold;'>" +
                        "✓ Allocation request approved successfully." +
                        "</div>"
                );

            } else if ("rejected".equals(message)) {

                html.append(
                        "<div style='background:#ffebee;" +
                        "color:#c62828;" +
                        "border:1px solid #ef9a9a;" +
                        "padding:14px 18px;" +
                        "border-radius:8px;" +
                        "margin-bottom:18px;" +
                        "font-weight:bold;'>" +
                        "✓ Allocation request rejected successfully." +
                        "</div>"
                );
            }
            // ==============================
            // REQUEST COUNT
            // ==============================

            html.append(
                    "<div class='summary'>");

            html.append(
                    "<div class='summary-icon'>⏳</div>");

            html.append(
                    "<div>");

            html.append(
                    "<h3>Pending Allocation Requests</h3>");

            html.append(
                    "<p>Requests below are waiting for admin review.</p>");

            html.append("</div>");

            html.append("</div>");


            // ==============================
            // TABLE
            // ==============================

            html.append(
                    "<div class='table-panel'>");

            html.append(
                    "<div class='table-wrapper'>");

            html.append("<table>");

            html.append("<thead>");

            html.append("<tr>");

            html.append("<th>Student</th>");

            html.append("<th>Room</th>");

            html.append("<th>Reason</th>");

            html.append("<th>Status</th>");

            html.append("<th>Action</th>");

            html.append("</tr>");

            html.append("</thead>");

            html.append("<tbody>");


            boolean found = false;

            int pendingCount = 0;


            // ==============================
            // REQUEST ROWS
            // ==============================

            while (rs.next()) {

                found = true;
                pendingCount++;

                int allocationId =
                        rs.getInt("allocation_id");

                String studentId =
                        rs.getString("student_id");

                String studentName =
                        rs.getString("student_name");

                String roomNumber =
                        rs.getString("room_number");

                int bedNumber =
                        rs.getInt("bed_number");

                String reason =
                        rs.getString("reason");

                String status =
                        rs.getString("status");


                if (studentName == null ||
                        studentName.trim().isEmpty()) {

                    studentName = "Student";
                }

                if (reason == null ||
                        reason.trim().isEmpty()) {

                    reason = "No reason provided";
                }


                html.append("<tr>");


                // STUDENT

                html.append("<td>");

                html.append(
                        "<div class='student-name'>" +
                        escapeHtml(studentName) +
                        "</div>");

                html.append(
                        "<div class='student-id'>" +
                        "ID: " +
                        escapeHtml(studentId) +
                        "</div>");

                html.append("</td>");


                // ROOM

                html.append("<td>");

                html.append(
                        "<div class='room'>" +
                        escapeHtml(roomNumber) +
                        "</div>");

                html.append(
                        "<div class='bed'>" +
                        "Bed " +
                        bedNumber +
                        "</div>");

                html.append("</td>");


                // REASON

                html.append("<td>");

                html.append(
                        "<div class='reason'>" +
                        escapeHtml(reason) +
                        "</div>");

                html.append("</td>");


                // STATUS

                html.append("<td>");

                html.append(
                        "<span class='status'>" +
                        escapeHtml(status) +
                        "</span>");

                html.append("</td>");


                // ACTIONS

                html.append("<td>");

                html.append(
                        "<div class='actions'>");


                html.append(
                        "<form action='ApproveServlet' " +
                        "method='post' " +
                        "style='display:inline;'>");

                html.append(
                        "<input type='hidden' " +
                        "name='allocationId' " +
                        "value='" +
                        allocationId +
                        "'>");

                html.append(
                        "<button type='submit' " +
                        "class='approve' " +
                        "onclick=\"return confirm('Are you sure you want to approve this request?');\">" +
                        "Approve" +
                        "</button>");

                html.append("</form>");


                html.append(
                        "<form action='RejectServlet' " +
                        "method='post' " +
                        "style='display:inline;'>");

                html.append(
                        "<input type='hidden' " +
                        "name='allocationId' " +
                        "value='" +
                        allocationId +
                        "'>");
                html.append(
                        "<button type='submit' " +
                        "class='reject' " +
                        "onclick=\"return confirm('Are you sure you want to reject this request?');\">" +
                        "Reject" +
                        "</button>");

                html.append("</form>");


                html.append("</div>");

                html.append("</td>");

                html.append("</tr>");
            }


            // ==============================
            // EMPTY STATE
            // ==============================

            if (!found) {

                html.append(
                        "<tr><td colspan='5'>");

                html.append(
                        "<div class='empty'>");

                html.append(
                        "<div class='empty-icon'>✓</div>");

                html.append(
                        "<h2>No Pending Requests</h2>");

                html.append(
                        "<p>" +
                        "There are currently no allocation " +
                        "requests waiting for approval." +
                        "</p>");

                html.append("</div>");

                html.append("</td></tr>");
            }


            html.append("</tbody>");

            html.append("</table>");

            html.append("</div>");

            html.append("</div>");


            // FOOTER

            html.append(
                    "<div class='footer'>" +
                    "HostelHub Admin Portal • " +
                    pendingCount +
                    " pending request");

            if (pendingCount != 1) {
                html.append("s");
            }

            html.append("</div>");


            html.append("</div>");

            html.append("</body>");

            html.append("</html>");


            response.getWriter().print(
                    html.toString()
            );


            rs.close();
            ps.close();
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
                    "<h2>Unable to load allocation requests</h2>"
            );

            response.getWriter().println(
                    "<p>Error: " +
                    escapeHtml(e.getMessage()) +
                    "</p>"
            );
        }
    }


    // ==============================
    // HTML ESCAPE
    // ==============================

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}