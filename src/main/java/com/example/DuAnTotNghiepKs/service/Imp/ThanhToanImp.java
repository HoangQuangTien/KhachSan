package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.ThanhToanDTO;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.KhachHang;
import com.example.DuAnTotNghiepKs.entity.ThanhToan;
import com.example.DuAnTotNghiepKs.repository.DatPhongRepo;
import com.example.DuAnTotNghiepKs.repository.ThamSoRepo;
import com.example.DuAnTotNghiepKs.repository.ThanhToanRepo;
import com.example.DuAnTotNghiepKs.service.ThanhToanService;
import com.itextpdf.kernel.font.PdfFontFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

        // Tạo đối tượng ThanhToan từ DTO
        ThanhToan thanhToan = new ThanhToan();
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

        // Thêm nội dung hóa đơn vào PDF
        document.add(new Paragraph("Hóa Đơn Thanh Toán")
                .setFontSize(20)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))); // Sử dụng PdfFontFactory

        document.add(new Paragraph("Thông tin khách hàng: " + thanhToanDTO.getIdKhachHang()));
        document.add(new Paragraph("Mã phòng: " + thanhToanDTO.getIdPhong()));
        document.add(new Paragraph("Tổng tiền: " + thanhToanDTO.getTongTien()));
        document.add(new Paragraph("Ngày thanh toán: " + thanhToanDTO.getNgayThanhToan()));

        document.close();
        return outputStream.toByteArray(); // Trả về byte array của PDF
    }







}

