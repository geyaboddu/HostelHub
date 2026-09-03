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

        try {

            Connection con = DatabaseConnection.getConnection();

            String sql = "SELECT room_id, room_number, block, room_type, "
                       + "capacity, occupied, status "
                       + "FROM rooms ORDER BY room_id";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            StringBuilder roomsData = new StringBuilder();

            while (rs.next()) {

                int roomId = rs.getInt("room_id");
                String roomNumber = rs.getString("room_number");
                String block = rs.getString("block");
                String roomType = rs.getString("room_type");
                int capacity = rs.getInt("capacity");
                int occupied = rs.getInt("occupied");
                String status = rs.getString("status");

                int availableBeds = capacity - occupied;

                roomsData.append("<tr>");

                roomsData.append("<td>")
                         .append(roomNumber)
                         .append("</td>");

                roomsData.append("<td>")
                         .append(block)
                         .append("</td>");

                // Floor
                if (roomNumber.startsWith("1")) {
                    roomsData.append("<td>1</td>");
                } else {
                    roomsData.append("<td>2</td>");
                }

                roomsData.append("<td>")
                         .append(roomType)
                         .append("</td>");

                roomsData.append("<td>")
                         .append(capacity)
                         .append("</td>");

                roomsData.append("<td>")
                         .append(availableBeds)
                         .append("</td>");

                if ("Available".equalsIgnoreCase(status)) {

                    roomsData.append("<td class='available'>")
                             .append("Available")
                             .append("</td>");

                    roomsData.append("<td>");

                    roomsData.append("<a class='apply-btn' href='allocation.html?")
                             .append("roomId=").append(roomId)
                             .append("&roomNumber=").append(roomNumber)
                             .append("&capacity=").append(capacity)
                             .append("&occupied=").append(occupied)
                             .append("'>Apply</a>");

                    roomsData.append("</td>");

                } else {

                    roomsData.append("<td class='full'>")
                             .append("Full")
                             .append("</td>");

                    roomsData.append("<td>")
                             .append("<span class='disabled-btn'>Full</span>")
                             .append("</td>");
                }

                roomsData.append("</tr>");
            }

            request.setAttribute("roomsData", roomsData.toString());

            rs.close();
            ps.close();
            con.close();

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
        }
    }
}