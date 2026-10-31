package com.example.airlinemanagementsystem.dao;

import com.example.airlinemanagementsystem.utils.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CouponManagementDAO {

    public List<String[]> getAllCoupons() {
        List<String[]> coupons = new ArrayList<>();
        String sql = "SELECT * FROM coupons ORDER BY coupon_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                coupons.add(new String[]{
                        rs.getString("coupon_id"),
                        rs.getString("code"),
                        rs.getString("discount_percent") + "%",
                        rs.getString("expiry_date"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coupons;
    }

    public boolean addCoupon(String code, int discount, LocalDate expiry, String status) {
        String checkSql = "SELECT coupon_id FROM coupons WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, code);
            if (ps.executeQuery().next()) return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO coupons (code, discount_percent, expiry_date, status) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.toUpperCase());
            ps.setInt(2, discount);
            ps.setDate(3, Date.valueOf(expiry));
            ps.setString(4, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCoupon(int couponId, int discount,
                                LocalDate expiry, String status) {
        String sql = "UPDATE coupons SET discount_percent=?, expiry_date=?, status=? " +
                "WHERE coupon_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, discount);
            ps.setDate(2, Date.valueOf(expiry));
            ps.setString(3, status);
            ps.setInt(4, couponId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCoupon(int couponId) {
        String sql = "DELETE FROM coupons WHERE coupon_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, couponId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}