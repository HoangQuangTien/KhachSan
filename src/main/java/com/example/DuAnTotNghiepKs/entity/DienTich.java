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
@Table(name = "DienTichPhong")
public class DienTich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dien_tich")
    private int idDienTich;
    @Column(name = "dien_tich")
    private float tenDienTich;
    @Column(name = "mo_ta")
    private String moTa;
    @OneToMany(mappedBy = "dienTich", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Phong> phongs = new HashSet<>();

}
