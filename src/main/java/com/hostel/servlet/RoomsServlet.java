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

@WebServlet("/RoomsServlet")
public class RoomsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            con = DatabaseConnection.getConnection();

            if (con == null) {
                response.setContentType("text/html");
                response.getWriter().println(
                    "<h2>Database connection failed.</h2>"
                );
                return;
            }

            String sql =
                    "SELECT room_id, room_number, block, floor, room_type, "
                  + "capacity, occupied, ac, status "
                  + "FROM rooms "
                  + "ORDER BY CASE "
                  + "WHEN room_number LIKE 'G%' THEN 1 "
                  + "WHEN room_number::integer BETWEEN 101 AND 199 THEN 2 "
                  + "WHEN room_number::integer BETWEEN 201 AND 299 THEN 3 "
                  + "ELSE 4 "
                  + "END, "
                  + "CASE "
                  + "WHEN room_type = '2 Sharing' THEN 1 "
                  + "WHEN room_type = '4 Sharing' THEN 2 "
                  + "WHEN room_type = '5 Sharing' THEN 3 "
                  + "END, "
                  + "room_number";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            StringBuilder roomsData = new StringBuilder();

            while (rs.next()) {

                int roomId = rs.getInt("room_id");
                String roomNumber = rs.getString("room_number");
                String block = rs.getString("block");
                String floor = rs.getString("floor");
                String roomType = rs.getString("room_type");
                int capacity = rs.getInt("capacity");
                int occupied = rs.getInt("occupied");
                boolean ac = rs.getBoolean("ac");
                String status = rs.getString("status");

                int availableBeds = capacity - occupied;

                roomsData.append("<tr>");

                // Room Number
                roomsData.append("<td>")
                         .append(roomNumber)
                         .append("</td>");

                // Block
                roomsData.append("<td>")
                         .append(block)
                         .append("</td>");

                // Floor
                roomsData.append("<td>")
                         .append(floor)
                         .append("</td>");

                // Room Type
                roomsData.append("<td>")
                         .append(roomType)
                         .append("</td>");

                // AC
                roomsData.append("<td>");

                if (ac) {
                    roomsData.append("AC");
                } else {
                    roomsData.append("Non-AC");
                }

                roomsData.append("</td>");

                // Capacity
                roomsData.append("<td>")
                         .append(capacity)
                         .append("</td>");

                // Available Beds
                roomsData.append("<td>")
                         .append(availableBeds)
                         .append("</td>");

                // Status and Action
                if ("Available".equalsIgnoreCase(status)
                        && availableBeds > 0) {

                    roomsData.append("<td class='available'>")
                             .append("Available")
                             .append("</td>");

                    roomsData.append("<td>");

                    roomsData.append(
                        "<a class='apply-btn' href='allocation.html?"
                    );

                    roomsData.append("roomId=")
                             .append(roomId);

                    roomsData.append("&roomNumber=")
                             .append(roomNumber);

                    roomsData.append("&capacity=")
                             .append(capacity);

                    roomsData.append("&occupied=")
                             .append(occupied);

                    roomsData.append("'>Apply</a>");

                    roomsData.append("</td>");

                } else {

                    roomsData.append("<td class='full'>")
                             .append("Full")
                             .append("</td>");

                    roomsData.append("<td>");

                    roomsData.append(
                        "<span class='disabled-btn'>Full</span>"
                    );

                    roomsData.append("</td>");
                }

                roomsData.append("</tr>");
            }

            request.setAttribute(
                "roomsData",
                roomsData.toString()
            );

            request.getRequestDispatcher("rooms.jsp")
                   .forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.setContentType("text/html");

            response.getWriter().println(
                "<h2>Error loading rooms</h2>"
            );

            response.getWriter().println(
                "<p>" + e.getMessage() + "</p>"
            );

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }

            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
        }
    }
}