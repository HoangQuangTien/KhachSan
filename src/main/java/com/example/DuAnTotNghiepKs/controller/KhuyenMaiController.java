package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.service.KhuyenMaiService;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quan-ly-khuyen-mai")
public class KhuyenMaiController {
    @Autowired
    private KhuyenMaiService khuyenMaiService;

//    @Autowired
//    private KhuyenMaiRepo khuyenMaiRepo;

    @GetMapping()
    public String showKhuyenMaiPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.DESC, "ngayKetThuc");
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiService.getKhuyenMaiPage(page, size,sort);
        model.addAttribute("khuyenMaiPage", khuyenMaiPage);
        return "list/QuanLyKhuyenMai/KhuyenMai";
    }

//    @GetMapping()
//    public String showKhuyenMaiPage(Model model) {
//        List<KhuyenMai> khuyenMais = khuyenMaiService.getAllKhuyenMai();
//        model.addAttribute("khuyenMais",khuyenMais);
//        return "khuyenmai";
//    }

//    @GetMapping
//    public List<KhuyenMai> getAllKhuyenMai() {
//        return khuyenMaiService.getAllKhuyenMai();
//
//    }

//    @GetMapping("/view-add")
//    public String  showAdd(Model model) {
//        model.addAttribute("khuyenMai", new KhuyenMai());
//        return "list/QuanLyKhuyenMai/add";
//
//    }

