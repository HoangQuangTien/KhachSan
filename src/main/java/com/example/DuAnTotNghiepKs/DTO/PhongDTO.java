package com.example.DuAnTotNghiepKs.DTO;


import lombok.*;

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

        private Integer idLoaiPhong;
        private String tenLoaiPhong;
        private Float giaLoaiPhong;

}
