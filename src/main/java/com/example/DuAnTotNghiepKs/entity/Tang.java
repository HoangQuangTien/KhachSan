package com.example.DuAnTotNghiepKs.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "Tang")
public class Tang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Tang")
    private int idTang;
    @Column(name = "ten_tang")
    private String tenTang;
    @Column(name = "mo_ta")
    private String moTa;

    @OneToOne(mappedBy = "tang", cascade = CascadeType.ALL, orphanRemoval = true)
    private LoaiPhong loaiPhong;


    @Override
    public String toString() {
        return "Tang{" +
                "idTang=" + idTang +
                ", tenTang='" + tenTang + '\'' +
                ", moTa='" + moTa + '\'' +
                '}';
    }
}
