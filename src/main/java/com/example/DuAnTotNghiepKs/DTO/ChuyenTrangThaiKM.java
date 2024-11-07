package com.example.DuAnTotNghiepKs.DTO;

import com.example.DuAnTotNghiepKs.ChuyenTrangThai.ChuyenTrangThai;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChuyenTrangThaiKM {
    public static void main(String[] args) {
        SpringApplication.run(ChuyenTrangThai.class, args);
    }
}