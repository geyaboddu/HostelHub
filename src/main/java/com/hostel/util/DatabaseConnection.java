package com.hostel.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {

        try {

            Class.forName("org.postgresql.Driver");

            // =====================================================
            // RENDER DATABASE
            // =====================================================

            String host = System.getenv("PGHOST");
            String port = System.getenv("PGPORT");
            String database = System.getenv("PGDATABASE");
            String username = System.getenv("PGUSER");
            String password = System.getenv("PGPASSWORD");

            String url;

            // If Render PostgreSQL environment variables exist
            if (host != null &&
                port != null &&
                database != null &&
                username != null &&
                password != null) {

                url =
                    "jdbc:postgresql://" +
                    host + ":" + port +
                    "/" + database +
                    "?sslmode=require";

                System.out.println(
                    "Connecting to Render PostgreSQL..."
                );

            } else {

                // =================================================
                // LOCAL POSTGRESQL
                // =================================================

                host = "localhost";
                port = "5432";
                database = "hostel_db";
                username = "postgres";

                // Put your existing LOCAL PostgreSQL password here
                password = "MyFirstProject";

                url =
                    "jdbc:postgresql://" +
                    host + ":" + port +
                    "/" + database;

                System.out.println(
                    "Connecting to local PostgreSQL..."
                );
            }

            Connection con =
                DriverManager.getConnection(
                    url,
                    username,
                    password
                );

            System.out.println(
                "PostgreSQL connected successfully!"
            );

            return con;

        } catch (Exception e) {

            System.out.println(
                "DATABASE ERROR:"
            );

            e.printStackTrace();

            return null;
        }
    }
}