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

            // Create admins table if it does not exist
            String createTable =
                    "CREATE TABLE IF NOT EXISTS admins (" +
                    "admin_id VARCHAR(50) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "password VARCHAR(100) NOT NULL" +
                    ")";

            stmt.executeUpdate(createTable);

            // Fix the table if it was created using the old structure
            try {
                stmt.executeUpdate(
                    "ALTER TABLE admins ALTER COLUMN admin_id DROP DEFAULT"
                );
            } catch (Exception e) {
                // Ignore if there is no default
            }

            try {
                stmt.executeUpdate(
                    "ALTER TABLE admins " +
                    "ALTER COLUMN admin_id TYPE VARCHAR(50) " +
                    "USING admin_id::VARCHAR"
                );
            } catch (Exception e) {
                // Ignore if already VARCHAR
            }

            // Add name column if it is missing
            stmt.executeUpdate(
                "ALTER TABLE admins " +
                "ADD COLUMN IF NOT EXISTS name VARCHAR(100)"
            );

            // Make sure our admin account exists
            stmt.executeUpdate(
                "INSERT INTO admins (admin_id, name, password) " +
                "VALUES ('ADMIN001', 'Hostel Administrator', 'admin123') " +
                "ON CONFLICT (admin_id) DO UPDATE SET " +
                "name = EXCLUDED.name, " +
                "password = EXCLUDED.password"
            );

            System.out.println("Admins table initialized successfully!");

            stmt.close();
            con.close();

        } catch (Exception e) {
            System.out.println("ADMIN TABLE ERROR:");
            e.printStackTrace();
        }
    }
}