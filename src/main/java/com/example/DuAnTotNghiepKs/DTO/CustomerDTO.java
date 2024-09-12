package com.example.DuAnTotNghiepKs.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerDTO {

    @NotEmpty(message = "Tên khách hàng không được để trống")
    @Size(max = 20, message = "Tên khách hàng không được vượt quá 20 ký tự")
    private String tenKhachHang;

    @Pattern(regexp = "\\d{10}", message = "Số điện thoại phải có 10 chữ số")
    private String soDienThoai;

    @Email(message = "Địa chỉ email không hợp lệ")
    @Size(max = 50, message = "Địa chỉ email không được vượt quá 50 ký tự")
    private String email;

    // Getters and setters
    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
