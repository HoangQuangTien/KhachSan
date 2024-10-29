package com.example.DuAnTotNghiepKs.SecurityConfig;

import com.example.DuAnTotNghiepKs.DTO.ChiTietVaiTroDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Arrays;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = { "/api", "/login","khach-hang","searchh","view-dat-phong","dat-phong",
            "/load-phong","/img/**","/css/**","/js/**","lien-he","hangphongdetail","phong-theo-loai"};
    private final String[] ADMIN_ENDPOINTS= {"/thongke"};
    private final String[] EMPLOYEE_ENDPOINTS = {"/"};


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TaiKhoanService taiKhoanService) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(EMPLOYEE_ENDPOINTS).hasRole("EMPLOYEE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?error=true")
                        .successHandler(customAuthenticationSuccessHandler())
                        .usernameParameter("tenDangNhap")
                        .passwordParameter("matKhau")
                )
                .logout(logoff -> logoff
                        .logoutUrl("/logoff")
                        .logoutSuccessUrl("/khach-hang")
                        .permitAll()
                )
                .userDetailsService(userDetailsService(taiKhoanService)); // Đảm bảo sử dụng service vừa implement

        return http.build();
    }


    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println("Authentication successful!");

            // Lấy các vai trò của người dùng
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            System.out.println("Các vai trò của người dùng: " + roles);

            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

            // Điều hướng dựa trên vai trò (chú ý thêm ROLE_)
            if (roles.contains("ROLE_ADMIN")) { // Chú ý ROLE_ ở đây
                System.out.println("Chuyển hướng đến /thongke");
                redirectStrategy.sendRedirect(request, response, "/thongke");
            } else if (roles.contains("ROLE_EMPLOYEE")) { // Chú ý ROLE_ ở đây
                System.out.println("Chuyển hướng đến /datphongs");
                redirectStrategy.sendRedirect(request, response, "/datphongs");
            } else {
                System.out.println("Chuyển hướng mặc định đến /khach-hang");
                redirectStrategy.sendRedirect(request, response, "/khach-hang");
            }
        };
    }





    @Bean
    public UserDetailsService userDetailsService(TaiKhoanService taiKhoanService) {
        return tenDangNhap -> {
            // Lấy TaiKhoanDTO từ cơ sở dữ liệu dựa trên tên đăng nhập
            TaiKhoanDTO taiKhoanDto = taiKhoanService.findByTenDangNhap(tenDangNhap);

            if (taiKhoanDto == null) {
                throw new UsernameNotFoundException("Tài khoản không tồn tại: " + tenDangNhap);
            }

            // Kiểm tra trạng thái của nhân viên (giả sử là trường `status` trong đối tượng TaiKhoanDTO)
            if (!taiKhoanDto.getNhanVienDTO().getTrangThai()) {
                throw new DisabledException("Tài khoản đã bị vô hiệu hóa");
            }

            Set<ChiTietVaiTroDTO> roles = taiKhoanDto.getChiTietVaiTros();

            // Map từ ChiTietVaiTroDTO sang tên vai trò
            String[] roleNames = roles.stream()
                    .map(chiTietVaiTroDto -> chiTietVaiTroDto.getVaiTroDTO().getTenVaiTro())
                    .toArray(String[]::new);

            System.out.println("Đang tìm tài khoản với tên đăng nhập: " + tenDangNhap);
            System.out.println("Mật khẩu từ cơ sở dữ liệu: " + taiKhoanDto.getMatKhau());
            System.out.println("Đang tìm kiếm vai trò:" + Arrays.toString(roleNames));

            // Tạo đối tượng UserDetails mà không mã hóa mật khẩu
            UserDetails userDetails = User.withUsername(taiKhoanDto.getTenDangNhap())
                    .password(taiKhoanDto.getMatKhau()) // Không mã hóa ở đây
                    .roles(roleNames) // Sử dụng mảng roleNames
                    .accountLocked(!taiKhoanDto.getNhanVienDTO().getTrangThai()) // Khóa tài khoản nếu cần
                    .build();

            return userDetails;
        };
    }






    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        //dùng để mã hóa mật khẩu
        return new BCryptPasswordEncoder(8);
    }
}
