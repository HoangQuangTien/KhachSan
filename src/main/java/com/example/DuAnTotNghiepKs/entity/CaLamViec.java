package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CaLamViec")
public class CaLamViec {
    @Id
    @NotBlank(message = "Mã ca không được để trống")
    @Column(name = "ma_ca_lam_viec")
    private String maCaLamViec;

    @NotBlank(message = "Tên ca không được để trống")
    @Column(name = "ten_ca_lam_viec")
    private String tenCaLamViec;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "gio_bat_dau")
    private Date gioBatDau;

    @NotNull(message = "Giờ kết thúc không được để trống")
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "gio_ket_thuc")
    private Date gioKetThuc;

    @OneToMany(mappedBy = "caLamViec")
    private Set<LichLamViec> lichLamViecs;
}

