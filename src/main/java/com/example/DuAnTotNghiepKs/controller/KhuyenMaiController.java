package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.KhuyenMaiDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.repository.KhuyenMaiRepo;
import com.example.DuAnTotNghiepKs.service.KhachHangService;
import com.example.DuAnTotNghiepKs.service.KhuyenMaiService;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quan-ly-khuyen-mai")
public class KhuyenMaiController {
    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private KhuyenMaiRepo khuyenMaiRepo;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping()
    public String showKhuyenMaiPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.DESC, "ngayKetThuc");
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiService.getKhuyenMaiPage(page, size,sort);

        // Lấy danh sách khuyến mãi còn hạn
        List<KhuyenMai> activeVouchers = khuyenMaiService.getActiveVouchers();

        model.addAttribute("khuyenMaiPage", khuyenMaiPage);
        model.addAttribute("listKH", khachHangService.getAll());
        model.addAttribute("Voucher", new KhuyenMai());
        model.addAttribute("activeVouchers", activeVouchers);

        //lấy id nhân viên
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg()); // Đảm bảo rằng bạn có trường img trong NhanVienDTO
        }
        return "list/QuanLyKhuyenMai/KhuyenMai";
    }


    @PostMapping("/add")
    public ResponseEntity<?> save(
            @RequestParam("maKhuyenMai") String maKhuyenMai,
            @RequestParam("tenKhuyenMai") String tenKhuyenMai,
            @RequestParam("moTa") String moTa,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("giamGia") Float giamGia,
            @RequestParam("giamToiThieu") Float giamToiThieu,
            @RequestParam("giamToiDa") Float giamToiDa,
            @RequestParam("ngayBatDau") @DateTimeFormat(pattern = ("yyyy-MM-dd'T'HH:mm")) Date ngayBatDau,
            @RequestParam("ngayKetThuc") @DateTimeFormat(pattern = ("yyyy-MM-dd'T'HH:mm")) Date ngayKetThuc,
//            @RequestParam("trangThai") String trangThai,
//            @RequestParam("loaiGiam") Boolean loaiGiam)
            @RequestParam("loaiGiam") String loaiGiamStr

//            ServletRequest servletRequest
    ) {
        try {
            // Kiểm tra điều kiện
            if (maKhuyenMai.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mã khuyến mãi không được để trống."));
            }
            if (tenKhuyenMai.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tên khuyến mãi không được để trống."));
            }
            if (soLuong <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Số lượng phải lớn hơn 0."));
            }
            if (giamGia < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Giảm giá không được âm."));
            }
            if (giamToiThieu < 0 || giamToiDa < 0 || giamToiDa < giamToiThieu) {
                return ResponseEntity.badRequest().body(Map.of("error", "Giá không hợp lệ."));
            }
            if (moTa.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Mô tả không được để trống."));
            }
            if (ngayBatDau.after(ngayKetThuc)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Ngày bắt đầu phải trước ngày kết thúc."));
            }
            KhuyenMai khuyenMai = new KhuyenMai();
            khuyenMai.setMaKhuyenMai(maKhuyenMai);
            khuyenMai.setTenKhuyenMai(tenKhuyenMai);
            khuyenMai.setMoTa(moTa);
            khuyenMai.setSoLuong(soLuong);
            khuyenMai.setGiamGia(giamGia);
//            khuyenMai.setLoaiGiam(true);
            khuyenMai.setGiamToiThieu(giamToiThieu);
            khuyenMai.setGiamToiDa(giamToiDa);
            khuyenMai.setNgayBatDau(ngayBatDau);
            khuyenMai.setNgayKetThuc(ngayKetThuc);
//            khuyenMai.setTrangThai(trangThai);
            // Xử lý loại giảm
            Boolean loaiGiam = Boolean.valueOf(loaiGiamStr); // Chuyển đổi từ String sang Boolean
            khuyenMai.setLoaiGiam(loaiGiam);

            // Xử lý loại giảm
            if ("phanTram".equals(loaiGiam)) {
                khuyenMai.setLoaiGiam(true);  // Phần trăm
            } else if ("tienMat".equals(loaiGiam)) {
                khuyenMai.setLoaiGiam(false); // Tiền mặt
            }


            // Thiết lập trạng thái tự động dựa trên ngày hiện tại so với ngày bắt đầu và ngày kết thúc.
            Date today = new Date();
            if (today.before(ngayBatDau)) {
                khuyenMai.setTrangThai("Sắp diễn ra");
            } else if (today.after(ngayKetThuc)) {
                khuyenMai.setTrangThai("Hết hạn");
            } else {
                khuyenMai.setTrangThai("Còn hạn");
            }


            // Giả sử bạn có một service để lưu khuyến mãi
//           // khuyenMaiService.saveKhuyenMai(khuyenMai);
//            khuyenMai.getMaKhuyenMai();
            khuyenMaiService.saveKhuyenMai(khuyenMai);
//             return "redirect:/admin/quan-ly-khuyen-mai";

            return ResponseEntity.ok(Map.of("success", "Thêm khuyến mãi thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Đã có lỗi xảy ra: " + e.getMessage()));
        }
    }





    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateKhuyenMai(
            @PathVariable("id") Integer id,
            @RequestParam("maKhuyenMai") String maKhuyenMai,
            @RequestParam("tenKhuyenMai") String tenKhuyenMai,
            @RequestParam("moTa") String moTa,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("giamGia") Float giamGia,
            @RequestParam("giamToiThieu") Float giamToiThieu,
            @RequestParam("giamToiDa") Float giamToiDa,
            @RequestParam("loaiGiam") Boolean loaiGiam,
            @RequestParam("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime ngayBatDau,
            @RequestParam("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime ngayKetThuc,
            ServletRequest servletRequest) {

        try {
            // Kiểm tra điều kiện
            if (maKhuyenMai.isEmpty() || tenKhuyenMai.isEmpty() || soLuong <= 0 || giamGia < 0 ||
                    giamToiThieu < 0 || giamToiDa < 0 || giamToiDa < giamToiThieu || moTa.isEmpty() ||
                    ngayBatDau.isAfter(ngayKetThuc)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thông tin không hợp lệ."));
            }

            // Tìm khuyến mãi theo id
            KhuyenMai khuyenMai = khuyenMaiService.getKhuyenMaiById(id);
            if (khuyenMai == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Khuyến mãi không tồn tại."));
            }

            // Cập nhật thông tin khuyến mãi
            khuyenMai.setMaKhuyenMai(maKhuyenMai);
            khuyenMai.setTenKhuyenMai(tenKhuyenMai);
            khuyenMai.setMoTa(moTa);
            khuyenMai.setSoLuong(soLuong);
            khuyenMai.setGiamGia(giamGia);
            khuyenMai.setGiamToiThieu(giamToiThieu);
            khuyenMai.setGiamToiDa(giamToiDa);
            khuyenMai.setLoaiGiam(loaiGiam);
            khuyenMai.setNgayBatDau(java.sql.Timestamp.valueOf(ngayBatDau));
            khuyenMai.setNgayKetThuc(java.sql.Timestamp.valueOf(ngayKetThuc));

            // Xử lý loại giảm
            String loaiGiamm = servletRequest.getParameter("loaiGiam");
            if ("phanTram".equals(loaiGiamm)) {
                khuyenMai.setLoaiGiam(true);  // Phần trăm
            } else if ("tienMat".equals(loaiGiamm)) {
                khuyenMai.setLoaiGiam(false); // Tiền mặt
            }

            // Tự động thiết lập trạng thái khuyến mãi
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(ngayBatDau)) {
                khuyenMai.setTrangThai("Sắp diễn ra");
            } else if (now.isAfter(ngayKetThuc)) {
                khuyenMai.setTrangThai("Hết hạn");
            } else {
                khuyenMai.setTrangThai("Còn hạn");
            }

            // Lưu khuyến mãi
            khuyenMaiService.updateKhuyenMai(id, khuyenMai);

            return ResponseEntity.ok(Map.of("success", "Cập nhật khuyến mãi thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Đã có lỗi xảy ra: " + e.getMessage()));
        }
    }
//






    @GetMapping("/edit/{id}")
    public String showEditKhuyenMai(@PathVariable("id")  Integer id , Model model) {
        KhuyenMai khuyenMai=khuyenMaiService.getKhuyenMaiById(id);
//        System.out.println("Ngày Bắt Đầu: " + khuyenMai.getNgayBatDau());
//        System.out.println("Ngày Kết Thúc: " + khuyenMai.getNgayKetThuc());
        model.addAttribute("khuyenMai",khuyenMai);
        return "list/QuanLyKhuyenMai/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateKhuyenMai(@Valid @ModelAttribute KhuyenMai khuyenMai, BindingResult result, @PathVariable Integer id) {

        // Ngày hiện tại
        LocalDate today = LocalDate.now();
        LocalDate ngayBatDau = khuyenMai.getNgayBatDau() != null ?
                khuyenMai.getNgayBatDau().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate ngayKetThuc = khuyenMai.getNgayKetThuc() != null ?
                khuyenMai.getNgayKetThuc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        // Kiểm tra ngày bắt đầu phải lớn hơn ngày hiện tại
        if (ngayBatDau != null && ngayBatDau.isBefore(today)) {
            result.rejectValue("ngayBatDau", "error.khuyenMai", "Ngày bắt đầu phải lớn hơn ngày hiện tại");
        }

        // Kiểm tra ngày kết thúc phải lớn hơn ngày bắt đầu
        if (ngayKetThuc != null && ngayBatDau != null && ngayKetThuc.isBefore(ngayBatDau)) {
            result.rejectValue("ngayKetThuc", "error.khuyenMai", "Ngày kết thúc phải lớn hơn ngày bắt đầu");
        }

        // Kiểm tra lỗi và trả về trang chỉnh sửa nếu có lỗi
        if (result.hasErrors()) {
            return "list/QuanLyKhuyenMai/edit";
        }

        // Lưu thông tin khuyến mãi nếu không có lỗi
        khuyenMaiService.updateKhuyenMai(id,khuyenMai);
        return "redirect:/admin/quan-ly-khuyen-mai";
    }



    @PostMapping("/delete/{id}")
    public String deleteKhuyenMai(@PathVariable Integer id) {
        khuyenMaiService.deleteKhuyenMaiById(id);
        return "redirect:/admin/quan-ly-khuyen-mai" ;
    }

//    @GetMapping("/search")
//    public String searchKhuyenMai(@RequestParam("keyword") String keyword, Model model) {
//        List<KhuyenMai> khuyenMais = khuyenMaiService.searchKhuyenMai(keyword);
//        model.addAttribute("khuyenMais", khuyenMais);
//        return "khuyenmai";
//    }

    @GetMapping("/search")
    public ResponseEntity<Page<KhuyenMai>> searchKhuyenMai(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "trangThai", required = false) String trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "ngayKetThuc"); // Sắp xếp theo ngày kết thúc
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiService.searchKhuyenMai(keyword, trangThai, page, size, sort);

        return ResponseEntity.ok(khuyenMaiPage); // Trả về trang kết quả
    }


}
