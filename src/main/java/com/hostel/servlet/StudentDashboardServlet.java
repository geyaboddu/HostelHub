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

@WebServlet("/StudentDashboardServlet")
public class StudentDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
            session.getAttribute("studentId") == null) {

            response.sendRedirect("login.html");
            return;
        }

        String studentId =
                (String) session.getAttribute("studentId");

        try {

            Connection con =
                    DatabaseConnection.getConnection();

            String sql =
                    "SELECT name, email, phone, branch, year "
                  + "FROM students "
                  + "WHERE student_id = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, studentId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {

                response.getWriter().println(
                    "<h2>Student details not found.</h2>"
                );

                rs.close();
                ps.close();
                con.close();

                return;
            }

            String name = rs.getString("name");
            String email = rs.getString("email");
            String phone = rs.getString("phone");
            String branch = rs.getString("branch");
            int year = rs.getInt("year");

            // Update session with latest details
            session.setAttribute("studentName", name);
            session.setAttribute("studentEmail", email);

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
                "<title>Student Dashboard | HostelHub</title>"
            );

            response.getWriter().println(
                "<style>" +

                "*{box-sizing:border-box;" +
                "font-family:Arial,sans-serif;}" +

                "body{margin:0;" +
                "background:#f4f8fc;}" +

                ".navbar{" +
                "background:#1e3a5f;" +
                "color:white;" +
                "padding:18px 40px;" +
                "display:flex;" +
                "justify-content:space-between;" +
                "align-items:center;" +
                "}" +

                ".logo{" +
                "font-size:26px;" +
                "font-weight:bold;" +
                "}" +

                ".logo span{color:#2196f3;}" +

                ".logout{" +
                "background:#e53935;" +
                "color:white;" +
                "padding:9px 18px;" +
                "text-decoration:none;" +
                "border-radius:6px;" +
                "}" +

                ".container{" +
                "width:90%;" +
                "max-width:1100px;" +
                "margin:40px auto;" +
                "}" +

                "h1{color:#1e3a5f;}" +

                ".subtitle{" +
                "color:#666;" +
                "margin-bottom:30px;" +
                "}" +

                ".cards{" +
                "display:flex;" +
                "gap:25px;" +
                "flex-wrap:wrap;" +
                "}" +

                ".card{" +
                "background:white;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "box-shadow:0 5px 18px rgba(0,0,0,0.10);" +
                "flex:1;" +
                "min-width:300px;" +
                "}" +

                ".card h2{" +
                "color:#1e3a5f;" +
                "margin-bottom:20px;" +
                "}" +

                ".card p{" +
                "color:#555;" +
                "margin:14px 0;" +
                "}" +

                ".btn{" +
                "display:block;" +
                "padding:13px;" +
                "margin-top:15px;" +
                "background:#2196f3;" +
                "color:white;" +
                "text-decoration:none;" +
                "text-align:center;" +
                "border-radius:6px;" +
                "font-weight:bold;" +
                "}" +

                ".btn:hover{" +
                "background:#1769aa;" +
                "}" +

                "</style>"
            );

            response.getWriter().println(
                "</head><body>"
            );

            // Navbar
            response.getWriter().println(
                "<div class='navbar'>" +
                "<div class='logo'>Hostel<span>Hub</span></div>" +
                "<a href='LogoutServlet' class='logout'>Logout</a>" +
                "</div>"
            );

            response.getWriter().println(
                "<div class='container'>"
            );

            response.getWriter().println(
                "<h1>Student Dashboard</h1>"
            );

            response.getWriter().println(
                "<p class='subtitle'>Welcome, " +
                name +
                "</p>"
            );

            response.getWriter().println(
                "<div class='cards'>"
            );

            // Student information
            response.getWriter().println(
                "<div class='card'>" +
                "<h2>Student Information</h2>" +
                "<p><strong>Student ID:</strong> " +
                studentId + "</p>" +
                "<p><strong>Name:</strong> " +
                name + "</p>" +
                "<p><strong>Email:</strong> " +
                email + "</p>" +
                "<p><strong>Phone:</strong> " +
                phone + "</p>" +
                "<p><strong>Branch:</strong> " +
                branch + "</p>" +
                "<p><strong>Year:</strong> " +
                year + "</p>" +
                "</div>"
            );

            // Hostel actions
            response.getWriter().println(
                "<div class='card'>" +
                "<h2>Hostel Services</h2>" +

                "<a class='btn' href='allocation.html'>" +
                "Apply for Room" +
                "</a>" +

                "<a class='btn' " +
                "href='MyAllocationServlet?studentId=" +
                studentId + "'>" +
                "My Allocation" +
                "</a>" +

                "</div>"
            );

            response.getWriter().println(
                "</div>"
            );

            response.getWriter().println(
                "</div>"
            );

            response.getWriter().println(
                "</body></html>"
            );

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            response.getWriter().println(
                "<h2>Dashboard Error</h2>"
            );

            response.getWriter().println(
                "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}