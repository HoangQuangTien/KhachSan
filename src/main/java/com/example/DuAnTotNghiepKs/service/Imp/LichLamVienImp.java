package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.LichLamViecDTO;
import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.entity.LichLamViec;
import com.example.DuAnTotNghiepKs.repository.LichLamViecRepo;
import com.example.DuAnTotNghiepKs.service.LichLamViecService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LichLamVienImp implements LichLamViecService {

    @Autowired
    private LichLamViecRepo lichLamViecRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<LichLamViecDTO> getAll() {
        List<LichLamViec> lichLamViecs = lichLamViecRepo.findAll();
        return lichLamViecs.stream()
                .map(lichLamViec -> {
                    LichLamViecDTO dto = modelMapper.map(lichLamViec, LichLamViecDTO.class);

                    // Kiểm tra dữ liệu
                    System.out.println("Ca làm việc: " + lichLamViec.getCaLamViec());

                    dto.setHoTen(lichLamViec.getNhanVien().getHoTen());
                    dto.setTrangThai(lichLamViec.getNhanVien().getTrangThai());
                    if (lichLamViec.getCaLamViec() != null) {
                        dto.setTenCaLamViec(lichLamViec.getCaLamViec().getTenCaLamViec());
                    } else {
                        dto.setTenCaLamViec("Chưa gán ca");
                    }

                    dto.setNgay(lichLamViec.getNgay());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public LichLamViecDTO save(LichLamViecDTO lichLamViecDTO){
        LichLamViec lichLamViec = modelMapper.map(lichLamViecDTO,LichLamViec.class);
        lichLamViec = lichLamViecRepo.save(lichLamViec);
        return modelMapper.map(lichLamViec, LichLamViecDTO.class);
    }

    @Override
    public LichLamViecDTO findById(Integer idLichLamViec) {
        LichLamViec lichLamViec = lichLamViecRepo.findById(idLichLamViec)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch với id:"+idLichLamViec));

        return modelMapper.map(lichLamViec, LichLamViecDTO.class);
    }





    @Override
    public LichLamViecDTO findTopByOrderByIdLichLamViecDesc() {
        LichLamViec lichLamViec = lichLamViecRepo.findTopByOrderByIdLichLamViecDesc();

        if (lichLamViec == null) {
            System.out.println("Không tìm thấy bản ghi nào trong bảng LichLamViec");
            return null;
        }

        System.out.println(lichLamViec); // Log để kiểm tra
        return modelMapper.map(lichLamViec, LichLamViecDTO.class);
    }



}
