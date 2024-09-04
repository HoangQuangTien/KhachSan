package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "LichLamViec")
public class LichLamViec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lich_lam_viec")
    private Integer idLichLamViec;

    @NotBlank(message = "Mã lịch làm việc không được để trống")
    @Column(name = "ma_lich_lam_viec")
    private String maLichLamViec;


    @ManyToOne
    @JoinColumn(name = "id_nhan_vien",referencedColumnName = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "ma_ca_lam_viec",referencedColumnName = "ma_ca_lam_viec")
    private CaLamViec caLamViec;

    @NotNull(message = "Ngày không được để đấy")
    @Future(message = "Ngày làm không được nhỏ hơn ngày hiện tại")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay")
    private Date ngay;



}
