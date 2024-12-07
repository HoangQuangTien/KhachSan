package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.ThanhToanDTO;

import com.example.DuAnTotNghiepKs.entity.*;
import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import com.example.DuAnTotNghiepKs.repository.ThamSoRepo;
import com.example.DuAnTotNghiepKs.repository.ThanhToanRepo;
import com.example.DuAnTotNghiepKs.service.TaiKhoanService;
import com.example.DuAnTotNghiepKs.service.ThanhToanService;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ThanhToanImp implements ThanhToanService {

    @Autowired
    private ThanhToanRepo thanhToanRepository;

    @Autowired
    private DatPhongRepo datPhongRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ThamSoRepo thamSoRepository;

    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public boolean xacNhanThanhToan(Integer idDatPhong, Double paymentAmount) {
        // Tìm đơn đặt phòng
        DatPhong datPhong = datPhongRepository.findById(idDatPhong).orElse(null);
        if (datPhong == null) {
            return false;
        }

        // Tạo đối tượng ThanhToan
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setDatPhong(datPhong);
        thanhToan.setNgayThanhToan(new Date()); // Ngày thanh toán hiện tại
        thanhToan.setPhuongThuc(paymentAmount >= (datPhong.getTienCoc())); // Giả sử phương thức thanh toán thành công nếu thanh toán >= tiền cọc
        thanhToan.setTinhTrang(true); // Đánh dấu là đã thanh toán

        // Lưu thanh toán vào cơ sở dữ liệu
        thanhToanRepository.save(thanhToan);

        // Cập nhật trạng thái đơn đặt phòng
        datPhong.setTinhTrang("Đã cọc"); // Cập nhật trạng thái
        datPhongRepository.save(datPhong);

        return thanhToan.isPhuongThuc();
    }


    @Override
    public List<ThanhToanDTO> getAll() {
        List<ThanhToan> thanhToans = thanhToanRepository.findAll();
        return thanhToans.stream()
                .map(thanhToan -> modelMapper.map(thanhToan,ThanhToanDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ThanhToanDTO> findAllByTinhTrang(Pageable pageable) {
        Page<ThanhToan> thanhToans = thanhToanRepository.findAllByTinhTrang(pageable);

        return thanhToans.map(thanhToan -> {
            ThanhToanDTO dto = new ThanhToanDTO();

            // Lấy thông tin phòng
            Phong phong = thanhToan.getDatPhong().getPhong();
            if (phong != null) {
                dto.setIdPhong(phong.getIdPhong());
                dto.setTenPhong(phong.getTenPhong()); // Thêm tên phòng vào DTO
            }

            // Lấy thông tin đặt phòng
            DatPhong datPhong = thanhToan.getDatPhong();
            if (datPhong != null) {
                dto.setIdDatPhong(datPhong.getIdDatPhong());
                dto.setTongTien(datPhong.getTongTien());

                // Lấy thông tin khách hàng
                KhachHang khachHang = datPhong.getKhachHang();
                if (khachHang != null) {
                    dto.setIdKhachHang(khachHang.getId()); // Lấy ID khách hàng
                    dto.setHoVaTen(khachHang.getHoVaTen()); // Lấy tên khách hàng vào DTO
                }
            }

            NhanVien nhanVien  = thanhToan.getNhanVien();
            if (nhanVien != null) {
                dto.setIdNhanVien(nhanVien.getIdNhanVien()); // Lấy ID khách hàng
                dto.setHoTen(nhanVien.getHoTen());
            }

            // Lấy các thông tin thanh toán
            dto.setNgayThanhToan(thanhToan.getNgayThanhToan());
            dto.setMaThanhToan(thanhToan.getMaThanhToan());
            dto.setTinhTrang(thanhToan.getTinhTrang());
            dto.setSoTien(thanhToan.getSoTien());

            return dto;
        });
    }





    // ngày hiện tại
    private Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }


    /**
     * Sinh mã duy nhất với ký tự chữ và số.
     */
    public String generateRandomCode() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int CODE_LENGTH = 8; // Độ dài của mã sinh ra
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    @Override
    public ThanhToanDTO save(ThanhToanDTO thanhToanDTO) {
        // Kiểm tra các ID từ DTO
        System.out.println("idDatPhong: " + thanhToanDTO.getIdDatPhong());
        System.out.println("idPhong: " + thanhToanDTO.getIdPhong());
        System.out.println("idKhachHang: " + thanhToanDTO.getIdKhachHang());
        System.out.println("idLoaiPhong: " + thanhToanDTO.getIdLoaiPhong());

        // Tìm đối tượng DatPhong từ idDatPhong
        DatPhong datPhong = datPhongRepository.findById(thanhToanDTO.getIdDatPhong())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt phòng với id: " + thanhToanDTO.getIdDatPhong()));

        LocalDateTime ngayCheckin = datPhong.getNgayCheckIn();
        // Cập nhật trạng thái của DatPhong sau khi thanh toán
        datPhong.setTrangThai(true);
        datPhong.setNgayCheckIn(ngayCheckin);

        // Lưu đối tượng DatPhong đã được cập nhật
        datPhongRepository.save(datPhong);
        System.out.println("Trạng thái DatPhong sau khi cập nhật: " + datPhong.getTrangThai());
        // Lưu thông tin thanh toán
        TaiKhoan taiKhoan = taiKhoanService.getTaiKhoanTuSession1();
        if (taiKhoan == null || taiKhoan.getNhanVien() == null){
            System.out.println("Nhân viên del tồn tại:"+taiKhoan.getNhanVien());
            throw new IllegalArgumentException("Nhân viên không tồn tại"+taiKhoan.getNhanVien());
        }
        NhanVien nhanVien = taiKhoan.getNhanVien();
        // Tạo đối tượng ThanhToan từ DTO
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setNhanVien(nhanVien);
        thanhToan.setDatPhong(datPhong);
        thanhToan.setMaThanhToan(generateRandomCode());
        thanhToan.setNgayThanhToan(getCurrentDate());
        thanhToan.setPhuongThuc(thanhToanDTO.getPhuongThuc());
        thanhToan.setTinhTrang(thanhToanDTO.getTinhTrang());
        thanhToan.setSoTien(thanhToanDTO.getSoTien());

        // Lưu đối tượng ThanhToan vào cơ sở dữ liệu
//        thanhToan.setTinhTrang(false);
        thanhToan = thanhToanRepository.save(thanhToan);

        // Ánh xạ từ Entity trở lại DTO
        ThanhToanDTO resultDTO = new ThanhToanDTO();
        resultDTO.setIdThanhToan(thanhToan.getIdThanhToan());
        resultDTO.setMaThanhToan(thanhToan.getMaThanhToan());
        resultDTO.setNgayThanhToan(thanhToan.getNgayThanhToan());
        resultDTO.setPhuongThuc(thanhToan.isPhuongThuc());
        resultDTO.setSoTien(thanhToan.getSoTien());
        resultDTO.setTinhTrang(thanhToan.getTinhTrang());

        // Ánh xạ thông tin DatPhong và Phong liên quan
        resultDTO.setIdDatPhong(datPhong.getIdDatPhong());
        if (datPhong.getPhong() != null) {
            resultDTO.setIdPhong(datPhong.getPhong().getIdPhong());
            resultDTO.setTenPhong(datPhong.getPhong().getTenPhong());

            resultDTO.setNgayCheckIn(datPhong.getNgayCheckIn());
        }
        if (datPhong.getLoaiPhong() != null) {
            resultDTO.setIdLoaiPhong(datPhong.getLoaiPhong().getIdLoaiPhong());
        }
        resultDTO.setNgayNhan(datPhong.getNgayNhan());
        resultDTO.setNgayTra(datPhong.getNgayTra());
        resultDTO.setTienCoc(datPhong.getTienCoc());
        resultDTO.setTongTien(datPhong.getTongTien());


// Ánh xạ thông tin KhachHang liên quan
        KhachHang khachHang = datPhong.getKhachHang();
        if (khachHang != null) {
            resultDTO.setHoVaTen(khachHang.getHoVaTen());
            resultDTO.setSoDienThoai(khachHang.getSoDienThoai());
            resultDTO.setEmail(khachHang.getEmail());
        } else {
            System.out.println("Khách hàng không tồn tại.");
        }

        return resultDTO;
    }

    @Override
    public Page<ThanhToan> getLoaiPhongPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return thanhToanRepository.findAll(pageable);
    }






    // Các phương thức khác...

    @Override
    public long getThoiGianChoPhepChuyenTrangThai() {
        Long idThamSo = 2L; // ID cho tham số thời gian chuyển trạng thái
        return thamSoRepository.findById(idThamSo)
                .map(thamSo -> Long.parseLong(thamSo.getGiaTri()) * 60 * 1000) // Chuyển đổi phút thành mili giây
                .orElse(0L); // Trả về 0 nếu không tìm thấy tham số
    }


    @Override
    public byte[] createInvoicePDF(ThanhToanDTO thanhToanDTO) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        // Font và tiêu đề
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // Tiêu đề hóa đơn
        document.add(new Paragraph("HÓA ĐƠN THANH TOÁN")
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Thông tin khách hàng
        document.add(new Paragraph("Thông tin khách hàng")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginBottom(10));

        Table customerTable = new Table(2);
        customerTable.setWidth(UnitValue.createPercentValue(100));
        customerTable.addCell(new Cell().add(new Paragraph("Họ và tên:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph(thanhToanDTO.getHoVaTen()).setFont(regularFont)).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph("Phòng:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph(thanhToanDTO.getTenPhong()).setFont(regularFont)).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph("Ngày thanh toán:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph(String.valueOf(thanhToanDTO.getNgayThanhToan())).setFont(regularFont)).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph("Nhân viên:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        customerTable.addCell(new Cell().add(new Paragraph(thanhToanDTO.getHoTen()).setFont(regularFont)).setBorder(Border.NO_BORDER));
        document.add(customerTable.setMarginBottom(20));

        // Thông tin thanh toán
        document.add(new Paragraph("Chi tiết thanh toán")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginBottom(10));

        Table paymentTable = new Table(2);
        paymentTable.setWidth(UnitValue.createPercentValue(100));
        paymentTable.addCell(new Cell().add(new Paragraph("Tổng tiền:").setFont(boldFont)).setBorder(Border.NO_BORDER));
        paymentTable.addCell(new Cell().add(new Paragraph(formatCurrency(thanhToanDTO.getTongTien())).setFont(regularFont)).setBorder(Border.NO_BORDER));
        document.add(paymentTable.setMarginBottom(20));

        // Thêm thông điệp cảm ơn
        document.add(new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi!")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(regularFont)
                .setFontSize(12)
                .setMarginTop(30));

        document.close();
        return outputStream.toByteArray();
    }

    // Hàm định dạng số tiền
    private String formatCurrency(double amount) {
        DecimalFormat decimalFormat = new DecimalFormat("#,### VND");
        return decimalFormat.format(amount);
    }




    @Override
    public List<ThanhToanDTO> search(String query, Date ngayThanhToan) {
        List<ThanhToan> results = thanhToanRepository.searchByKhachHangOrNhanVienAndNgayThanhToan(query, ngayThanhToan);
        return results.stream()
                .map(thanhToan -> modelMapper.map(thanhToan, ThanhToanDTO.class))
                .collect(Collectors.toList());
    }



    @Override
    public ThanhToanDTO getThanhToanById(Integer id) {
        // Truy vấn thanh toán từ repository theo ID
        Optional<ThanhToan> thanhToanOptional = thanhToanRepository.findById(id);

        // Nếu không tìm thấy, trả về null hoặc có thể ném exception tùy vào logic bạn muốn
        if (thanhToanOptional.isPresent()) {
            ThanhToan thanhToan = thanhToanOptional.get();

            // Chuyển đổi entity ThanhToan thành DTO
            ThanhToanDTO thanhToanDTO = new ThanhToanDTO();
            thanhToanDTO.setIdThanhToan(thanhToan.getIdThanhToan());
            thanhToanDTO.setHoVaTen(thanhToan.getDatPhong().getKhachHang().getHoVaTen());
            thanhToanDTO.setTenPhong(thanhToan.getDatPhong().getPhong().getTenPhong());
            thanhToanDTO.setTongTien(thanhToan.getDatPhong().getTongTien());
            thanhToanDTO.setNgayThanhToan(thanhToan.getNgayThanhToan());
            thanhToanDTO.setHoTen(thanhToan.getNhanVien().getHoTen());

            // Trả về DTO
            return thanhToanDTO;
        } else {
            return null; // Hoặc ném exception nếu bạn muốn
        }
    }



    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // true để gửi email dưới dạng HTML
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu cần
        }
    }


}