//    @PostMapping("/add")
//    public String addKhuyenMai(@ModelAttribute @Valid KhuyenMai khuyenMai, BindingResult result) {
//
//
//        // Ngày hiện tại
//        LocalDate today = LocalDate.now();
//        LocalDate ngayBatDau = khuyenMai.getNgayBatDau() != null ?
//                khuyenMai.getNgayBatDau().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
//        LocalDate ngayKetThuc = khuyenMai.getNgayKetThuc() != null ?
//                khuyenMai.getNgayKetThuc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
//
//        // Kiểm tra ngày bắt đầu phải lớn hơn ngày hiện tại
//        if (ngayBatDau != null && ngayBatDau.isBefore(today)) {
//            result.rejectValue("ngayBatDau", "error.khuyenMai", "Ngày bắt đầu phải lớn hơn ngày hiện tại");
//        }
//
//        // Kiểm tra ngày kết thúc phải lớn hơn ngày bắt đầu
//        if (ngayKetThuc != null && ngayBatDau != null && ngayKetThuc.isBefore(ngayBatDau)) {
//            result.rejectValue("ngayKetThuc", "error.khuyenMai", "Ngày kết thúc phải lớn hơn ngày bắt đầu");
//        }
//
//        if (result.hasErrors()) {
//            return "redirect:/admin/quan-ly-khuyen-mai";
//        }
//        if (khuyenMaiService.existsByMaKhuyenMai(khuyenMai.getMaKhuyenMai())) {
//            result.rejectValue("maKhuyenMai", "error.khuyenMai", "Mã khuyến mãi đã tồn tại");
//            return "redirect:/admin/quan-ly-khuyen-mai";
//        }
//        khuyenMai.getMaKhuyenMai();
//        khuyenMaiService.saveKhuyenMai(khuyenMai);
//        return "redirect:/admin/quan-ly-khuyen-mai";
//    }



    @PostMapping("/add")
    public ResponseEntity<?> save(
            @RequestParam("maKhuyenMai") String maKhuyenMai,
            @RequestParam("tenKhuyenMai") String tenKhuyenMai,
            @RequestParam("moTa") String moTa,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("giamGia") Float giamGia,
            @RequestParam("giamToiThieu") Float giamToiThieu,
            @RequestParam("giamToiDa") Float giamToiDa,
            @RequestParam("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
            @RequestParam("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc,
//            @RequestParam("trangThai") String trangThai,
//            @RequestParam("loaiGiam") Boolean loaiGiam)
            @RequestParam("loaiGiam") String loaiGiamStr,
            @RequestParam("trangThai") String trangThai

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
//            if (img.isEmpty()) {
//                return ResponseEntity.badRequest().body(Map.of("error", "Tệp ảnh không được để trống."));
//            }
//            // Tạo đường dẫn thư mục static/img
//            String uploadDir = "src/main/resources/static/img";
//            String fileName = img.getOriginalFilename();
//            Path filePath = Paths.get(uploadDir, fileName);
//
//            // Lưu file ảnh vào hệ thống
//            Files.copy(img.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Tạo đối tượng khuyến mãi và lưu vào cơ sở dữ liệu
            // Lấy giá trị trangThai từ form
//            String trangThai = servletRequest.getParameter("trangThai");
//            Boolean loaiGiam = Boolean.valueOf(servletRequest.getParameter("loaiGiam"));
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
            // Gán trạng thái dựa trên radio button
            if ("conHan".equals(trangThai)) {
                khuyenMai.setTrangThai("Còn hạn");
            } else if ("hetHan".equals(trangThai)) {
                khuyenMai.setTrangThai("Hết hạn");
            } else if ("sapDienRa".equals(trangThai)) {
                khuyenMai.setTrangThai("Sắp diễn ra");
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
            @RequestParam("id") Integer id,
            @RequestParam("maKhuyenMai") String maKhuyenMai,
            @RequestParam("tenKhuyenMai") String tenKhuyenMai,
            @RequestParam("moTa") String moTa,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("giamGia") Float giamGia,
            @RequestParam("giamToiThieu") Float giamToiThieu,
            @RequestParam("giamToiDa") Float giamToiDa,
            @RequestParam("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
            @RequestParam("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc,
            ServletRequest servletRequest) {

        try {
            // Kiểm tra điều kiện
            if (maKhuyenMai.isEmpty() || tenKhuyenMai.isEmpty() || soLuong <= 0 || giamGia < 0 ||
                    giamToiThieu < 0 || giamToiDa < 0 || giamToiDa < giamToiThieu || moTa.isEmpty() ||
                    ngayBatDau.after(ngayKetThuc)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thông tin không hợp lệ."));
            }

            // Tìm khuyến mãi theo id
            KhuyenMai khuyenMai = khuyenMaiService.getKhuyenMaiById(id);
            if (khuyenMai == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Khuyến mãi không tồn tại."));
            }

            // Cập nhật thông tin khuyến mãi
            khuyenMai.setIdKhuyenMai(id);
            khuyenMai.setMaKhuyenMai(maKhuyenMai);
            khuyenMai.setTenKhuyenMai(tenKhuyenMai);
            khuyenMai.setMoTa(moTa);
            khuyenMai.setSoLuong(soLuong);
            khuyenMai.setGiamGia(giamGia);
            khuyenMai.setGiamToiThieu(giamToiThieu);
            khuyenMai.setGiamToiDa(giamToiDa);
            khuyenMai.setNgayBatDau(ngayBatDau);
            khuyenMai.setNgayKetThuc(ngayKetThuc);

            // Xử lý loại giảm
            String loaiGiam = servletRequest.getParameter("loaiGiam");
            if ("phanTram".equals(loaiGiam)) {
                khuyenMai.setLoaiGiam(true);  // Phần trăm
            } else if ("tienMat".equals(loaiGiam)) {
                khuyenMai.setLoaiGiam(false); // Tiền mặt
            }

            // Cập nhật trạng thái
            String trangThai = servletRequest.getParameter("trangThai");
            if ("conHan".equals(trangThai)) {
                khuyenMai.setTrangThai("Còn hạn");
            } else if ("hetHan".equals(trangThai)) {
                khuyenMai.setTrangThai("Hết hạn");
            } else if ("sapDienRa".equals(trangThai)) {
                khuyenMai.setTrangThai("Sắp diễn ra");
            }

            // Lưu khuyến mãi
            khuyenMaiService.saveKhuyenMai(khuyenMai);

            return ResponseEntity.ok(Map.of("success", "Cập nhật khuyến mãi thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Đã có lỗi xảy ra: " + e.getMessage()));
        }
    }
//





//    @PostMapping("/add")
//    public String addKhuyenMai(@ModelAttribute KhuyenMai khuyenMai,Model model){
//        khuyenMaiService.saveKhuyenMai(khuyenMai);
//        return"redirect:/khuyenmaii";
//    }
//    @PostMapping("/add")
//    public String addKhuyenMai(@Valid @ModelAttribute KhuyenMai khuyenMai, BindingResult result, Model model) {
//        // Kiểm tra lỗi xác thực
//        if (result.hasErrors()) {
//            // Nếu có lỗi, trả về lại trang với thông báo lỗi và dữ liệu đã nhập
//            model.addAttribute("khuyenMai", khuyenMai);
//            return "addKM"; // Thay đổi tên trang HTML tương ứng với trang hiện tại của bạn
//        }
//
//        // Nếu không có lỗi, lưu khuyến mãi vào cơ sở dữ liệu
//        khuyenMaiService.saveKhuyenMai(khuyenMai);
//
//        // Redirect đến trang danh sách khuyến mãi sau khi thêm thành công
//        return "redirect:/khuyenmaii";
//    }



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
    public String searchKhuyenMai(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "trangThai") String trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayKetThuc"));
        Page<KhuyenMai> khuyenMaiPage = khuyenMaiService.searchKhuyenMai(keyword, trangThai, page, size);

        model.addAttribute("khuyenMaiPage", khuyenMaiPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThai", trangThai);

        return "list/QuanLyKhuyenMai/KhuyenMai";
    }





}
