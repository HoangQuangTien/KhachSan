package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.DTO.PhongDTO;
import com.example.DuAnTotNghiepKs.entity.DienTich;
import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.entity.Tang;
import com.example.DuAnTotNghiepKs.service.DienTichService;
import com.example.DuAnTotNghiepKs.service.LoaiPhongService;
import com.example.DuAnTotNghiepKs.service.PhongService;
import com.example.DuAnTotNghiepKs.service.TangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/phongs")
public class PhongController {
    @Autowired
    private PhongService phongService;

    @Autowired
    private LoaiPhongService loaiPhongService;

    @Autowired
    private TangService tangService;

    @Autowired
    private DienTichService dienTichService;

//    @Autowired
//    private ChiTietPhongService chiTietPhongService;



    @GetMapping
    public String listPhong(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) Integer loaiPhongId,
                            @RequestParam(required = false) Boolean tinhTrang,
                            Model model) {
        int pageSize = 5; // Số lượng phòng trên mỗi trang
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("idPhong").ascending());
        Page<Phong> phongPage;

        if (loaiPhongId != null && tinhTrang != null) {
            phongPage = phongService.getPhongsByLoaiPhongAndTinhTrang(loaiPhongId, tinhTrang, pageable);
            model.addAttribute("selectedLoaiPhong", loaiPhongId);
            model.addAttribute("selectedTinhTrang", tinhTrang);
        } else if (loaiPhongId != null) {
            phongPage = phongService.getPhongsByLoaiPhong(loaiPhongId, pageable);
            model.addAttribute("selectedLoaiPhong", loaiPhongId);
        } else if (tinhTrang != null) {
            phongPage = phongService.getPhongsByTinhTrang(tinhTrang, pageable);
            model.addAttribute("selectedTinhTrang", tinhTrang);
        } else {
            phongPage = phongService.getAllPhongs(pageable);
        }


        List<LoaiPhong> loaiPhongs = loaiPhongService.getAllLoaiPhongs();
        List<Tang> tangs = tangService.getAllTangs(); // Lấy danh sách tầng
        List<DienTich> dienTichs = dienTichService.getAllDienTichPhongs(); // Lấy danh sách diện tích

//        long totalAvailablePhongs = phongService.countAvailableActiveRooms();
//        long totalInactivePhongs1 = phongService.countActivePhongs1();
        // Log dữ liệu để kiểm tra
        System.out.println("Phongs: " + phongPage.getContent());

        model.addAttribute("phongs", phongPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", phongPage.getTotalPages());
        model.addAttribute("loaiPhongs", loaiPhongs);
        model.addAttribute("tangs", tangs);
        model.addAttribute("dienTichs", dienTichs);
        model.addAttribute("phong", new Phong());
        model.addAttribute("totalActivePhongs", phongService.countActivePhongs());
        model.addAttribute("totalInactivePhongs1", phongService.countAvailableActiveRooms());
        return "list/QuanLyPhong/phongs";
    }





