package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ThanhVienDoan")
public class ThanhVienDoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thanh_vien")
    private Integer idThanhVienDoan;
    @Column(name = "ten_thanh_vien")
    private String tenThanhVien;
    @Column(name = "cccd")
    private String cccd;

    @ManyToOne
    @JoinColumn(name = "id_doan")
    private Doan doan;
}
