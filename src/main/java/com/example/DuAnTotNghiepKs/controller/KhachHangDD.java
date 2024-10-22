package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.PhongDTO;
import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.repository.PhongRepo;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.PhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping
@Controller
public class KhachHangDD {

    @Autowired
    private PhongService phongService;

    @Autowired
    private PhongRepo phongRepo;


    private final DatPhongService datPhongService;

    public KhachHangDD(DatPhongService phongService) {
        this.datPhongService = phongService;
    }


    @GetMapping("/khach-hang")
    public String hienThi(){
        return "list/KhachHang/khachHang";
    }


    @GetMapping("/load-phong")
    public ResponseEntity<List<Object[]>> loadAllPhongKhachHang(){
        List<Object[]> objects = datPhongService.getTopPhongDuocDatNhieuNhat();
        return ResponseEntity.ok(objects);
    }



    @GetMapping("/phong-theo-loai")
    public ResponseEntity<List<PhongDTO>> getRoomsByRoomType(@RequestParam(value = "loaiPhongId", required = false) Integer loaiPhongId) {
        if (loaiPhongId == null || loaiPhongId <= 0) {
            return ResponseEntity.ok(Collections.emptyList()); // Trả về [] nếu loaiPhongId không hợp lệ
        }

        List<Phong> phongs = phongService.getPhongsByLoaiPhong1(loaiPhongId);

        if (phongs.isEmpty()) {
            System.out.println("Không tìm thấy phòng nào cho loại phòng ID: " + loaiPhongId); // Thêm log này
            return ResponseEntity.ok(Collections.emptyList()); // Trả về [] nếu không tìm thấy phòng nào
        }

        List<PhongDTO> phongDTOs = phongs.stream()
                .map(phongService::convertToPhongDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(phongDTOs);
    }

    public List<Phong> getPhongsByLoaiPhong1(Integer loaiPhongId) {
        return phongRepo.findByLoaiPhong_IdLoaiPhongAndTrangThaiTrue(loaiPhongId);
    }
    public PhongDTO convertToPhongDTO(Phong phong) {
        PhongDTO dto = new PhongDTO();
        dto.setIdPhong(phong.getIdPhong());
        dto.setTenPhong(phong.getTenPhong());
        dto.setMaPhong(phong.getMaPhong());
        dto.setMoTa(phong.getMoTa());
        dto.setTinhTrang(phong.getTinhTrang());
        dto.setTrangThai(phong.getTrangThai());
        dto.setGia(phong.getGia());

        // Kiểm tra và thiết lập các thuộc tính liên quan đến LoaiPhong
        if (phong.getLoaiPhong() != null) {
            dto.setIdLoaiPhong(phong.getLoaiPhong().getIdLoaiPhong());
            dto.setTenLoaiPhong(phong.getLoaiPhong().getTenLoaiPhong());
            dto.setGiaLoaiPhong(phong.getLoaiPhong().getGia());
            dto.setSoNguoiToiDa(phong.getLoaiPhong().getSoNguoiToiDa()); // Thêm dòng này
        }

        return dto;
    }

}
