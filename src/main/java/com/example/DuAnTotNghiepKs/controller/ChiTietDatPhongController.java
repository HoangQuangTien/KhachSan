package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.CustomerDTO;
import com.example.DuAnTotNghiepKs.entity.*;
import com.example.DuAnTotNghiepKs.repository.ChiTietDatPhongRepo;
import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import com.example.DuAnTotNghiepKs.repository.KhachHangRepository;
import com.example.DuAnTotNghiepKs.repository.PhongRepo;
import com.example.DuAnTotNghiepKs.service.*;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/danhsachdatphong")
public class ChiTietDatPhongController {

    @Autowired
    private ChiTietDatPhongRepo chiTietDatPhongRepo;
    @Autowired
    private DatPhongRepo datPhongRepo;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private PhongRepo phongRepo;

    @Autowired
    private PhongService phongService;

    @Autowired
    private LoaiPhongService loaiPhongService;

    @Autowired
    private QrCodeService qrCodeService;
    @Autowired
    private ChiTietDatPhongService chiTietDatPhongService;
    @Autowired
    private DatPhongService datPhongService;
    @Autowired
    private EmailService emailService;
    @GetMapping
    public String index(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "limit", defaultValue = "10") int pageSize,
            @RequestParam(name = "startDate", defaultValue = "") String startDateStr,
            @RequestParam(name = "endDate", defaultValue = "") String endDateStr,
            @RequestParam(name = "search", defaultValue = "false") boolean search,
            Model model
    ) {
        Date startDate = null;
        Date endDate = null;
        String errorMessage = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        try {
            if (!startDateStr.isEmpty()) {
                startDate = dateFormat.parse(startDateStr);
            }
            if (!endDateStr.isEmpty()) {
                endDate = dateFormat.parse(endDateStr);
            }
            if (startDate != null && endDate != null && startDate.after(endDate)) {
                errorMessage = "Ngày bắt đầu không được lớn hơn ngày kết thúc.";
                startDate = null;
                endDate = null;
            }
        } catch (ParseException e) {
            errorMessage = "Ngày tháng không hợp lệ. Vui lòng nhập đúng định dạng yyyy-MM-dd.";
        }

        List<DatPhong> datPhongs;
        List<ChiTietDatPhong> chiTietDatPhongs;
        List<KhachHang> khachHangs;
        List<Phong> phongs;

        if (errorMessage == null) {
            if (search) {
                if (startDate == null || endDate == null) {
                    errorMessage = "Vui lòng nhập đầy đủ ngày bắt đầu và ngày kết thúc.";
                    datPhongs = List.of();
                    chiTietDatPhongs = List.of();
                } else {
                    datPhongs = datPhongRepo.findByNgayNhanBetween(startDate, endDate);
                    chiTietDatPhongs = chiTietDatPhongRepo.findByDatPhongIn(datPhongs);
                }
            } else {
                datPhongs = datPhongRepo.findAll();
                chiTietDatPhongs = chiTietDatPhongRepo.findAll();
            }
            datPhongs.forEach(datPhong -> {
                if (datPhong.getNgayNhan() != null) {
                    datPhong.setNgayNhan(setDefaultTimeTo7AM(datPhong.getNgayNhan()));
                }
                if (datPhong.getNgayTra() != null) {
                    datPhong.setNgayTra(setDefaultTimeTo7AM(datPhong.getNgayTra()));
                }
            });
            // Lấy danh sách khách hàng và phòng
            khachHangs = khachHangRepository.findAll();
            phongs = phongRepo.findAll();
            // Phân trang chi tiết đặt phòng
            int startItem = (pageNo - 1) * pageSize;
            List<ChiTietDatPhong> paginatedChiTietDatPhongs = chiTietDatPhongs.stream()
                    .skip(startItem)
                    .limit(pageSize)
                    .toList();

            int totalItems = chiTietDatPhongs.size();
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);

            model.addAttribute("data", paginatedChiTietDatPhongs);
            model.addAttribute("datPhongs", datPhongs);
            model.addAttribute("khachHangs", khachHangs);
            model.addAttribute("phongs", phongs);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
        } else {
            model.addAttribute("data", List.of());
            model.addAttribute("datPhongs", List.of());
            model.addAttribute("khachHangs", List.of());
            model.addAttribute("phongs", List.of());
        }

        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);
        model.addAttribute("errorMessage", errorMessage);

        return "list/QuanLyDatPhong/danhsachdatphong";
    }

    @GetMapping("/qrcode/{id}")
    public ResponseEntity<byte[]> getQRCode(@PathVariable("id") Integer id) {
        ChiTietDatPhong chiTietDatPhong = chiTietDatPhongService.findById(id);
        String qrData = String.format(
                "Mã CTDP: %s\nTên Nhân Viên: %s\nTên Khách Hàng: %s\nTên Phòng: %s\nNgày Nhận Phòng: %s\nNgày Trả Phòng: %s\nTổng Tiền: %s",
                chiTietDatPhong.getMaChiTietDatPhong(),
//                chiTietDatPhong.getNhanVien().getHoTen(),
                chiTietDatPhong.getKhachHang().getHoVaTen(),
                chiTietDatPhong.getDatPhong().getPhong().getTenPhong(),
                chiTietDatPhong.getDatPhong().getNgayNhan(),
                chiTietDatPhong.getDatPhong().getNgayTra(),
                chiTietDatPhong.getTongTien()
        );

        try {
            byte[] qrCodeImage = qrCodeService.generateQRCode(qrData, 300, 300);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/danhsachdatphong/sua-khach-hang/{id}")
    public String editKhachHang(@PathVariable("id") Integer id, Model model) {
        KhachHang khachHang = khachHangRepository.findById(id).orElse(null);
        if (khachHang == null) {
            return "redirect:/danhsachdatphong";
        }
        model.addAttribute("khachHang", khachHang);
        return "list/QuanlyDatPhong/edit_khach_hang";
    }
    @PostMapping("/update-customer")
    @ResponseBody
    public ResponseEntity<?> updateCustomer(
            @ModelAttribute("customerDTO") @Valid CustomerDTO customerDTO,
            BindingResult result,
            @RequestParam("customerId") Integer customerId) {

        if (result.hasErrors()) {
            // Return error response
            String errorMessage = result.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", errorMessage));
        }

        try {
            KhachHang khachHang = khachHangRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

            khachHang.setHoVaTen(customerDTO.getTenKhachHang());
            khachHang.setSoDienThoai(customerDTO.getSoDienThoai());
            khachHang.setEmail(customerDTO.getEmail());

            khachHangRepository.save(khachHang);

            return ResponseEntity.ok(Map.of("success", true, "message", "Cập nhật thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Đã xảy ra lỗi: " + e.getMessage()));
        }

    }

    @GetMapping("/get-customer/{id}")
    @ResponseBody
    public KhachHang getCustomer(@PathVariable Integer id) {
        return khachHangRepository.findById(id).orElse(null); // Trả về null nếu không tìm thấy khách hàng
    }

    @PostMapping("/gui-email")
    public String sendEmail(@RequestParam("chiTietDatPhongId") Integer chiTietDatPhongId, RedirectAttributes redirectAttributes) {
        System.out.println("ID chi tiết đặt phòng: " + chiTietDatPhongId); // Debug ID
        try {
            // Retrieve ChiTietDatPhong from the database
            ChiTietDatPhong ctdp = chiTietDatPhongService.findById(chiTietDatPhongId);
            System.out.println("ChiTietDatPhong retrieved: " + ctdp); // Debug object

            if (ctdp == null) {
                System.out.println("Chi tiết đặt phòng không được tìm thấy.");
                redirectAttributes.addFlashAttribute("message", "Chi tiết đặt phòng không được tìm thấy.");
                return "redirect:/danhsachdatphong";
            }

            // Get the customer's email from ChiTietDatPhong
            String email = ctdp.getKhachHang().getEmail();
            if (email == null || email.isEmpty()) {
                System.out.println("Không tìm thấy địa chỉ email của khách hàng.");
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy địa chỉ email của khách hàng.");
                return "redirect:/danhsachdatphong";
            }

            // Send email with booking details
            emailService.sendEmail(email, "Chi Tiết Đặt Phòng", "Nội dung email", ctdp);
            System.out.println("Gửi email thành công."); // Debug success message

            // Add success message to RedirectAttributes
            redirectAttributes.addFlashAttribute("message", "Gửi email thành công.");
        } catch (MessagingException | IOException e) {
            System.out.println("Gửi email thất bại: " + e.getMessage()); // Debug error message
            // Add error message to RedirectAttributes
            redirectAttributes.addFlashAttribute("message", "Gửi email thất bại: " + e.getMessage());
        }
        // Redirect to /danhsachdatphong and display the message
        return "redirect:/danhsachdatphong";
    }



    @GetMapping("/xuat-excel")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        List<ChiTietDatPhong> chiTietDatPhongs = chiTietDatPhongRepo.findAll(); // Retrieve all data

        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Chi Tiết Đặt Phòng");

        // Create column headers
        String[] columnNames = {"Mã CTDP", "Tên Nhân Viên", "Tên Khách Hàng", "Tên Phòng", "Ngày Nhận Phòng", "Ngày Trả Phòng", "Tổng Tiền"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnNames[i]);
        }

        int rowNum = 1;
        for (ChiTietDatPhong ctdp : chiTietDatPhongs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(ctdp.getMaChiTietDatPhong());
//            row.createCell(1).setCellValue(ctdp.getNhanVien().getHoTen());
            row.createCell(2).setCellValue(ctdp.getKhachHang().getHoVaTen());
            row.createCell(3).setCellValue(ctdp.getDatPhong().getPhong().getTenPhong());
            row.createCell(4).setCellValue(ctdp.getDatPhong().getNgayNhan().toString());
            row.createCell(5).setCellValue(ctdp.getDatPhong().getNgayTra().toString());
            row.createCell(6).setCellValue(ctdp.getTongTien().toString());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=chitietdatphong.xlsx");

        return ResponseEntity.ok().headers(headers).body(out.toByteArray());
    }
    private Date setDefaultTimeTo7AM(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 7);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


//    @GetMapping("/edit-room/{id}")
//    public String showEditRoomForm(@PathVariable("id") Integer id, Model model) {
//        Optional<Phong> optionalPhong = phongService.getPhongById(id);
//        if (optionalPhong.isPresent()) {
//            Phong phong = optionalPhong.get();
//            model.addAttribute("phong", phong);
//            model.addAttribute("loaiPhongs", .getAllLoaiPhongs());
//            model.addAttribute("tangs", tangService.getAllTangs());
//        } else {
//            model.addAttribute("errorMessage", "Không tìm thấy phòng với ID: " + id);
//            return "error"; // Trả về trang lỗi nếu phòng không tìm thấy
//        }
//        return "list/QuanLyDatPhong/edit-phong";
//    }
//
//
//    @PostMapping("/update-room")
//    public ResponseEntity<?> updateRoom(
//            @RequestParam("roomId") Integer roomId,
//            @RequestParam("tenPhong") String tenPhong,
//            @RequestParam("loaiPhong") Integer loaiPhongId,
//            @RequestParam("idTang") Integer idTang,
//            @RequestParam("gia") Float gia) {
//
//        try {
//            Phong phong = phongService.getPhongById(roomId)
//                    .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
//            if (gia < phong.getGia()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(Map.of("success", false, "message", "Giá mới phải lớn hơn hoặc bằng giá hiện tại"));
//            }
//            phong.setTenPhong(tenPhong);
//            phong.setGia(gia);
//            LoaiPhong loaiPhong = loaiPhongService.findById1(loaiPhongId)
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + loaiPhongId));
//            phong.setLoaiPhong(loaiPhong);
//            Tang tang = tangService.getTangById(idTang)
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tầng với ID: " + idTang));
//            phong.setTang(tang);
//            phongService.savePhong(phong);
//            return ResponseEntity.status(HttpStatus.OK)
//                    .body(Map.of("success", true, "message", "Phòng đã được cập nhật thành công"));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("success", false, "message", "Đã xảy ra lỗi: " + e.getMessage()));
//        }
//    }

    @GetMapping("/edit-room/{id}")
    public String showEditRoomForm(@PathVariable("id") Integer id, Model model) {
        Optional<Phong> optionalPhong = phongService.getPhongById(id);
        if (optionalPhong.isPresent()) {
            Phong phong = optionalPhong.get();
            model.addAttribute("phong", phong);  // Truyền đúng phong

            List<Phong> allRooms = phongService.getAllPhongs1(); // Lấy đúng allRooms
            model.addAttribute("allRooms", allRooms);  // Đảm bảo allRooms chứa tên phòng

            List<LoaiPhong> loaiPhongs = loaiPhongService.getAllLoaiPhongs();
            model.addAttribute("loaiPhongs", loaiPhongs);
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy phòng với ID: " + id);
            return "error";
        }
        return "list/QuanLyDatPhong/edit-phong";
    }


    @PostMapping("/update-room")
    public ResponseEntity<?> updateRoom(
            @RequestParam("roomId") Integer roomId,
            @RequestParam("tenPhong") String tenPhong,
            @RequestParam("loaiPhong") Integer loaiPhongId) {

        try {
            if (tenPhong == null || tenPhong.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Tên phòng không được để trống."));
            }

            Phong phong = phongService.getPhongById(roomId)
                    .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

            Optional<Phong> existingPhong = phongRepo.findByTenPhong(tenPhong);
            if (existingPhong.isPresent() && !existingPhong.get().getIdPhong().equals(roomId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Tên phòng đã tồn tại, vui lòng chọn tên khác."));
            }

            // Kiểm tra trạng thái phòng xem có đặt phòng nào đã check-in chưa
            Optional<DatPhong> datPhongOpt = datPhongRepo.findByPhongAndTinhTrang(phong, true);
            if (datPhongOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Phòng đã được check-in, không thể sửa đổi."));
            }

            LoaiPhong newLoaiPhong = loaiPhongService.findById(loaiPhongId);
            if (newLoaiPhong == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Không tìm thấy loại phòng với ID: " + loaiPhongId));
            }

            // Đảm bảo rằng giá loại phòng mới bằng hoặc lớn hơn giá hiện tại
            if (newLoaiPhong.getGia() < phong.getGia()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Loại phòng mới phải có giá bằng hoặc lớn hơn giá hiện tại"));
            }

            phong.setTenPhong(tenPhong);
            phong.setLoaiPhong(newLoaiPhong);
            phong.setGia(newLoaiPhong.getGia());
            Phong updatedPhong = phongService.savePhong(phong);
            if (updatedPhong == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Không thể lưu phòng đã được cập nhật"));
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("success", true, "message", "Phòng đã được cập nhật thành công"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Đã xảy ra lỗi: " + e.getMessage()));
        }
    }

}



