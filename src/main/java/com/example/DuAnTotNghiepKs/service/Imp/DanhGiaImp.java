package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.DanhGiaDTO;
import com.example.DuAnTotNghiepKs.entity.DanhGia;
import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.repository.DanhGiaRepo;
import com.example.DuAnTotNghiepKs.repository.PhongRepo;
import com.example.DuAnTotNghiepKs.service.DanhGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DanhGiaImp implements DanhGiaService {

    @Autowired
    private DanhGiaRepo danhGiaRepo;

    @Autowired
    private PhongRepo phongRepo;

    @Override
    public List<DanhGiaDTO> getDanhGiaByPhong(Integer idPhong) {
        List<DanhGia> danhGiaList = danhGiaRepo.findByPhong_IdPhong(idPhong);
        return danhGiaList.stream()
                .map(danhGia -> new DanhGiaDTO(
                        danhGia.getIdDanhGia(),
                        danhGia.getPhong().getIdPhong(),
                        danhGia.getTenKhachHang(),
                        danhGia.getSoSao(),
                        danhGia.getNoiDung(),
                        danhGia.getNgayDanhGia(),
                        danhGia.getPhong().getTenPhong()
                ))
                .collect(Collectors.toList());
    }


    @Override
    public DanhGiaDTO addDanhGia(DanhGiaDTO danhGiaDTO) {


        // Chuyển DTO sang entity
        DanhGia danhGia = new DanhGia();
        danhGia.setTenKhachHang(danhGiaDTO.getTenKhachHang());
        danhGia.setSoSao(danhGiaDTO.getSoSao());
        danhGia.setNoiDung(danhGiaDTO.getNoiDung());
        danhGia.setNgayDanhGia(LocalDateTime.now().now());

        // Tìm phòng để gắn vào đánh giá
        Optional<Phong> phong = phongRepo.findById(danhGiaDTO.getIdPhong());
        if (phong.isPresent()) {
            danhGia.setPhong(phong.get());
        } else {
            throw new RuntimeException("Không tìm thấy phòng với ID: " + danhGiaDTO.getIdPhong());
        }

        // Lưu vào cơ sở dữ liệu
        DanhGia savedDanhGia = danhGiaRepo.save(danhGia);

        // Chuyển entity đã lưu về DTO
        return new DanhGiaDTO(
                savedDanhGia.getIdDanhGia(),
                savedDanhGia.getPhong().getIdPhong(),
                savedDanhGia.getTenKhachHang(),
                savedDanhGia.getSoSao(),
                savedDanhGia.getNoiDung(),
                savedDanhGia.getNgayDanhGia(),
                savedDanhGia.getPhong().getTenPhong()
        );
    }


    @Override
    // Lấy tất cả đánh giá
    public List<DanhGiaDTO> getAllDanhGia() {
        List<DanhGia> danhGiaList = danhGiaRepo.findAll(); // Lấy tất cả đánh giá từ cơ sở dữ liệu
        return danhGiaList.stream()
                .map(danhGia -> new DanhGiaDTO(danhGia))  // Chuyển đổi từ entity sang DTO
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteDanhGia(Integer idDanhGia) {
        // Kiểm tra xem đánh giá có tồn tại không
        Optional<DanhGia> danhGiaOptional = danhGiaRepo.findById(idDanhGia);
        if (danhGiaOptional.isPresent()) {
            // Nếu đánh giá tồn tại, tiến hành xóa
            danhGiaRepo.deleteById(idDanhGia);
            return true; // Trả về true nếu xóa thành công
        }
        return false; // Trả về false nếu không tìm thấy đánh giá
    }

}
