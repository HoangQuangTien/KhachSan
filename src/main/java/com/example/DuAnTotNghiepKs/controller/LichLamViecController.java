package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.CaLamViecDTO;
import com.example.DuAnTotNghiepKs.DTO.LichLamViecDTO;
import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import com.example.DuAnTotNghiepKs.service.CaLamViecService;
import com.example.DuAnTotNghiepKs.service.LichLamViecService;
import com.example.DuAnTotNghiepKs.service.NhanVienService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/lich-lam-viec")
public class LichLamViecController {

    @Autowired
    private LichLamViecService lichLamViecService;
    @Autowired
    private NhanVienService nhanVienService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CaLamViecService caLamViecService;

    @GetMapping
    public String loadAll(Model model) {
//        List<LichLamViecDTO> lichLamViecDTOS = lichLamViecService.getAll();
        List<NhanVienDTO> nhanVienList = nhanVienService.getAll(); // Lấy danh sách nhân viên
        List<CaLamViecDTO> caLamViecList = caLamViecService.getAll(); // Lấy danh sách ca làm việc
        model.addAttribute("nhanVienList", nhanVienList);
        model.addAttribute("caLamViecList", caLamViecList);

        return "list/QuanLyLichLamViec/lichLamViec";
    }

    @GetMapping("/load")
    public ResponseEntity<List<LichLamViecDTO>> load() {
        List<LichLamViecDTO> lichLamViecDTOS = lichLamViecService.getAll();
        return ResponseEntity.ok(lichLamViecDTOS);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addLich(
            @RequestParam("ngay") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngay,
            @RequestParam("nhanVien") Integer idNhanVien,
            @RequestParam("caLamViec") String caLamViec) {

        try {
            // Kiểm tra các trường không được để trống
            if (idNhanVien == null || caLamViec == null || caLamViec.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Các trường không được để trống"));
            }

            // Kiểm tra ngày không được nhỏ hơn ngày hiện tại
            Date today = new Date();
            if (ngay.before(today)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ngày làm việc không được nhỏ hơn ngày hiện tại"));
            }

            // Tạo mã lịch làm việc mới
            String maLichLamViec = generateMaLichLamViec();

            // Tạo đối tượng LichLamViecDTO và set các thuộc tính
            LichLamViecDTO lichLamViecDTO = new LichLamViecDTO();
            lichLamViecDTO.setMaLichLamViec(maLichLamViec);
            lichLamViecDTO.setIdNhanVien(idNhanVien);
            lichLamViecDTO.setNgay(ngay);
            lichLamViecDTO.setMaCaLamViec(caLamViec);

            // Lưu đối tượng
            lichLamViecService.save(lichLamViecDTO);

            // Trả về phản hồi thành công
            return ResponseEntity.ok(Map.of("success", "Thêm thành công"));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());

            // Trả về phản hồi lỗi
            return ResponseEntity.badRequest().body(Map.of("error", "Đã xảy ra lỗi khi thêm: " + e.getMessage()));
        }
    }


    @PostMapping("/update/{idLichLamViec}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable("idLichLamViec") Integer idLichLamViec,
            @RequestBody Map<String, Object> requestBody
    ) {
        try {
            // Lấy dữ liệu từ request body
            String nhanVienString = (String) requestBody.get("nhanVien");
            Integer idNhanVien = Integer.parseInt(nhanVienString);  // Ép kiểu từ String sang Integer

            String maCaLamViec = (String) requestBody.get("caLamViec");
            String ngayString = (String) requestBody.get("ngay");  // Lấy ngày dưới dạng chuỗi
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Định dạng ngày
//            Date ngay = sdf.parse(ngayString);  // Chuyển đổi chuỗi thành đối tượng Date

            // Kiểm tra các trường không được để trống
            if (idNhanVien == null || maCaLamViec == null || maCaLamViec.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Các trường không được để trống"));
            }

            // Kiểm tra ngày không được nhỏ hơn ngày hiện tại
//            Date today = new Date();
//            if (ngay.before(today)) {
//                return ResponseEntity.badRequest().body(Map.of("error", "Ngày làm việc không được nhỏ hơn ngày hiện tại"));
//            }

            // Lấy lịch làm việc hiện tại theo id
            LichLamViecDTO lichLamViecDTO = lichLamViecService.findById(idLichLamViec);
            lichLamViecDTO.setMaCaLamViec(maCaLamViec);
            lichLamViecDTO.setIdNhanVien(idNhanVien);
//            lichLamViecDTO.setNgay(ngay);  // Cập nhật ngày làm việc

            // Lưu lại lịch làm việc đã chỉnh sửa
            lichLamViecService.save(lichLamViecDTO);

            // Trả về phản hồi thành công
            return ResponseEntity.ok(Map.of("success", "Sửa lịch thành công"));
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());

            // Trả về phản hồi lỗi
            return ResponseEntity.badRequest().body(Map.of("error", "Đã xảy ra lỗi khi sửa: " + e.getMessage()));
        }
    }



    private String generateMaLichLamViec() {
        String prefix = "LLV";
        LichLamViecDTO lichLamViecDTO = lichLamViecService.findTopByOrderByIdLichLamViecDesc();
        int nextId = lichLamViecDTO != null ? (int) (lichLamViecDTO.getIdLichLamViec() + 1) : 1;
        return prefix + String.format("%03d", nextId);
    }


}
