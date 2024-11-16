package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.DTO.NhanVienDTO;
import com.example.DuAnTotNghiepKs.DTO.TaiKhoanDTO;
import com.example.DuAnTotNghiepKs.entity.ChiTietVaiTro;
import com.example.DuAnTotNghiepKs.entity.NhanVien;
import com.example.DuAnTotNghiepKs.entity.TaiKhoan;
import com.example.DuAnTotNghiepKs.entity.VaiTro;
import com.example.DuAnTotNghiepKs.repository.ChiTietVaiTroRepo;
import com.example.DuAnTotNghiepKs.repository.NhanVienRepo;
import com.example.DuAnTotNghiepKs.repository.TaiKhoanRepo;
import com.example.DuAnTotNghiepKs.repository.VaiTroRepo;
import jakarta.mail.MessagingException;
import jakarta.persistence.Tuple;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepo nhanVienRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private TaiKhoanRepo taiKhoanRepo;
    @Autowired
    private VaiTroRepo vaiTroRepo;

    @Autowired
    private ChiTietVaiTroRepo chiTietVaiTroRepo;

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
    public List<NhanVienDTO> findAll(){
        List<NhanVien> nhanViens = nhanVienRepo.findAll();
        return nhanViens.stream().map(nhanVien -> modelMapper.map(nhanVien,NhanVienDTO.class))
                .collect(Collectors.toList());
    }

    public Page<NhanVienDTO> getAll(Pageable pageable) {
        Page<NhanVien> nhanViens = nhanVienRepo.findAll(pageable);
        return nhanViens.map(nhanVien -> {
            NhanVienDTO dto = modelMapper.map(nhanVien, NhanVienDTO.class);

            if (nhanVien.getTaiKhoan() != null) {
                TaiKhoanDTO taiKhoanDTO = new TaiKhoanDTO();
                taiKhoanDTO.setTenDangNhap(nhanVien.getTaiKhoan().getTenDangNhap());
                taiKhoanDTO.setMatKhau(nhanVien.getTaiKhoan().getMatKhau());
                dto.setTaiKhoanDTO(taiKhoanDTO);
            } else {
                // Xử lý trường hợp không có tài khoản
                dto.setTaiKhoanDTO(new TaiKhoanDTO());
            }

            System.out.println("ID Nhân Viên: " + nhanVien.getIdNhanVien() + ", Tên Đăng Nhập: " + nhanVien.getTaiKhoan());
            return dto;
        });
    }

    //filter
    public Page<NhanVienDTO> filterTrangThai(Boolean trangThai,Pageable pageable){
        Page<NhanVien> nhanViens = nhanVienRepo.filterTrangThai(trangThai,pageable);
        return nhanViens.map(nhanVien -> modelMapper.map(nhanVien,NhanVienDTO.class));
    }



    //search
    public Page<NhanVienDTO> searchEmployees(String keyword,Pageable pageable) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idNhanVien"));
        Page<NhanVien> nhanViens = nhanVienRepo.searchEmployees(keyword,pageable);
        return nhanViens
                .map(nhanVien -> modelMapper.map(nhanVien,NhanVienDTO.class));
    }






    public Optional<NhanVien> findById(Integer id){
        Optional<NhanVien> nhanVien = nhanVienRepo.findById(id);
        if (!nhanVien.isPresent()){
            throw new RuntimeException("Nhân viên không tồn tại: "+id);
        }
        return nhanVien;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public NhanVienDTO update(NhanVienDTO nhanVienDTO) {
        // Chuyển đổi nhanVienDTO sang NhanVien
        NhanVien nhanVien = modelMapper.map(nhanVienDTO, NhanVien.class);

        // Lưu tài khoản

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(nhanVienDTO.getTaiKhoanDTO().getTenDangNhap());
        taiKhoan.setMatKhau(nhanVienDTO.getTaiKhoanDTO().getMatKhau());
        System.out.println("TenDangNhap: " + taiKhoan.getTenDangNhap());
        taiKhoan = taiKhoanRepo.save(taiKhoan);
        System.out.println("Saved TaiKhoan: " + taiKhoan);

        // Gán tài khoản vào nhân viên
        nhanVien.setTaiKhoan(taiKhoan);
        System.out.println("NhanVien before save: " + nhanVien);

        // Lưu nhân viên
        nhanVien = nhanVienRepo.save(nhanVien);
        System.out.println("Saved NhanVien: " + nhanVien);

        return modelMapper.map(nhanVien, NhanVienDTO.class);
    }

    public NhanVienDTO save(NhanVienDTO nhanVienDTO) {
        // Chuyển đổi nhanVienDTO sang NhanVien
        NhanVien nhanVien = modelMapper.map(nhanVienDTO, NhanVien.class);

        // Lưu tài khoản

        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(nhanVienDTO.getTaiKhoanDTO().getTenDangNhap());
        taiKhoan.setMatKhau(passwordEncoder.encode(nhanVienDTO.getTaiKhoanDTO().getMatKhau()));
        System.out.println("TenDangNhap: " + taiKhoan.getTenDangNhap());
        taiKhoan = taiKhoanRepo.save(taiKhoan);
        System.out.println("Saved TaiKhoan: " + taiKhoan);


        // Gán tài khoản vào nhân viên
        nhanVien.setTaiKhoan(taiKhoan);
        System.out.println("NhanVien before save: " + nhanVien);

        //tạo vai trò
        VaiTro vaiTro = vaiTroRepo.findByIdVaiTro(2);
        //tạo chi tiết vai trò
        ChiTietVaiTro chiTietVaiTro = new ChiTietVaiTro();
        chiTietVaiTro.setMaChoTietVaiTro(generateMaChiTietVaiTro());
        chiTietVaiTro.setVaiTro(vaiTro);
        chiTietVaiTro.setTaiKhoan(taiKhoan);
        chiTietVaiTroRepo.save(chiTietVaiTro);

        // Lưu nhân viên
        nhanVien = nhanVienRepo.save(nhanVien);
        System.out.println("Saved NhanVien: " + nhanVien);

        return modelMapper.map(nhanVien, NhanVienDTO.class);
    }

    //genmaChiTietVaiTro
    private String generateMaChiTietVaiTro() {
        String prefix = "CTVT";
        ChiTietVaiTro lastCTVT = chiTietVaiTroRepo.findTopByOrderByIdChiTietVaiTroDesc();
        int nextId = lastCTVT != null ? (int) (lastCTVT.getIdChiTietVaiTro() + 1) : 1;
        return prefix + String.format("%03d", nextId);
    }



    //validate add
    public boolean isEmailExists(String email) {
        return nhanVienRepo.existsByEmail(email); // Phương thức kiểm tra email tồn tại
    }

    public boolean isPhoneNumberExists(String soDienThoai) {
        return nhanVienRepo.existsBySoDienThoai(soDienThoai); // Phương thức kiểm tra số điện thoại tồn tại
    }


    //validate update
    public NhanVienDTO findByEmail(String email) {
        List<Tuple> tuples = nhanVienRepo.findByEmail(email);
        if (!tuples.isEmpty()) {
            Tuple tuple = tuples.get(0);
            NhanVienDTO dto = new NhanVienDTO();
            // Giả sử bạn cần ánh xạ các trường
            dto.setIdNhanVien(tuple.get(0, Integer.class));
            dto.setEmail(tuple.get(1, String.class));
            // Tiếp tục ánh xạ các trường khác
            return dto;
        }
        return null;
    }


    public NhanVienDTO findByPhoneNumber(String soDienThoai) {
        List<Tuple> tuples = nhanVienRepo.findBySoDienThoai(soDienThoai);
        if (!tuples.isEmpty()) {
            Tuple tuple = tuples.get(0);
            NhanVienDTO dto = new NhanVienDTO();
            // Giả sử bạn cần ánh xạ các trường
            dto.setIdNhanVien(tuple.get(0, Integer.class));
            dto.setSoDienThoai(tuple.get(2, String.class));
            // Tiếp tục ánh xạ các trường khác
            return dto;
        }
        return null;
    }


    public NhanVien getOne(Integer id){
        return nhanVienRepo.getOne(id);
    }






    // Phương thức tìm kiếm nhân viên theo tên
    public List<NhanVienDTO> findByName(String name) {
        return nhanVienRepo.findByHoTenContaining(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private NhanVienDTO convertToDTO(NhanVien nhanVien) {
        if (nhanVien == null) {
            return null; // Trả về null nếu đối tượng là null
        }

        return NhanVienDTO.builder()
                .maNhanVien(nhanVien.getMaNhanVien())
                .hoTen(nhanVien.getHoTen())
                .ngaySinh(nhanVien.getNgaySinh())
                .gioiTinh(nhanVien.getGioiTinh())
                .soDienThoai(nhanVien.getSoDienThoai())
                .email(nhanVien.getEmail())
                // Thêm các trường khác mà bạn muốn chuyển đổi
                .build();
    }


    public List<NhanVienDTO> getAll(){
        List<NhanVien> nhanViens = nhanVienRepo.findAll();
        return nhanViens.stream()
                .map(nhanVien -> modelMapper.map(nhanVien,NhanVienDTO.class))
                .collect(Collectors.toList());
    }



}
