package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.utils.DatabaseConnection;

import java.sql.*;

public class CouponDAO {

    public int getCouponDiscount(String code) {
        String sql = "SELECT discount_percent FROM coupons " +
                "WHERE code = ? AND status = 'Active' " +
                "AND expiry_date >= CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("discount_percent");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // 0 means invalid coupon
    }
}