package com.example.DuAnTotNghiepKs.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "ThamSo")
public class ThamSo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_tham_so", nullable = false)
    private String tenThamSo;

    @Column(name = "gia_tri", nullable = false)
    private String giaTri;

    @Column(name = "mo_ta")
    private String moTa;
}