//    @PostMapping("/add")
//    public String createPhong(@ModelAttribute Phong phong) {
//        phongService.savePhong(phong);
//        return "redirect:/phongs";
//    }
    @GetMapping("/checkMaPhong")
    @ResponseBody
    public Map<String, Boolean> checkMaPhong(@RequestParam String maPhong) {
        boolean exists = phongService.isMaPhongTrung(maPhong);
        return Collections.singletonMap("exists", exists);
    }


    @GetMapping("/add")
    public String showCreatePhongForm(Model model) {
        List<LoaiPhong> loaiPhongs = loaiPhongService.getAllLoaiPhongs();
        model.addAttribute("phong", new Phong());
        model.addAttribute("loaiPhongs", loaiPhongs);
        return "createPhong";
    }

    @PostMapping("/save")
    public String savePhong(@ModelAttribute Phong phong,PhongDTO phongDTO,
                            @RequestParam String action,
                            RedirectAttributes redirectAttributes) {
        if ("create".equals(action)) {
            // Xử lý tạo mới
            if (phong.getTinhTrang() == null) {
                phong.setTinhTrang(false); // Giá trị mặc định
            }
            if (phongService.isMaPhongTrung(phongDTO.getMaPhong())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mã phòng đã tồn tại!");
                return "redirect:/phongs";
            }

            phong.setLoaiPhong(loaiPhongService.getLoaiPhongById(phong.getLoaiPhong().getIdLoaiPhong()).orElse(null));
            phong.setTang(tangService.getTangById(phong.getTang().getIdTang()).orElse(null));
            phong.setDienTich(dienTichService.getDienTichById(phong.getDienTich().getIdDienTich()).orElse(null));

            phongService.savePhong(phong);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm phòng thành công!");
        } else if ("update".equals(action)) {
            // Xử lý cập nhật
            if (phong.getIdPhong() != null) {
                phong.setLoaiPhong(loaiPhongService.getLoaiPhongById(phong.getLoaiPhong().getIdLoaiPhong()).orElse(null));
                phong.setTang(tangService.getTangById(phong.getTang().getIdTang()).orElse(null));
                phong.setDienTich(dienTichService.getDienTichById(phong.getDienTich().getIdDienTich()).orElse(null));

                phongService.updatePhong(phong);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phòng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Phòng không tồn tại.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Action không hợp lệ.");
        }

        return "redirect:/phongs";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Phong phong = phongService.findById(id);
        model.addAttribute("phong", phong);
        model.addAttribute("loaiPhongs", loaiPhongService.getAllLoaiPhongs());
        model.addAttribute("tangs", tangService.getAllTangs());
        model.addAttribute("dienTichs", dienTichService.getAllDienTichPhongs());
        return "list/QuanLyPhong/edit-phong";
    }

    @PostMapping("/update")
    public String updatePhong(@ModelAttribute("phong") Phong phong) {
        phongService.updatePhong(phong);
        return "redirect:/phongs";
    }
//
//    @GetMapping("/phong/{id}/details")
//    @ResponseBody
//    public ResponseEntity<List<ChiTietPhong>> getChiTietPhong(@PathVariable("id") Integer idPhong) {
//        List<ChiTietPhong> chiTietPhong = chiTietPhongService.findByPhongId(idPhong);
//        return ResponseEntity.ok(chiTietPhong);
//    }

//    @PostMapping("/chi-tiet-phong/save")
//    public String saveChiTietPhong(@ModelAttribute ChiTietPhong chiTietPhong) {
//        chiTietPhongService.save(chiTietPhong);
//        return "redirect:/phongs"; // Chuyển hướng lại trang danh sách phòng sau khi lưu
//    }

    @PostMapping("/phongs/update-tinh-trang")
    public ResponseEntity<?> updateTinhTrang(
            @RequestParam Integer idPhong,
            @RequestParam boolean tinhTrang) {
        try {
            // Gọi service để cập nhật trạng thái
            phongService.updateTinhTrang(idPhong, tinhTrang);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Có lỗi xảy ra: " + e.getMessage());
        }
    }


    @PostMapping("/save-tang")
    public ResponseEntity<?> saveTang(@RequestBody Tang tang) {
        try {
            Tang savedTang = tangService.saveTang(tang);
            return ResponseEntity.ok(savedTang);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể thêm tầng mới.");
        }
    }


    @PostMapping("/save-dien-tich")
    public ResponseEntity<?> saveDienTich(@RequestBody DienTich dienTich) {
        try {
            DienTich savedDienTich = dienTichService.saveDienTich(dienTich);
            return ResponseEntity.ok(savedDienTich);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể thêm diện tích mới.");
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<PhongDTO>> search(@RequestParam String query) {
        List<PhongDTO> results = phongService.search(query);
        return ResponseEntity.ok(results);
    }



}

