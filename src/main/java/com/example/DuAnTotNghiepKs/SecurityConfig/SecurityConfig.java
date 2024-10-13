package com.example.DuAnTotNghiepKs.SecurityConfig;

import com.example.DuAnTotNghiepKs.DTO.ChiTietVaiTroDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = { "/api", "/login","khach-hang","/img/**","/css/**","/js/**"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TaiKhoanService taiKhoanService) throws Exception {
        http
                .csrf().disable() // Tùy chọn tắt CSRF nếu không cần thiết
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("tenDangNhap")
                        .passwordParameter("matKhau")
                )
                .logout(logoff -> logoff
                        .logoutUrl("/logoff")
                        .logoutSuccessUrl("/khach-hang") // Đường dẫn quay lại sau khi đăng xuất
                        .permitAll()
                )
                .userDetailsService(userDetailsService(taiKhoanService)); // Chỉ định UserDetailsService ở đây

        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService(TaiKhoanService taiKhoanService) {
        return tenDangNhap -> {
            // Lấy TaiKhoanDTO từ cơ sở dữ liệu dựa trên tên đăng nhập
            TaiKhoanDTO taiKhoanDto = taiKhoanService.findByTenDangNhap(tenDangNhap);

            if (taiKhoanDto == null) {
                throw new UsernameNotFoundException("Tài khoản không tồn tại: " + tenDangNhap);
            }

            Set<ChiTietVaiTroDTO> roles = taiKhoanDto.getChiTietVaiTros();

            // Map từ ChiTietVaiTroDTO sang tên vai trò
            String[] roleNames = roles.stream()
                    .map(chiTietVaiTroDto -> chiTietVaiTroDto.getVaiTroDTO().getTenVaiTro())
                    .toArray(String[]::new);
            System.out.println("Đang tìm tài khoản với tên đăng nhập: " + tenDangNhap);
            System.out.println("Mật khẩu từ cơ sở dữ liệu: " + taiKhoanDto.getMatKhau());
            // Tạo đối tượng UserDetails
            UserDetails userDetails = User.withDefaultPasswordEncoder()
                    .username(taiKhoanDto.getTenDangNhap())
                    .password(taiKhoanDto.getMatKhau())
                    .roles(roleNames)  // Sử dụng mảng roleNames
                    .build();


            return userDetails;
        };
    }





    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);


        return new ProviderManager(authenticationProvider);
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        //dùng để mã hóa mật khẩu
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

