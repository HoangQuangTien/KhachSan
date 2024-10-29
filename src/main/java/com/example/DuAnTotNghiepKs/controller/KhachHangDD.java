package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.PhongDTO;
import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.repository.PhongRepo;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.PhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        List<Object[]> objects = datPhongService.getTopPhongDuocDatNhieuNhat1();
        return ResponseEntity.ok(objects);
    }

    @GetMapping("/searchh")
    public String searchPhong(@RequestParam("ngayNhan") String ngayNhanStr,
                              @RequestParam("ngayTra") String ngayTraStr,
                              @RequestParam("soLuongNguoi") Integer soLuongNguoi,
                              Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime ngayNhan = LocalDateTime.parse(ngayNhanStr, formatter);
        LocalDateTime ngayTra = LocalDateTime.parse(ngayTraStr, formatter);

        // Validate ngày trả không được trước ngày nhận
        if (ngayTra.isBefore(ngayNhan)) {
            model.addAttribute("error", "Ngày trả không được trước ngày nhận.");
            return "list/KhachHang/khachHang"; // View hiển thị lỗi
        }
        List<Phong> phongs = phongService.searchPhongs(ngayNhan, ngayTra, soLuongNguoi);
        model.addAttribute("phongs", phongs);
        return "list/KhachHang/khachHang"; // Tên của view hiển thị danh sách phòng
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
    @GetMapping("/hangphongdetail")
    public String showRoomDetailPage() {
        return "List/KhachHang/hangphongdetail";
    }

    // Phương thức tìm kiếm phòng theo ngày và số lượng người
    public List<Phong> searchPhongs(LocalDateTime ngayNhan, LocalDateTime ngayTra, Integer soLuongNguoi) {
        return phongRepo.findAvailablePhongs(ngayNhan, ngayTra, soLuongNguoi);
    }


}
