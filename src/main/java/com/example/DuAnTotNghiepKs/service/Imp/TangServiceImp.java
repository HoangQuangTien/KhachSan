package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.entity.Tang;
import com.example.DuAnTotNghiepKs.repository.TangRepo;
import com.example.DuAnTotNghiepKs.service.TangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TangServiceImp implements TangService {
    @Autowired
    private TangRepo tangRepository;

    @Override
    public Optional<Tang> getTangById(Integer idTang) {
        return tangRepository.findById(idTang);
    }

    @Override
    public List<Tang> getAllTangs() {
        return tangRepository.findAll();
    }



    @Override
    public Tang saveTang(Tang tang) { // Đảm bảo phương thức này được triển khai
        return tangRepository.save(tang);
    }

    // Tìm danh sách tầng theo id loại phòng
    @Override
    public List<Tang> findByLoaiPhongId(Integer idLoaiPhong) {
        return tangRepository.findByLoaiPhong_IdLoaiPhong(idLoaiPhong);
    }

}
