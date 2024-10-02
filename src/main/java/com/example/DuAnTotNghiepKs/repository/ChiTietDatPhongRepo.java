package com.example.DuAnTotNghiepKs.repository;

import com.example.DuAnTotNghiepKs.entity.ChiTietDatPhong;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietDatPhongRepo extends JpaRepository<ChiTietDatPhong, Integer> {


    List<ChiTietDatPhong> findByDatPhongIn(List<DatPhong> datPhongs);

    List<ChiTietDatPhong> findByDatPhong_IdDatPhong(Integer idDatPhong);

    public Optional<ChiTietDatPhong> findById(Integer id);


}
