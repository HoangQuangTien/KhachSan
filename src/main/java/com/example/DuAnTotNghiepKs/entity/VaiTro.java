package com.example.DuAnTotNghiepKs.entity;

import com.example.DuAnTotNghiepKs.DTO.ChiTietVaiTroDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "VaiTro")
public class VaiTro {
    @Id
    @Column(name = "id_vai_tro")
    private Integer idVaiTro;

    @Column(name = "ma_vai_tro")
    private String maVaiTro;

    @Column(name = "ten_vai_tro")
    private String tenVaiTro;

    @OneToMany(mappedBy = "vaiTro",fetch = FetchType.EAGER)
    private Set<ChiTietVaiTro> chiTietVaiTros;



}
