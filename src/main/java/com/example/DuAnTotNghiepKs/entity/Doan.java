package com.example.DuAnTotNghiepKs.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Doan")
public class Doan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idDoan;
    @Column(name = "groupName")
    private String tenDoan;
    @Column(name = "so_luong")
    private Integer soLuong;
    @Column(name = "ghi_chu")
    private String ghiChu;

    @ManyToOne
    @JoinColumn(name = "idDatPhong")
    private DatPhong datPhong;

    @OneToMany(mappedBy = "doan")
    private Set<ThanhVienDoan> thanhVienDoans ;
}
