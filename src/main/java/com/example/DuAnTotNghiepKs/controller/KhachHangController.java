package com.example.DuAnTotNghiepKs.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.service.DatPhongService;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.DuAnTotNghiepKs.DTO.DiaChiKhachHangDTO;
import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.entity.KhachHang;
import com.example.DuAnTotNghiepKs.service.DiaChiKhachHangService;
import com.example.DuAnTotNghiepKs.service.KhachHangService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private DiaChiKhachHangService diaChiKhachHangService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private DatPhongService ;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @GetMapping("quan-ly-khach-hang")
    public String hienThiKhachHang(Model model, @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHangDTO> khachHangDTOS = khachHangService.getAll(pageable);

        model.addAttribute("page", khachHangDTOS);
        model.addAttribute("khachHangDTO", new KhachHangDTO());
        model.addAttribute("diaChiDTO", new DiaChiKhachHangDTO());
        return "list/quan-ly-khach-hang2";
    }

    @PostMapping("/quan-ly-khach-hang/them")
    public String addKhachHang(@ModelAttribute KhachHangDTO khachHangDTO) {
        // Kiểm tra nếu mã khách hàng chưa tồn tại, sinh mã tự động
        if (khachHangDTO.getMaKhachHang() == null || khachHangDTO.getMaKhachHang().isEmpty()) {
            String generatedMaKhachHang = khachHangService.generateMaKhachHang();
            khachHangDTO.setMaKhachHang(generatedMaKhachHang); // Gán mã tự động vào DTO
        }
        // Gọi service để lưu khách hàng
        khachHangService.save(khachHangDTO);
        return "redirect:/quan-ly-khach-hang";
    }

    @GetMapping("/quan-ly-khach-hang-detail")
    public String detailHTKH(@RequestParam(value = "id", required = false) Integer id,
                             @RequestParam(value = "page", defaultValue = "0") int pageDiaChi,
                             @RequestParam(value = "sizeDiaChi", defaultValue = "3") int sizeDiaChi, Model model) {
        if (id != null) {
            KhachHangDTO khachHangDTO = khachHangService.findById(id);
            Pageable pageable = PageRequest.of(pageDiaChi, sizeDiaChi, Sort.by("createdAt").descending()); // Sử dụng
            // pageDiaChi
            // và
            // sizeDiaChi
            // từ
            // request
            Page<DiaChiKhachHangDTO> diaChiPage = diaChiKhachHangService.findByIdDC(id, pageable);
            List<DiaChiKhachHangDTO> diaChiList = new ArrayList<>(diaChiPage.getContent());

            if (diaChiList.isEmpty() && pageDiaChi > 0) {
                return "redirect:/quan-ly-khach-hang-detail?id=" + id + "&page=" + (pageDiaChi - 1); // Sửa đổi thành
                // pageDiaChi -
                // 1
            }

//            Collections.reverse(diaChiList);

            model.addAttribute("khachHang", khachHangDTO);
            model.addAttribute("diaChiList", diaChiList);
            model.addAttribute("totalPages", diaChiPage.getTotalPages());
            model.addAttribute("currentPage", pageDiaChi);
            model.addAttribute("id", id); // Chỉ thêm id vào model nếu nó tồn tại
            model.addAttribute("sizeDiaChi", sizeDiaChi); // Thêm size vào model để nó có thể truyền lại vào view
        }

        return "list/quan-ly-dia-chi-khach-hang";
    }

    @PostMapping("/quan-ly-khach-hang/cap-nhat")
    public String updateKH(@ModelAttribute KhachHangDTO khachHangDTO) {
        khachHangService.update(khachHangDTO.getId(), khachHangDTO);
        return "redirect:/quan-ly-khach-hang";
    }

    @GetMapping("/khach-hang/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/khach-hang";
    }

    @GetMapping("/khach-hang/trang-ca-nhan")
    public String trangcanhan(Model model) {
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession();
        if (taiKhoanDTO != null && taiKhoanDTO.getKhachHangDTO() != null
                && taiKhoanDTO.getKhachHangDTO().getHoVaTen() != null) {
            model.addAttribute("user", taiKhoanDTO.getKhachHangDTO());
            List<DiaChiKhachHangDTO> listDiaChiKhachHangDTO = diaChiKhachHangService
                    .findById(taiKhoanDTO.getKhachHangDTO().getId());
            if (listDiaChiKhachHangDTO.size() > 0) {
                model.addAttribute("userDC", listDiaChiKhachHangDTO.get(0));
            } else {
                model.addAttribute("userDC", new DiaChiKhachHangDTO());
            }
        } else {
            return "redirect:/login";
        }
        return "list/KhachHang/trangcanhan";
    }

    @PutMapping("/khach-hang/trang-ca-nhan")
    public ResponseEntity<?> register(@RequestParam String hoVaTen, @RequestParam String soDienThoai,
                                      @RequestParam Boolean gioiTinh, @RequestParam String email,
                                      @RequestParam(value = "diaChi", required = false) String diaChi,
                                      @RequestParam(value = "thanhPho", required = false) String thanhPho,
                                      @RequestParam(value = "phuongXa", required = false) String phuongXa,
                                      @RequestParam(value = "quan", required = false) String quan) {
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession();
        if (taiKhoanDTO == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "user không tồn tại"));
        }
        KhachHangDTO khachHangDTO = taiKhoanDTO.getKhachHangDTO();
        khachHangDTO.setHoVaTen(hoVaTen);
        khachHangDTO.setSoDienThoai(soDienThoai);
        khachHangDTO.setGioiTinh(gioiTinh);
        khachHangDTO.setEmail(email);

        khachHangService.update(khachHangDTO.getId(), khachHangDTO);

        List<DiaChiKhachHangDTO> listDiaChiKhachHangDTO = diaChiKhachHangService.findById(khachHangDTO.getId());

        if (listDiaChiKhachHangDTO.size() == 0) {
            DiaChiKhachHangDTO diaChiKhachHangDTO = new DiaChiKhachHangDTO();
            diaChiKhachHangDTO.setIdKhachHang(khachHangDTO.getId());
            diaChiKhachHangDTO.setDiaChiCuThe(diaChi);
            diaChiKhachHangDTO.setThanhPho(thanhPho);
            diaChiKhachHangDTO.setPhuongXa(phuongXa);
            diaChiKhachHangDTO.setQuanHuyen(quan);
            diaChiKhachHangDTO.setCreatedAt(LocalDateTime.now());
            diaChiKhachHangService.save(diaChiKhachHangDTO);
        } else {
            DiaChiKhachHangDTO diaChiKhachHangDTO = listDiaChiKhachHangDTO.get(0);
            diaChiKhachHangDTO.setIdKhachHang(khachHangDTO.getId());
            diaChiKhachHangDTO.setDiaChiCuThe(diaChi);
            diaChiKhachHangDTO.setThanhPho(thanhPho);
            diaChiKhachHangDTO.setPhuongXa(phuongXa);
            diaChiKhachHangDTO.setQuanHuyen(quan);
            diaChiKhachHangDTO.setUpdatedAt(LocalDateTime.now());
            diaChiKhachHangService.save(diaChiKhachHangDTO);
        }

        return ResponseEntity.ok(Map.of("message", "Cập nhật tài khoản thành công"));
    }


//    @GetMapping("/datphongKH")
//    public String showDatPhongList(Model model) {
//        // Lấy tất cả thông tin đặt phòng chưa check-in
//        List<DatPhong> datPhongs = datPhongService.getDatPhongChuaVaDaCheckIn();
//        model.addAttribute("datPhongs", datPhongs);
//
//        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
//        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
//            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
//            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg());
//        }
//
//        return "list/QuanLyDatPhong/nguoidicung"; // Đường dẫn tới trang HTML hiển thị danh sách đặt phòng
//    }


}
