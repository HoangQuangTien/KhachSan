package com.example.DuAnTotNghiepKs.entity;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PhuPhi")
public class PhuPhi {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phu_phi")
    private Integer idPhuPhi;

    @Column(name = "ten_phu_phi")
    private String tenPhuPhi;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    @Column(name = "gia_phu_phi")
    private Float giaPhuPhi;

    @ManyToOne
    @JoinColumn(name = "id_dat_phong",referencedColumnName = "id_dat_phong")
    private DatPhong datPhong;

}