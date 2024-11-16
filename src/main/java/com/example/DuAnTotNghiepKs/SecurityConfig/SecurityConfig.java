package com.example.DuAnTotNghiepKs.SecurityConfig;

import com.example.DuAnTotNghiepKs.DTO.ChiTietVaiTroDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = { "/api","/register", "/login","khach-hang","view-dat-phong","/thanh-toan-khach-hang","/dat-phong","/vnpay",
            "/assets1/**","vnpay_pay","vnpay_result","vnpay-create","https://sandbox.vnpayment.vn/paymentv2/vpcpay.html",
            "/load-phong/**","/img/**","/css/**","/js/**","lien-he","/searchh","/about","/listPhong","/phong-theo-loai","/register","/api/available-rooms"
            ,"/khach-hang/login"};
    private final String[] ADMIN_ENDPOINTS= {"thongke"};
    private final String[] EMPLOYEE_ENDPOINTS= {"quan-ly-khach-hang/**"};

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//               registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:8080") // Địa chỉ frontend
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedHeaders("*")
//                        .allowCredentials(true);
//            }
//        };
//    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TaiKhoanService taiKhoanService) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                                .requestMatchers(EMPLOYEE_ENDPOINTS).hasAnyRole("ADMIN","EMPLOYEE")
//                        .requestMatchers("/dat-phong").hasAnyRole("CUSTOMER")
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?error=true")
                        .successHandler( customAuthenticationSuccessHandler())
                        .usernameParameter("tenDangNhap")
                        .passwordParameter("matKhau")
                )
                .logout(logoff -> logoff
                        .logoutUrl("/logoff")
                        .logoutSuccessUrl("/khach-hang")
                        .permitAll()
                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                )
                .userDetailsService(userDetailsService(taiKhoanService)); // Đảm bảo sử dụng service vừa implement

        return http.build();
    }

//    @Bean
//    public AuthenticationSuccessHandler customCombinedAuthenticationSuccessHandler() {
//        return new AuthenticationSuccessHandler() {
//            private final SavedRequestAwareAuthenticationSuccessHandler defaultHandler =
//                    new SavedRequestAwareAuthenticationSuccessHandler();
//
//            @Override
//            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                                org.springframework.security.core.Authentication authentication)
//                    throws IOException, ServletException {
//                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
//                System.out.println("Vai trò người dùng: " + roles); // Ghi log vai trò
//
//                String redirectUrl = (String) request.getSession().getAttribute("redirectUrl");
//                if (redirectUrl != null) {
//                    System.out.println("Redirect URL: " + redirectUrl);
//                    redirectUrl = URLDecoder.decode(redirectUrl, StandardCharsets.UTF_8);
//                }
//
//                if (request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST") != null) {
//                    defaultHandler.onAuthenticationSuccess(request, response, authentication);
//                } else {
//                    // Lấy thông tin ID phòng từ session hoặc query parameter
//                    Integer roomId = (Integer) request.getSession().getAttribute("roomId"); // Nếu có
//                    if (roomId != null) {
//                        response.sendRedirect("/view-dat-phong?roomId=" + roomId);
//                    } else if (roles.contains("ROLE_ADMIN")) {
//                        response.sendRedirect(redirectUrl != null ? redirectUrl : "/thongke");
//                    } else if (roles.contains("ROLE_EMPLOYEE")) {
//                        response.sendRedirect(redirectUrl != null ? redirectUrl : "/datphongs");
//                    } else {
//                        response.sendRedirect(redirectUrl != null ? redirectUrl : "/khach-hang");
//                    }
//                }
//
//            }
//
//
//        };
//    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // Lấy vai trò của người dùng
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

            // In log để kiểm tra vai trò hiện tại của người dùng
            System.out.println("Các vai trò của người dùng: " + roles);

            // Điều hướng dựa trên vai trò
            if (roles.contains("ROLE_ADMIN")) {
                System.out.println("Chuyển hướng đến /thongke");
                response.sendRedirect("/thongke");
            } else if (roles.contains("ROLE_EMPLOYEE")) {
                System.out.println("Chuyển hướng đến /datphongs");
                response.sendRedirect("/datphongs");
            } else {
                System.out.println("Chuyển hướng mặc định đến /khach-hang");
                response.sendRedirect("/khach-hang");
            }
        };
    }



    @Bean
    public UserDetailsService userDetailsService(TaiKhoanService taiKhoanService) {
        return tenDangNhap -> {
            TaiKhoanDTO taiKhoanDto = taiKhoanService.findByTenDangNhap(tenDangNhap);

            if (taiKhoanDto == null) {
                throw new UsernameNotFoundException("Tài khoản không tồn tại: " + tenDangNhap);
            }

            // Kiểm tra trạng thái của nhân viên và khách hàng
            boolean isNhanVienActive = taiKhoanDto.getNhanVienDTO() != null && Boolean.TRUE.equals(taiKhoanDto.getNhanVienDTO().getTrangThai());
            boolean isKhachHangActive = taiKhoanDto.getKhachHangDTO() != null && Boolean.FALSE.equals(taiKhoanDto.getKhachHangDTO().getDeletedAt());

            System.out.println("isNhanVienActive: " + isNhanVienActive);
            System.out.println("isKhachHangActive: " + isKhachHangActive);

            if (!isNhanVienActive && !isKhachHangActive) {
                throw new DisabledException("Tài khoản đã bị vô hiệu hóa");
            }

            // Lấy danh sách vai trò của tài khoản
            Set<ChiTietVaiTroDTO> roles = taiKhoanDto.getChiTietVaiTros();
            String[] roleNames = roles.stream()
                    .map(chiTietVaiTroDto -> chiTietVaiTroDto.getVaiTroDTO().getTenVaiTro())
                    .toArray(String[]::new);

            // Tạo đối tượng UserDetails, không khóa tài khoản khách hàng
            return User.withUsername(taiKhoanDto.getTenDangNhap())
                    .password(taiKhoanDto.getMatKhau())
                    .roles(roleNames)
                    .accountLocked(false) // Đảm bảo tài khoản không bị khóa đối với khách hàng
                    .build();
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

//    @Bean
//    public SpringTemplateEngine templateEngine() {
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//        templateEngine.addDialect(new SpringSecurityDialect());
//        return templateEngine;
//    }
}
