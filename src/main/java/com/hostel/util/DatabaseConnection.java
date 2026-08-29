package com.hostel.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {

        try {

            String databaseUrl = System.getenv("DATABASE_URL");

            if (databaseUrl == null || databaseUrl.isEmpty()) {
                System.out.println("DATABASE_URL is not set!");
                return null;
            }

            // Convert Render PostgreSQL URL to JDBC URL
            if (databaseUrl.startsWith("postgres://")) {
                databaseUrl = databaseUrl.replaceFirst(
                        "postgres://",
                        "jdbc:postgresql://"
                );
            } 
            else if (databaseUrl.startsWith("postgresql://")) {
                databaseUrl = databaseUrl.replaceFirst(
                        "postgresql://",
                        "jdbc:postgresql://"
                );
            }

            // Fix invalid/missing Render port
            databaseUrl = databaseUrl.replace(":-1/", ":5432/");

            // If there is no port at all, add PostgreSQL default port
            int slashIndex = databaseUrl.indexOf("/", "jdbc:postgresql://".length());

            if (slashIndex > 0) {

                String hostPart = databaseUrl.substring(
                        "jdbc:postgresql://".length(),
                        slashIndex
                );

                if (!hostPart.contains(":")) {

                    databaseUrl =
                            databaseUrl.substring(
                                    0,
                                    "jdbc:postgresql://".length()
                            )
                            + hostPart
                            + ":5432"
                            + databaseUrl.substring(slashIndex);
                }
            }

            System.out.println("JDBC URL: " +
                    databaseUrl.replaceAll(":[^:@/]+@", ":****@"));

            Class.forName("org.postgresql.Driver");

            Connection con = DriverManager.getConnection(databaseUrl);

            System.out.println("PostgreSQL connected successfully!");

            return con;

        } catch (Exception e) {

            System.out.println("DATABASE ERROR:");
            e.printStackTrace();

            return null;
        }
    }
}