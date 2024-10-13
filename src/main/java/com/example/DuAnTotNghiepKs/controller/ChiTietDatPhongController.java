package com.example.DuAnTotNghiepKs.controller;

import com.example.DuAnTotNghiepKs.DTO.CustomerDTO;
import com.example.DuAnTotNghiepKs.entity.*;
import com.example.DuAnTotNghiepKs.repository.*;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private LichSuDatPhongRepo lichSuDatPhongRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private NhanVienRepo nhanVienRepo;

    @GetMapping
    public String index(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "size", defaultValue = "5") int pageSize,
            @RequestParam(name = "startDate", defaultValue = "") String startDateStr,
            @RequestParam(name = "endDate", defaultValue = "") String endDateStr,
            @RequestParam(defaultValue = "", name = "keyword") String keyword,
            @RequestParam(name = "tinhTrang", defaultValue = "") String tinhTrangStr,
            Model model
    ) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        String errorMessage = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        // Xử lý ngày tháng
        try {
            if (!startDateStr.isEmpty()) {
                startDate = LocalDateTime.parse(startDateStr + "T00:00", formatter);
            }
            if (!endDateStr.isEmpty()) {
                endDate = LocalDateTime.parse(endDateStr + "T23:59", formatter);
            }
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                errorMessage = "Ngày bắt đầu không được lớn hơn ngày kết thúc.";
                startDate = null;
                endDate = null;
            }
        } catch (DateTimeParseException e) {
            errorMessage = "Ngày tháng không hợp lệ. Vui lòng nhập đúng định dạng yyyy-MM-dd.";
        }

        List<DatPhong> datPhongs = new ArrayList<>();
        List<ChiTietDatPhong> chiTietDatPhongs = new ArrayList<>();

        if (errorMessage == null) {
            // Lọc theo ngày nhận nếu có
            if (startDate != null && endDate != null) {
                datPhongs = datPhongRepo.findByNgayNhanBetween(startDate, endDate);
            } else {
                datPhongs = datPhongRepo.findAll();
            }

            // Lọc theo tình trạng nếu có
            if (!tinhTrangStr.isEmpty()) {
                datPhongs = datPhongs.stream()
                        .filter(datPhong -> datPhong.getTinhTrang().equals(tinhTrangStr))
                        .collect(Collectors.toList());
            }

            // Tìm kiếm theo từ khóa
            if (!keyword.isEmpty()) {
                datPhongs = datPhongRepo.findByKeyword(keyword);
            }

            // Lấy chi tiết đặt phòng từ danh sách đặt phòng
            chiTietDatPhongs = chiTietDatPhongRepo.findByDatPhongIn(datPhongs);

//            // Đặt lại thời gian cho ngày nhận và trả
//            datPhongs.forEach(datPhong -> {
//                if (datPhong.getNgayNhan() != null) {
//                    datPhong.setNgayNhan(setDefaultTimeTo7AM(datPhong.getNgayNhan()));
//                }
//                if (datPhong.getNgayTra() != null) {
//                    datPhong.setNgayTra(setDefaultTimeTo7AM(datPhong.getNgayTra()));
//                }
//            });

            List<KhachHang> khachHangs = khachHangRepository.findAll();
            List<Phong> phongs = phongRepo.findAll();
//            List<NhanVien> nhanViens = nhanVienRepo.findAll();

            int startItem = (pageNo - 1) * pageSize;
            List<ChiTietDatPhong> paginatedChiTietDatPhongs = chiTietDatPhongs.stream()
                    .skip(startItem)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            int totalItems = chiTietDatPhongs.size();
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);

            // Thêm các thuộc tính vào model
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

        // Thêm các thuộc tính cần thiết vào model
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);
        model.addAttribute("tinhTrang", tinhTrangStr);
        model.addAttribute("keyword", keyword);
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
            model.addAttribute("phong", phong); // Đảm bảo phong đã được lấy đầy đủ thông tin

            List<Phong> allRooms = phongService.getAllPhongs1();
            model.addAttribute("allRooms", allRooms);

            List<LoaiPhong> loaiPhongs = loaiPhongService.getAllLoaiPhongs();
            model.addAttribute("loaiPhongs", loaiPhongs);
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy phòng với ID: " + id);
            return "error"; // Xử lý lỗi nếu không tìm thấy phòng
        }

        return "list/QuanLyDatPhong/edit-phong"; // Trả về trang chỉnh sửa phòng
    }


    @PostMapping("/update-room")
    public ResponseEntity<?> updateRoom(
            @RequestParam("roomId") Integer roomId,
            @RequestParam("tenPhong") Integer tenPhongId, // ID của phòng mới được chọn
            @RequestParam("lyDoThayDoi") String lyDoThayDoi) {

        try {
            // Tìm phòng hiện tại để cập nhật
            Phong phongHienTai = phongService.getPhongById(roomId)
                    .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

            // Kiểm tra xem phòng hiện tại có đặt phòng nào không
            Optional<DatPhong> datPhongOpt = datPhongRepo.findByPhong(phongHienTai);
            if (datPhongOpt.isPresent()) {
                DatPhong datPhong = datPhongOpt.get();

                // Kiểm tra nếu trạng thái là "Đã Checkin" hoặc "Đã Hủy"
                if ("Đã Checkin".equals(datPhong.getTinhTrang())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "message", "Không thể đổi phòng vì phòng đã được check-in."));
                }
                if ("Đã Hủy".equals(datPhong.getTinhTrang())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("success", false, "message", "Không thể đổi phòng vì phòng đã bị hủy."));
                }
            }

            // Tìm phòng mới từ ID phòng đã chọn
            Phong phongMoi = phongService.getPhongById(tenPhongId)
                    .orElseThrow(() -> new RuntimeException("Phòng không tồn tại với ID: " + tenPhongId));

            // Kiểm tra xem phòng mới có giá thấp hơn phòng hiện tại không
            if (phongMoi.getGia() < phongHienTai.getGia()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Không thể đổi sang phòng có giá thấp hơn phòng hiện tại."));
            }

            // Kiểm tra xem phòng mới có đang được đặt không
            boolean isPhongMoiBooked = datPhongRepo.findByPhong(phongMoi).isPresent();
            if (isPhongMoiBooked) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Phòng mới đang được đặt. Không thể chuyển sang phòng này."));
            }

            // Cập nhật thông tin phòng trong đặt phòng
            if (datPhongOpt.isPresent()) {
                DatPhong datPhong = datPhongOpt.get();
                datPhong.setPhong(phongMoi); // Thay đổi phòng trong đối tượng DatPhong
                datPhongRepo.save(datPhong); // Lưu lại thay đổi
            }

            // Lưu lịch sử đặt phòng
            LichSuDatPhong lichSuDatPhong = new LichSuDatPhong();
            lichSuDatPhong.setDatPhong(datPhongOpt.get());
            lichSuDatPhong.setChiTietThayDoi("Đổi từ " + phongHienTai.getTenPhong() + " sang phòng " + phongMoi.getTenPhong() + ". Lý do: " + lyDoThayDoi);
            lichSuDatPhong.setThoiGianThayDoi(new Date());
            lichSuDatPhongRepo.save(lichSuDatPhong);

            return ResponseEntity.ok(Map.of("success", true, "message", "Đã chuyển phòng thành công"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Đã xảy ra lỗi: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Đã xảy ra lỗi không mong muốn: " + e.getMessage()));
        }
    }

}


