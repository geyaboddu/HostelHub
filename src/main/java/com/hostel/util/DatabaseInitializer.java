package com.hostel.util;

import java.sql.Connection;
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

            // Add username if missing
            try {
                stmt.executeUpdate(
                    "ALTER TABLE admins " +
                    "ADD COLUMN IF NOT EXISTS username VARCHAR(50)"
                );
            } catch (Exception e) {
                // Ignore
            }

            // Add name if missing
            try {
                stmt.executeUpdate(
                    "ALTER TABLE admins " +
                    "ADD COLUMN IF NOT EXISTS name VARCHAR(100)"
                );
            } catch (Exception e) {
                // Ignore
            }

            // Make sure admin account exists
            try {
                stmt.executeUpdate(
                    "INSERT INTO admins " +
                    "(admin_id, username, name, password) " +
                    "VALUES " +
                    "('ADMIN001', 'ADMIN001', 'Hostel Administrator', 'admin123') " +
                    "ON CONFLICT (username) DO UPDATE SET " +
                    "admin_id = EXCLUDED.admin_id, " +
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
                    "room_type VARCHAR(100), " +
                    "capacity INT NOT NULL, " +
                    "occupied INT DEFAULT 0, " +
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
                    "ADD COLUMN IF NOT EXISTS status VARCHAR(20) " +
                    "DEFAULT 'Available'"
                );
            } catch (Exception e) {
                // Ignore
            }


            // =====================================================
            // 3. INSERT / UPDATE HOSTEL ROOMS
            // =====================================================

            String[] rooms = {

                "INSERT INTO rooms " +
                "(room_number, block, room_type, capacity, occupied, status) " +
                "VALUES ('101', 'A Block', '2 Sharing', 2, 2, 'Full') " +
                "ON CONFLICT (room_number) DO UPDATE SET " +
                "block = EXCLUDED.block, " +
                "room_type = EXCLUDED.room_type, " +
                "capacity = EXCLUDED.capacity, " +
                "occupied = EXCLUDED.occupied, " +
                "status = EXCLUDED.status",

                "INSERT INTO rooms " +
                "(room_number, block, room_type, capacity, occupied, status) " +
                "VALUES ('102', 'A Block', '2 Sharing', 2, 2, 'Full') " +
                "ON CONFLICT (room_number) DO UPDATE SET " +
                "block = EXCLUDED.block, " +
                "room_type = EXCLUDED.room_type, " +
                "capacity = EXCLUDED.capacity, " +
                "occupied = EXCLUDED.occupied, " +
                "status = EXCLUDED.status",

                "INSERT INTO rooms " +
                "(room_number, block, room_type, capacity, occupied, status) " +
                "VALUES ('103', 'A Block', '3 Sharing', 3, 0, 'Available') " +
                "ON CONFLICT (room_number) DO UPDATE SET " +
                "block = EXCLUDED.block, " +
                "room_type = EXCLUDED.room_type, " +
                "capacity = EXCLUDED.capacity, " +
                "occupied = EXCLUDED.occupied, " +
                "status = EXCLUDED.status",

                "INSERT INTO rooms " +
                "(room_number, block, room_type, capacity, occupied, status) " +
                "VALUES ('201', 'B Block', '4 Sharing', 4, 0, 'Available') " +
                "ON CONFLICT (room_number) DO UPDATE SET " +
                "block = EXCLUDED.block, " +
                "room_type = EXCLUDED.room_type, " +
                "capacity = EXCLUDED.capacity, " +
                "occupied = EXCLUDED.occupied, " +
                "status = EXCLUDED.status",

                "INSERT INTO rooms " +
                "(room_number, block, room_type, capacity, occupied, status) " +
                "VALUES ('202', 'B Block', '5 Sharing', 5, 0, 'Available') " +
                "ON CONFLICT (room_number) DO UPDATE SET " +
                "block = EXCLUDED.block, " +
                "room_type = EXCLUDED.room_type, " +
                "capacity = EXCLUDED.capacity, " +
                "occupied = EXCLUDED.occupied, " +
                "status = EXCLUDED.status"
            };

            for (String room : rooms) {
                try {
                    stmt.executeUpdate(room);
                } catch (Exception e) {
                    System.out.println("Room setup error: "
                            + e.getMessage());
                }
            }

            System.out.println("Rooms table initialized successfully!");

            stmt.close();
            con.close();

        } catch (Exception e) {

            System.out.println("DATABASE INITIALIZATION ERROR:");
            e.printStackTrace();
        }
    }
}