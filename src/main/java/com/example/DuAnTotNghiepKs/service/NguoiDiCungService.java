package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.NguoiDiCungDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.NguoiDiCung;
import com.example.DuAnTotNghiepKs.repository.NguoiDiCungRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NguoiDiCungService {

    @Autowired
    private NguoiDiCungRepo nguoiDiCungRepo;
    @Autowired
    private DatPhongService datPhongService;

    public NguoiDiCungDTO findById(Integer id) {
        NguoiDiCung nguoiDiCung = nguoiDiCungRepo.findById(id).orElse(null);
        return convertToDTO(nguoiDiCung);
    }

    private NguoiDiCungDTO convertToDTO(NguoiDiCung entity) {
        if (entity == null) {
            return null;
        }
        NguoiDiCungDTO dto = new NguoiDiCungDTO();
//         dto.setDatPhong(entity.getId());
        dto.setTenNguoiDiCung(entity.getTenNguoiDiCung());
        dto.setSoCmnd(entity.getSoCmnd());

        return dto;
    }

    @Transactional
    public void save(NguoiDiCung nguoiDiCung) {
        nguoiDiCungRepo.save(nguoiDiCung);
    }
//    public void updateNguoiDiCung(Integer id, NguoiDiCungDTO nguoiDiCungDTO) {
//        NguoiDiCung existingNguoiDiCung = nguoiDiCungRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("Người đi cùng không tồn tại."));
//
//        // Cập nhật các trường cần thiết
//        existingNguoiDiCung.setTenNguoiDiCung(nguoiDiCungDTO.getHoVaTen());
//        existingNguoiDiCung.setSoCmnd(nguoiDiCungDTO.getSoCmnd());
//        // Bạn có thể cập nhật thêm các trường khác nếu cần
//
//        // Lưu lại thay đổi
//        nguoiDiCungRepo.save(existingNguoiDiCung);
//    }

    public boolean existsById(Integer id) {
        return nguoiDiCungRepo.existsById(id);
    }
    public int countByDatPhongId(Integer datPhongId) {
        return nguoiDiCungRepo.countByDatPhongId(datPhongId);
    }
    public List<NguoiDiCungDTO> findByDatPhongId(Integer datPhongId) {
        List<NguoiDiCung> nguoiDiCungs = nguoiDiCungRepo.findByDatPhong_IdDatPhong(datPhongId);
        return nguoiDiCungs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


}
