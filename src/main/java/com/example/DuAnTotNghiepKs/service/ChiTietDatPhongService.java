package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.ChiTietDatPhong;
import com.example.DuAnTotNghiepKs.repository.ChiTietDatPhongRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChiTietDatPhongService {

    @Autowired
    private ChiTietDatPhongRepo chiTietDatPhongRepo;

    @Transactional
    public void saveChiTietDatPhong(ChiTietDatPhong chiTietDatPhong) {
        chiTietDatPhongRepo.save(chiTietDatPhong);
    }

//    public ChiTietDatPhong findById(Integer id) {
//        Optional<ChiTietDatPhong> optionalChiTietDatPhong = chiTietDatPhongRepo.findById(id);
//        return optionalChiTietDatPhong.orElse(null);
//    }

    public ChiTietDatPhong findById(Integer id) {
        return chiTietDatPhongRepo.findById(id).orElse(null);
    }

}
