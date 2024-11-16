package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.LoaiPhongDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.DienTich;
import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import com.example.DuAnTotNghiepKs.entity.Tang;
import com.example.DuAnTotNghiepKs.repository.DienTichRepo;
import com.example.DuAnTotNghiepKs.repository.TangRepo;
import com.example.DuAnTotNghiepKs.service.DienTichService;
import com.example.DuAnTotNghiepKs.service.LoaiPhongService;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import com.example.DuAnTotNghiepKs.service.TangService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/loaiphongs")
public class LoaiPhongController {
    @Autowired
    private LoaiPhongService loaiPhongService;

    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private TangRepo tangRepo;
    @Autowired
    private TangService tangService;
    @Autowired
    private DienTichService dienTichService ;
    @Autowired
    private DienTichRepo dienTichRepo;


    @GetMapping
    public String listLoaiPhong(@RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "5") int size,
                                Model model) {
        Page<LoaiPhong> loaiPhongPage=loaiPhongService.getLoaiPhongPage(page,size);
        model.addAttribute("loaiPhongPage", loaiPhongPage);

        //lấy id nhân viên
        TaiKhoanDTO taiKhoanDTO = taiKhoanService.getTaiKhoanTuSession(); // Lấy thông tin tài khoản từ session
        if (taiKhoanDTO != null && taiKhoanDTO.getNhanVienDTO().getHoTen() != null) {
            model.addAttribute("hoTen", taiKhoanDTO.getNhanVienDTO().getHoTen());
            model.addAttribute("img", taiKhoanDTO.getNhanVienDTO().getImg()); // Đảm bảo rằng bạn có trường img trong NhanVienDTO
        }

        return "list/QuanLyLoaiPhong/loaiphongs";
    }

    @GetMapping("/create")
    public String createLoaiPhongForm(Model model) {
        model.addAttribute("loaiPhong", new LoaiPhong());

        // Lấy danh sách tầng và diện tích từ cơ sở dữ liệu
        List<Tang> tangs = tangRepo.findAll();
        List<DienTich> dienTichs = dienTichRepo.findAll();

        // Thêm danh sách tầng và diện tích vào model
        model.addAttribute("tangs", tangs);
        model.addAttribute("dienTichs", dienTichs);

        return "list/QuanLyLoaiPhong/Create";
    }


    @PostMapping("/save")
    public String saveLoaiPhong(@ModelAttribute("loaiPhong") @Valid LoaiPhong loaiPhong,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        // Kiểm tra lỗi binding từ form
        if (result.hasErrors()) {
            return "list/QuanLyLoaiPhong/Create";  // Trả về form nếu có lỗi
        }


        // Tạo mã loại phòng tự động
        String maLoaiPhongMoi = generateMaLoaiPhong();
        loaiPhong.setMaLoaiPhong(maLoaiPhongMoi);

        // Lưu loại phòng vào cơ sở dữ liệu
        loaiPhongService.saveLoaiPhong(loaiPhong);

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("message", "Loại phòng đã được lưu thành công");
        redirectAttributes.addFlashAttribute("messageClass", "alert-success");

        // Điều hướng về trang danh sách loại phòng
        return "redirect:/loaiphongs";
    }


    private String generateMaLoaiPhong() {
        // Lấy số lượng loại phòng hiện có để tạo mã mới
        long count = loaiPhongService.countLoaiPhong();  // Tạo phương thức đếm số lượng loại phòng
        return "LP" + String.format("%03d", count + 1);  // Tạo mã LP001, LP002, LP003,...
    }

    @PostMapping("/save-tang")
    public ResponseEntity<Tang> saveTang() {
        try {
            // Đếm số tầng hiện tại để tạo tên tự động
            long countTang = tangService.countTang();

            // Tạo đối tượng Tang mới và đặt tên tự động
            Tang tang = new Tang();
            tang.setTenTang("Tang " + (countTang + 1));

            // Gọi service để lưu tầng vào DB
            Tang savedTang = tangService.saveTang(tang);

            // Trả về tầng mới đã lưu
            return ResponseEntity.ok(savedTang);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Lỗi khi lưu
        }
    }


    @PostMapping("/save-dien-tich")
    public ResponseEntity<DienTich> saveDienTich(@RequestBody DienTich dienTich) {
        try {
            DienTich savedDienTich = dienTichService.saveDienTich(dienTich); // Lưu diện tích vào DB
            return ResponseEntity.ok(savedDienTich); // Trả về diện tích mới đã lưu
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Lỗi khi lưu
        }
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        // Lấy loại phòng từ dịch vụ theo ID
        LoaiPhong loaiPhong = loaiPhongService.getLoaiPhongById1(id);
        if (loaiPhong != null) {
            // Nếu tìm thấy loại phòng, thêm nó vào model và trả về view chỉnh sửa
            model.addAttribute("loaiPhong", loaiPhong);
            return "list/QuanLyLoaiPhong/edit"; // Trả về trang chỉnh sửa
        } else {
            // Nếu không tìm thấy loại phòng, chuyển hướng và hiển thị thông báo lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy loại phòng với ID: " + id);
            return "redirect:/loaiphongs"; // Quay lại danh sách các loại phòng
        }
    }

    @PostMapping("/edit/{id}")
    public String updateLoaiPhong(@PathVariable("id") Integer id,
                                  @ModelAttribute("loaiPhong") @Valid LoaiPhong loaiPhong,
                                  BindingResult result,
                                  Model model) {
        // Kiểm tra nếu có lỗi xác thực
        if (result.hasErrors()) {
            // Nếu có lỗi xác thực, trả lại trang chỉnh sửa với thông báo lỗi
            return "list/QuanLyLoaiPhong/edit";
        }

        // Lấy loại phòng hiện tại từ cơ sở dữ liệu
        LoaiPhong existingLoaiPhong = loaiPhongService.getLoaiPhongById1(id);

        if (existingLoaiPhong != null) {
            // Cập nhật thông tin loại phòng
            existingLoaiPhong.setMaLoaiPhong(loaiPhong.getMaLoaiPhong());
            existingLoaiPhong.setTenLoaiPhong(loaiPhong.getTenLoaiPhong());
            existingLoaiPhong.setMoTa(loaiPhong.getMoTa());
            existingLoaiPhong.setSoLuongGiuong(loaiPhong.getSoLuongGiuong());
            existingLoaiPhong.setSoNguoiToiDa(loaiPhong.getSoNguoiToiDa());
            existingLoaiPhong.setGia(loaiPhong.getGia());
            existingLoaiPhong.setSucChua(loaiPhong.getSucChua());

            // Lưu lại loại phòng đã được cập nhật
            loaiPhongService.saveLoaiPhong(existingLoaiPhong);

            // Chuyển hướng về trang danh sách loại phòng
            return "redirect:/loaiphongs";
        } else {
            // Nếu không tìm thấy loại phòng, hiển thị thông báo lỗi
            model.addAttribute("errorMessage", "Không tìm thấy loại phòng với ID: " + id);
            return "redirect:/loaiphongs";
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public Page<LoaiPhongDTO> search(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        Page<LoaiPhong> loaiPhongPage = loaiPhongService.searchLoaiPhong(keyword, page, size);

        // Chuyển đổi từ LoaiPhong thành LoaiPhongDTO nếu cần
        Page<LoaiPhongDTO> loaiPhongDTOPage = loaiPhongPage.map(this::convertToLoaiPhongDTO);

        return loaiPhongDTOPage;
    }

    private LoaiPhongDTO convertToLoaiPhongDTO(LoaiPhong loaiPhong) {
        return new LoaiPhongDTO(
                loaiPhong.getIdLoaiPhong(),
                loaiPhong.getMaLoaiPhong(),
                loaiPhong.getTenLoaiPhong(),
                loaiPhong.getMoTa(),
                loaiPhong.getSoLuongGiuong(),
                loaiPhong.getSoNguoiToiDa(),
                loaiPhong.getGia(),
                loaiPhong.getDienTich(),
                loaiPhong.getSucChua(),
                loaiPhong.getTang()
        );
    }


    @GetMapping("/delete/{id}")
    public String deleteLoaiPhong(@PathVariable int id) {
        loaiPhongService.deleteLoaiPhong(id);
        return "redirect:/loaiphongs";
    }
}
