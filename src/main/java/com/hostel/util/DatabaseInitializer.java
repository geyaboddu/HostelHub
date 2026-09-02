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

            String createTable =
                    "CREATE TABLE IF NOT EXISTS admins (" +
                    "admin_id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL" +
                    ")";

            stmt.executeUpdate(createTable);

            String insertAdmin =
                    "INSERT INTO admins (username, password) " +
                    "VALUES ('ADMIN001', 'admin123') " +
                    "ON CONFLICT (username) DO NOTHING";

            stmt.executeUpdate(insertAdmin);

            System.out.println("Admins table initialized successfully!");

            stmt.close();
            con.close();

        } catch (Exception e) {
            System.out.println("ADMIN TABLE ERROR:");
            e.printStackTrace();
        }
    }
}