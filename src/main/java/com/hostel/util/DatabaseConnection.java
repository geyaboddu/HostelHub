package com.hostel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.net.URI;

public class DatabaseConnection {

    public static Connection getConnection() {

        try {
            String databaseUrl = System.getenv("DATABASE_URL");

            if (databaseUrl == null || databaseUrl.isEmpty()) {
                System.out.println("DATABASE_URL is not set!");
                return null;
            }

            // Convert Render PostgreSQL URL to JDBC format
            if (databaseUrl.startsWith("postgres://")) {
                databaseUrl = databaseUrl.replaceFirst(
                        "postgres://",
                        "postgresql://"
                );
            }

            URI uri = new URI(databaseUrl);

            String userInfo = uri.getUserInfo();
            String username = userInfo.substring(0, userInfo.indexOf(":"));
            String password = userInfo.substring(userInfo.indexOf(":") + 1);

            String host = uri.getHost();
            int port = uri.getPort();

            String database = uri.getPath().substring(1);

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port
                    + "/" + database
                    + "?sslmode=require";

            Class.forName("org.postgresql.Driver");

            Connection con = DriverManager.getConnection(
                    jdbcUrl,
                    username,
                    password
            );

            System.out.println("PostgreSQL connected successfully!");

            return con;

        } catch (Exception e) {

            System.out.println("DATABASE ERROR:");
            e.printStackTrace();

            return null;
        }
    }
}