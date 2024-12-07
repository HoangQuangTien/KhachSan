package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.DTO.ThanhToanDTO;
import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.KhuyenMai;
import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import com.example.DuAnTotNghiepKs.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/thanhToan")
public class ThanhToanController {

    @Autowired
    private ThanhToanService thanhToanService;

    @Autowired
    private DatPhongService datPhongService;

    @Autowired
    private PhuPhiService phuPhiService;

    @Autowired
    private PhongService phongService;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private DatPhongRepo datPhongRepo; // Repository để truy cập dữ liệu đặt phòng


    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @GetMapping()
    public String loadAll(
            @ModelAttribute ThanhToanDTO thanhToanDTO,
            @RequestParam(value = "page", defaultValue = "0") int currentPage, // Trang hiện tại
            @RequestParam(value = "size", defaultValue = "2") int pageSize, // Kích thước trang
            Model model) {

        // Lấy danh sách đặt phòng đã cọc
        Page<DatPhong> datPhongPage = datPhongService.getDatPhongsDaCoc(currentPage, pageSize);

        // Lấy danh sách khuyến mãi còn hạn
        List<KhuyenMai> activeVouchers = khuyenMaiService.getActiveVouchers();

        // Thêm dữ liệu vào model
        model.addAttribute("datPhongs", datPhongPage.getContent());
        model.addAttribute("phuPhis", phuPhiService.getAll());

        // Thêm thông tin phân trang
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("currentPage", pageSize);
        model.addAttribute("totalPagesDatPhong", datPhongPage.getTotalPages());
        model.addAttribute("activeVouchers", activeVouchers);

        //lấy id nhân viên
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg()); // Đảm bảo rằng bạn có trường img trong NhanVienDTO
        }




        return "list/QuanLyThanhToan/thanhToan"; // Trả về view


    }







    @PostMapping("/add")
    public String save(@ModelAttribute ThanhToanDTO thanhToanDTO, @RequestParam(required = false) Integer idKhuyenMai, RedirectAttributes redirectAttributes) {
        try {
            if (idKhuyenMai != null) {
                ResponseEntity<Map<String, Object>> response = khuyenMaiService.capNhatSoLuong(idKhuyenMai);

                if (!(Boolean) response.getBody().get("success")) {
                    redirectAttributes.addFlashAttribute("errorMessage", response.getBody().get("message"));
                    return "redirect:/thanhToan"; // Quay lại trang thanh toán nếu không thành công
                }
            }

            // Lưu thông tin thanh toán
            thanhToanService.save(thanhToanDTO);

            // Lấy thời gian cho phép chuyển trạng thái
            long thoiGianChoPhep = thanhToanService.getThoiGianChoPhepChuyenTrangThai();

            // Tạo một Thread để thay đổi trạng thái phòng sau thời gian đã chỉ định
            new Thread(() -> {
                try {
                    // Đợi thời gian cho phép chuyển trạng thái
                    Thread.sleep(thoiGianChoPhep);

                    // Cập nhật trạng thái phòng
                    phongService.updateRoomStatusToAvailable(thanhToanDTO.getIdPhong());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Khôi phục trạng thái ngắt
                }
            }).start();

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công!");

        } catch (Exception e) {
            // Thêm thông báo lỗi vào redirect nếu có lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra, vui lòng thử lại!");
        }

        // Redirect về trang thanh toán nếu có lỗi
        return "redirect:/thanhToan";
    }





}
