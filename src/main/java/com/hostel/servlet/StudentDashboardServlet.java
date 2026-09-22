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

@WebServlet("/StudentDashboardServlet")
public class StudentDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);

        // =====================================================
        // CHECK LOGIN
        // =====================================================

        if (session == null ||
                session.getAttribute("studentId") == null) {

            response.sendRedirect("login.html");
            return;
        }

        String studentId =
                (String) session.getAttribute("studentId");

        Connection con = null;

        try {

            con = DatabaseConnection.getConnection();

            if (con == null) {

                response.getWriter().println(
                    "<h2>Database Connection Failed</h2>"
                );

                return;
            }

            // =====================================================
            // GET STUDENT DETAILS
            // =====================================================

            String studentSql =
                    "SELECT name, email, phone, branch, year "
                  + "FROM students "
                  + "WHERE student_id = ?";

            PreparedStatement studentPs =
                    con.prepareStatement(studentSql);

            studentPs.setString(1, studentId);

            ResultSet studentRs =
                    studentPs.executeQuery();

            String name = "";
            String email = "";
            String phone = "";
            String branch = "";
            int year = 0;

            if (studentRs.next()) {

                name =
                    studentRs.getString("name");

                email =
                    studentRs.getString("email");

                phone =
                    studentRs.getString("phone");

                branch =
                    studentRs.getString("branch");

                year =
                    studentRs.getInt("year");
            }

            studentRs.close();
            studentPs.close();

            // =====================================================
            // GET LATEST ALLOCATION STATUS
            // =====================================================

            String latestSql =
                    "SELECT status "
                  + "FROM allocations "
                  + "WHERE student_id = ? "
                  + "ORDER BY allocation_id DESC "
                  + "LIMIT 1";

            PreparedStatement latestPs =
                    con.prepareStatement(latestSql);

            latestPs.setString(1, studentId);

            ResultSet latestRs =
                    latestPs.executeQuery();

            String latestStatus = "Not Applied";

            if (latestRs.next()) {

                latestStatus =
                    latestRs.getString("status");
            }

            latestRs.close();
            latestPs.close();

            // =====================================================
            // HTML PAGE
            // =====================================================

            PrintWriter out =
                    response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html>");

            out.println("<head>");

            out.println("<meta charset='UTF-8'>");

            out.println(
                "<meta name='viewport' " +
                "content='width=device-width, initial-scale=1.0'>"
            );

            out.println(
                "<title>Student Dashboard | HostelHub</title>"
            );

            out.println("<style>");

            out.println(
                "body {" +
                "font-family: Arial, sans-serif;" +
                "background: #f4f6f8;" +
                "margin: 0;" +
                "padding: 0;" +
                "}"
            );

            out.println(
                ".container {" +
                "width: 500px;" +
                "margin: 50px auto;" +
                "background: white;" +
                "padding: 30px;" +
                "border-radius: 10px;" +
                "box-shadow: 0 0 10px #ccc;" +
                "}"
            );

            out.println(
                "h2 {" +
                "text-align: center;" +
                "color: #333;" +
                "}"
            );

            out.println(
                ".info {" +
                "margin-top: 20px;" +
                "line-height: 1.8;" +
                "}"
            );

            out.println(
                ".status {" +
                "padding: 10px;" +
                "margin-top: 20px;" +
                "border-radius: 5px;" +
                "background: #f1f1f1;" +
                "}"
            );

            out.println(
                ".pending {" +
                "color: #856404;" +
                "background: #fff3cd;" +
                "padding: 10px;" +
                "border-radius: 5px;" +
                "}"
            );

            out.println(
                ".approved {" +
                "color: #155724;" +
                "background: #d4edda;" +
                "padding: 10px;" +
                "border-radius: 5px;" +
                "}"
            );

            out.println(
                ".rejected {" +
                "color: #721c24;" +
                "background: #f8d7da;" +
                "padding: 10px;" +
                "border-radius: 5px;" +
                "}"
            );

            out.println(
                "a {" +
                "display: inline-block;" +
                "margin-top: 15px;" +
                "margin-right: 10px;" +
                "padding: 10px 15px;" +
                "background: #007bff;" +
                "color: white;" +
                "text-decoration: none;" +
                "border-radius: 5px;" +
                "}"
            );

            out.println(
                ".logout {" +
                "background: #dc3545;" +
                "}"
            );

            out.println("</style>");

            out.println("</head>");

            out.println("<body>");

            out.println("<div class='container'>");

            out.println(
                "<h2>Student Dashboard</h2>"
            );

            // =====================================================
            // STUDENT INFORMATION
            // =====================================================

            out.println("<div class='info'>");

            out.println(
                "<b>Student ID:</b> "
                + studentId
                + "<br>"
            );

            out.println(
                "<b>Name:</b> "
                + name
                + "<br>"
            );

            out.println(
                "<b>Email:</b> "
                + email
                + "<br>"
            );

            out.println(
                "<b>Phone:</b> "
                + phone
                + "<br>"
            );

            out.println(
                "<b>Branch:</b> "
                + branch
                + "<br>"
            );

            out.println(
                "<b>Year:</b> "
                + year
                + "<br>"
            );

            out.println("</div>");

            // =====================================================
            // ALLOCATION STATUS
            // =====================================================

            out.println("<div class='status'>");

            out.println(
                "<b>Allocation Status:</b>"
            );

            if (latestStatus.equalsIgnoreCase("Pending")) {

                out.println(
                    "<div class='pending'>" +
                    "Your room allocation request is " +
                    "<b>Pending</b> and waiting for admin approval." +
                    "</div>"
                );

            } else if (
                    latestStatus.equalsIgnoreCase("Approved")) {

                out.println(
                    "<div class='approved'>" +
                    "Your room allocation request has been " +
                    "<b>Approved</b>." +
                    "</div>"
                );

            } else if (
                    latestStatus.equalsIgnoreCase("Rejected")) {

                out.println(
                    "<div class='rejected'>" +
                    "Your previous room allocation request was " +
                    "<b>Rejected</b>." +
                    "</div>"
                );

            } else {

                out.println(
                    "<p>Not Allocated</p>"
                );
            }

            out.println("</div>");

            // =====================================================
            // BUTTONS BASED ON STATUS
            // =====================================================

            if (latestStatus.equalsIgnoreCase("Pending")) {

                // -----------------------------------------------
                // PENDING
                // -----------------------------------------------

                out.println(
                    "<p style='margin-top:20px;'>" +
                    "<b>Room Already Requested</b>" +
                    "<br>" +
                    "You cannot submit another room request " +
                    "while your current request is Pending." +
                    "</p>"
                );

            } else if (
                    latestStatus.equalsIgnoreCase("Approved")) {

                // -----------------------------------------------
                // APPROVED
                // -----------------------------------------------

                out.println(
                    "<a href='MyAllocationServlet'>" +
                    "My Allocation" +
                    "</a>"
                );

            } else {

                // -----------------------------------------------
                // REJECTED OR NOT APPLIED
                // -----------------------------------------------

                out.println(
                    "<a href='allocation.html'>" +
                    "Apply for Room" +
                    "</a>"
                );
            }

            // =====================================================
            // LOGOUT
            // =====================================================

            out.println(
                "<a href='LogoutServlet' " +
                "class='logout'>" +
                "Logout" +
                "</a>"
            );

            out.println("</div>");

            out.println("</body>");

            out.println("</html>");

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html");

            PrintWriter out =
                    response.getWriter();

            out.println(
                "<h2>Dashboard Error</h2>"
            );

            out.println(
                "<p><b>Error:</b> "
                + e.getMessage()
                + "</p>"
            );
        }
    }
}