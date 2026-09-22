package com.hostel.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        try {
            Connection con = DatabaseConnection.getConnection();

            if (con == null) {
                System.out.println("Database connection failed.");
                return;
            }

            Statement stmt = con.createStatement();

            // =====================================================
            // 1. ADMINS TABLE
            // =====================================================

            String createAdmins =
                    "CREATE TABLE IF NOT EXISTS admins (" +
                    "admin_id VARCHAR(50) PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "password VARCHAR(100) NOT NULL" +
                    ")";

            stmt.executeUpdate(createAdmins);

            try {
                stmt.executeUpdate(
                    "ALTER TABLE admins " +
                    "ADD COLUMN IF NOT EXISTS username VARCHAR(50)"
                );
            } catch (Exception e) {
                // Ignore
            }

            try {
                stmt.executeUpdate(
                    "ALTER TABLE admins " +
                    "ADD COLUMN IF NOT EXISTS name VARCHAR(100)"
                );
            } catch (Exception e) {
                // Ignore
            }

            try {
                stmt.executeUpdate(
                    "INSERT INTO admins " +
                    "(admin_id, username, name, password) " +
                    "VALUES " +
                    "('ADMIN001', 'ADMIN001', 'Hostel Administrator', 'admin123') " +
                    "ON CONFLICT (admin_id) DO UPDATE SET " +
                    "username = EXCLUDED.username, " +
                    "name = EXCLUDED.name, " +
                    "password = EXCLUDED.password"
                );
            } catch (Exception e) {
                System.out.println("Admin account setup skipped: "
                        + e.getMessage());
            }

            System.out.println("Admins table initialized successfully!");


            // =====================================================
            // 2. ROOMS TABLE
            // =====================================================

            String createRooms =
                    "CREATE TABLE IF NOT EXISTS rooms (" +
                    "room_id SERIAL PRIMARY KEY, " +
                    "room_number VARCHAR(20) UNIQUE NOT NULL, " +
                    "block VARCHAR(100), " +
                    "floor VARCHAR(50), " +
                    "room_type VARCHAR(100), " +
                    "capacity INT NOT NULL, " +
                    "occupied INT DEFAULT 0, " +
                    "ac BOOLEAN DEFAULT FALSE, " +
                    "status VARCHAR(20) DEFAULT 'Available'" +
                    ")";

            stmt.executeUpdate(createRooms);


            // Add missing columns to existing rooms table

            try {
                stmt.executeUpdate(
                    "ALTER TABLE rooms " +
                    "ADD COLUMN IF NOT EXISTS room_number VARCHAR(20)"
                );
            } catch (Exception e) {
                // Ignore
            }

            try {
                stmt.executeUpdate(
                    "ALTER TABLE rooms " +
                    "ADD COLUMN IF NOT EXISTS block VARCHAR(100)"
                );
            } catch (Exception e) {
                // Ignore
            }

            try {
                stmt.executeUpdate(
                    "ALTER TABLE rooms " +
                    "ADD COLUMN IF NOT EXISTS floor VARCHAR(50)"
                );
            } catch (Exception e) {
                // Ignore
            }

            try {
                stmt.executeUpdate(
                    "ALTER TABLE rooms " +
                    "ADD COLUMN IF NOT EXISTS room_type VARCHAR(100)"
                );
            } catch (Exception e) {
                // Ignore
            }

            try {
                stmt.executeUpdate(
                    "ALTER TABLE rooms " +
                    "ADD COLUMN IF NOT EXISTS capacity INT"
                );
            } catch (Exception e) {
                // Ignore
            }

            try {
                stmt.executeUpdate(
                    "ALTER TABLE rooms " +
                    "ADD COLUMN IF NOT EXISTS occupied INT DEFAULT 0"
                );
            } catch (Exception e) {
                // Ignore
            }

            try {
                stmt.executeUpdate(
                    "ALTER TABLE rooms " +
                    "ADD COLUMN IF NOT EXISTS ac BOOLEAN DEFAULT FALSE"
                );
            } catch (Exception e) {
                // Ignore
            }

            try {
                stmt.executeUpdate(
                    "ALTER TABLE rooms " +
                    "ADD COLUMN IF NOT EXISTS status VARCHAR(20) " +
                    "DEFAULT 'Available'"
                );
            } catch (Exception e) {
                // Ignore
            }


            // =====================================================
            // 3. HOSTEL ROOM DATA
            // =====================================================

            String[][] roomData = {

                // -------------------------------------------------
                // GROUND FLOOR
                // -------------------------------------------------

                {"G01", "Ground Floor", "2 Sharing", "2", "false"},
                {"G02", "Ground Floor", "2 Sharing", "2", "false"},
                {"G03", "Ground Floor", "2 Sharing", "2", "false"},

                {"G04", "Ground Floor", "4 Sharing", "4", "false"},
                {"G05", "Ground Floor", "4 Sharing", "4", "false"},
                {"G06", "Ground Floor", "4 Sharing", "4", "false"},
                {"G07", "Ground Floor", "4 Sharing", "4", "false"},

                {"G08", "Ground Floor", "5 Sharing", "5", "false"},
                {"G09", "Ground Floor", "5 Sharing", "5", "false"},
                {"G10", "Ground Floor", "5 Sharing", "5", "false"},


                // -------------------------------------------------
                // 1ST FLOOR
                // -------------------------------------------------

                {"101", "1st Floor", "2 Sharing", "2", "false"},
                {"102", "1st Floor", "2 Sharing", "2", "false"},
                {"103", "1st Floor", "2 Sharing", "2", "false"},

                {"104", "1st Floor", "4 Sharing", "4", "false"},
                {"105", "1st Floor", "4 Sharing", "4", "false"},
                {"106", "1st Floor", "4 Sharing", "4", "false"},
                {"107", "1st Floor", "4 Sharing", "4", "false"},

                {"108", "1st Floor", "5 Sharing", "5", "false"},
                {"109", "1st Floor", "5 Sharing", "5", "false"},
                {"110", "1st Floor", "5 Sharing", "5", "false"},


                // -------------------------------------------------
                // 2ND FLOOR - ALL AC
                // -------------------------------------------------

                {"201", "2nd Floor", "2 Sharing", "2", "true"},
                {"202", "2nd Floor", "2 Sharing", "2", "true"},
                {"203", "2nd Floor", "2 Sharing", "2", "true"},

                {"204", "2nd Floor", "4 Sharing", "4", "true"},
                {"205", "2nd Floor", "4 Sharing", "4", "true"},
                {"206", "2nd Floor", "4 Sharing", "4", "true"},
                {"207", "2nd Floor", "4 Sharing", "4", "true"},

                {"208", "2nd Floor", "5 Sharing", "5", "true"},
                {"209", "2nd Floor", "5 Sharing", "5", "true"},
                {"210", "2nd Floor", "5 Sharing", "5", "true"}
            };


            // =====================================================
            // 4. INSERT / UPDATE ALL 30 ROOMS
            // =====================================================

            String roomSql =
                    "INSERT INTO rooms " +
                    "(room_number, block, floor, room_type, capacity, " +
                    "occupied, ac, status) " +
                    "VALUES (?, ?, ?, ?, ?, 0, ?, 'Available') " +
                    "ON CONFLICT (room_number) DO UPDATE SET " +
                    "block = EXCLUDED.block, " +
                    "floor = EXCLUDED.floor, " +
                    "room_type = EXCLUDED.room_type, " +
                    "capacity = EXCLUDED.capacity, " +     
                    "ac = EXCLUDED.ac";

            PreparedStatement ps = con.prepareStatement(roomSql);

            for (String[] room : roomData) {

                String roomNumber = room[0];
                String floor = room[1];
                String roomType = room[2];
                int capacity = Integer.parseInt(room[3]);
                boolean ac = Boolean.parseBoolean(room[4]);

                

                String block = "A Block";

                ps.setString(1, roomNumber);
                ps.setString(2, block);
                ps.setString(3, floor);
                ps.setString(4, roomType);
                ps.setInt(5, capacity);
                ps.setBoolean(6, ac);

                ps.executeUpdate();
            }

            ps.close();

            System.out.println(
                "30 hostel rooms initialized successfully!"
            );
         // =====================================================
         // 5. STUDENTS TABLE
         // =====================================================

         String createStudents =
                 "CREATE TABLE IF NOT EXISTS students (" +
                 "student_id VARCHAR(50) PRIMARY KEY, " +
                 "name VARCHAR(100) NOT NULL, " +
                 "email VARCHAR(100) UNIQUE NOT NULL, " +
                 "phone VARCHAR(20), " +
                 "branch VARCHAR(100), " +
                 "year INT, " +
                 "password VARCHAR(100) NOT NULL" +
                 ")";

         stmt.executeUpdate(createStudents);
         stmt.executeUpdate(
        		    "ALTER TABLE students " +
        		    "ADD COLUMN IF NOT EXISTS phone VARCHAR(20)"
        		);

        		stmt.executeUpdate(
        		    "ALTER TABLE students " +
        		    "ADD COLUMN IF NOT EXISTS branch VARCHAR(100)"
        		);

        		stmt.executeUpdate(
        		    "ALTER TABLE students " +
        		    "ADD COLUMN IF NOT EXISTS year INT"
        		);
         System.out.println("Students table initialized successfully!");
      // =====================================================
      // 6. ALLOCATIONS TABLE
      // =====================================================

      String createAllocations =
              "CREATE TABLE IF NOT EXISTS allocations (" +
              "allocation_id SERIAL PRIMARY KEY, " +
              "student_id VARCHAR(50) NOT NULL, " +
              "room_id INT NOT NULL, " +
              "bed_number INT NOT NULL, " +
              "reason VARCHAR(500), " +
              "status VARCHAR(20) DEFAULT 'Pending'" +
              ")";

      stmt.executeUpdate(createAllocations);

      System.out.println("Allocations table initialized successfully!");
            // =====================================================
            // CLOSE CONNECTION
            // =====================================================

            stmt.close();
            con.close();

        } catch (Exception e) {

            System.out.println("DATABASE INITIALIZATION ERROR:");
            e.printStackTrace();
        }
    }
}