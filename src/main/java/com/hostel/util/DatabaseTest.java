package com.hostel.util;

import java.sql.Connection;

public class DatabaseTest {

    public static void main(String[] args) {

        Connection con = DatabaseConnection.getConnection();

        if (con != null) {
            System.out.println("SUCCESS: MySQL connection is working!");
        } else {
            System.out.println("FAILED: Could not connect to MySQL.");
        }
    }
}