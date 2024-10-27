package com.example.DuAnTotNghiepKs.DTO;





import lombok.*;

import org.springframework.web.multipart.MultipartFile;



@Builder

@ToString

@Getter

@Setter

@AllArgsConstructor

@NoArgsConstructor

public class PhongDTO {



        private Integer idPhong;

        private String tenPhong;

        private String maPhong;

        private String moTa;

        private Boolean tinhTrang;

        private Boolean trangThai;

        private Float gia;

        private String img;

        private Integer soNguoiToiDa;

//        private float tenDienTich;

//        private String tenTang;

        private Integer idLoaiPhong;

        private String tenLoaiPhong;

        private Float giaLoaiPhong;


        public void setSoNguoiToiDa(Integer soNguoiToiDa) {
        }
}