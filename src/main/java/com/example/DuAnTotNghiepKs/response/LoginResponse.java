package com.example.DuAnTotNghiepKs.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String tenDangNhap;
    private String hoTen;
    private String email;
    private Set<String> vaiTro; // Hoặc List<String> tùy theo nhu cầu
//    private String token; // Chỉ định nếu sử dụng JWT
    private String redirectUrl;

}
