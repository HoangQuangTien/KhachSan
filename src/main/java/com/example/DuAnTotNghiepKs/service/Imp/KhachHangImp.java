package com.example.DuAnTotNghiepKs.service.Imp;

import com.example.DuAnTotNghiepKs.DTO.DiaChiKhachHangDTO;
import com.example.DuAnTotNghiepKs.DTO.KhachHangDTO;
import com.example.DuAnTotNghiepKs.entity.DiaChiKhachHang;
import com.example.DuAnTotNghiepKs.entity.KhachHang;
import com.example.DuAnTotNghiepKs.exception.ResourceNotfound;
import com.example.DuAnTotNghiepKs.repository.DiaChiKhachHangRepository;
import com.example.DuAnTotNghiepKs.repository.KhachHangRepository;
import com.example.DuAnTotNghiepKs.repository.TaiKhoanRepo;
import com.example.DuAnTotNghiepKs.service.KhachHangService;
import org.modelmapper.ModelMapper;
import org.netlib.util.booleanW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhachHangImp implements KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private TaiKhoanRepo taiKhoanRepo;
    @Autowired
    private DiaChiKhachHangRepository diaChiKhachHangRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<KhachHangDTO> getAll() {
        List<KhachHang> khachHangs = khachHangRepository.findAll();
        return khachHangs.stream().map(khachHang -> modelMapper.map(khachHang, KhachHangDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public KhachHangDTO getOne(Integer id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfound("Không tồn tại id: " + id));
        if (!khachHang.isDeletedAt()) {
            throw new ResourceNotfound("Bản ghi đã bị xoá: " + id);
        }
        return modelMapper.map(khachHang, KhachHangDTO.class);
    }

    @Override
    public KhachHangDTO save(KhachHangDTO khachHangDTO) {
        KhachHang khachHang = modelMapper.map(khachHangDTO, KhachHang.class);
        khachHang = khachHangRepository.save(khachHang);

        List<DiaChiKhachHangDTO> diaChiList = khachHangDTO.getDiaChi();
        if (diaChiList != null && !diaChiList.isEmpty()) {
            for (DiaChiKhachHangDTO diaChiKhachHangDTO : diaChiList) {
                DiaChiKhachHang diaChiKhachHang = modelMapper.map(diaChiKhachHangDTO, DiaChiKhachHang.class);
                diaChiKhachHang.setIdKhachHang(khachHang);
                diaChiKhachHangRepository.save(diaChiKhachHang);
            }
        }

        return modelMapper.map(khachHang, KhachHangDTO.class);

    }

    @Override
    public String generateMaKhachHang() {
        // Logic để sinh mã khách hàng tự động
        // Ví dụ: mã khách hàng có thể là một chuỗi số tự tăng
        return "KH0" + (khachHangRepository.count() + 1);
    }

    @Override
    public void update(Integer id, KhachHangDTO khachHangDTO) {
        // Lấy khách hàng từ repository
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfound("Không tìm thấy khách hàng có id: " + id));

        // Cập nhật thông tin từ khachHangDTO vào khachHang
        if (khachHangDTO.getMaKhachHang() != null) {
            khachHang.setMaKhachHang(khachHangDTO.getMaKhachHang());
        }
//        if (khachHangDTO.getMatKhau() != null) {
//            khachHang.setMatKhau(khachHangDTO.getMatKhau());
//        }
        if (khachHangDTO.getHoVaTen() != null) {
            khachHang.setHoVaTen(khachHangDTO.getHoVaTen());
        }
        if (khachHangDTO.getEmail() != null) {
            khachHang.setEmail(khachHangDTO.getEmail());
        }
        khachHang.setGioiTinh(khachHangDTO.isGioiTinh());
        if (khachHangDTO.getSoDienThoai() != null) {
            khachHang.setSoDienThoai(khachHangDTO.getSoDienThoai());
        }
//        if (khachHangDTO.getTrangThai() != null) {
//            khachHang.setTrangThai(khachHangDTO.getTrangThai());
//        }

        // Lưu thông tin khách hàng đã cập nhật vào cơ sở dữ liệu
        khachHangRepository.save(khachHang);

    }

    @Override
    public void delete(Integer id) {
    }

    @Override
    public KhachHangDTO findById(Integer id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotfound("Không tồn tại id: " + id));
        return modelMapper.map(khachHang, KhachHangDTO.class);
    }

    @Override
    public Page<KhachHangDTO> getAll(Pageable pageable) {
        Page<KhachHang> khachHangs = khachHangRepository.findAll(pageable);
        return khachHangs.map(khachHang -> modelMapper.map(khachHang, KhachHangDTO.class));
    }

    @Override
    public List<KhachHangDTO> search(String query) {
        List<KhachHang> results = khachHangRepository.searchByNameOrPhone(query);
        return results.stream().map(khachHang -> modelMapper.map(khachHang, KhachHangDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<KhachHangDTO> filter(String status) {
//        if ("all".equals(status)) {
//            return khachHangRepository.findAll().stream()
//                    .map(khachHang -> modelMapper.map(khachHang, KhachHangDTO.class))
//                    .collect(Collectors.toList());
//        } else {
//            return khachHangRepository.filterByStatus(status).stream()
//                    .map(khachHang -> modelMapper.map(khachHang, KhachHangDTO.class))
//                    .collect(Collectors.toList());
//        }
        return null;
    }

    @Override
    public KhachHang convertToEntity(KhachHangDTO khachHangDTO) {
        KhachHang khachHang = new KhachHang();
        khachHang.setId(khachHangDTO.getId());
        khachHang.setHoVaTen(khachHangDTO.getHoVaTen());
        khachHang.setEmail(khachHangDTO.getEmail());

        return khachHang;
    }

    @Override
    public Integer saveKhachHang(KhachHangDTO khachHangDTO) {
        // Chuyển đổi DTO sang entity nếu cần
        KhachHang khachHang = modelMapper.map(khachHangDTO, KhachHang.class);

        // Lưu thông tin khách hàng vào DB
        KhachHang savedKhachHang = khachHangRepository.save(khachHang);

        // Trả về ID của khách hàng vừa được lưu
        return savedKhachHang.getId();
    }

    @Override
    public boolean existsByEmail(String email) {
        return khachHangRepository.existsByEmail(email);
    }

    @Override
    public boolean existsBySoDienThoai(String soDienThoai) {
        return khachHangRepository.existsBySoDienThoai(soDienThoai);
    }

    @Override
    public KhachHangDTO findBySoDienThoai(String soDienThoai) {
        KhachHang khachHang = khachHangRepository.findBySoDienThoai(soDienThoai);
        if (khachHang != null) {
            // Chuyển đổi từ KhachHang entity sang KhachHangDTO
            return KhachHangDTO.builder().id(khachHang.getId()).maKhachHang(khachHang.getMaKhachHang())
                    .hoVaTen(khachHang.getHoVaTen()).email(khachHang.getEmail()).gioiTinh(khachHang.isGioiTinh())
                    .soDienThoai(khachHang.getSoDienThoai()).build();
        }
        return null;
    }

    @Override
    public KhachHangDTO findByEmail(String email) {
        KhachHang khachHang = khachHangRepository.findByEmail(email);
        if (khachHang != null) {
            // Chuyển đổi từ KhachHang entity sang KhachHangDTO
            return KhachHangDTO.builder().id(khachHang.getId()).maKhachHang(khachHang.getMaKhachHang())
                    .hoVaTen(khachHang.getHoVaTen()).email(khachHang.getEmail()).gioiTinh(khachHang.isGioiTinh())
                    .soDienThoai(khachHang.getSoDienThoai()).build();
        }
        return null;
    }

    @Override
    public boolean existsById(Integer id) {
        return khachHangRepository.existsById(id);
    }

    @Override
    public boolean update1(Integer id, KhachHangDTO khachHangDTO) {
        // Tìm kiếm khách hàng theo ID
        KhachHang khachHang = khachHangRepository.findById(id).orElse(null);
        if (khachHang != null) {
            // Cập nhật thông tin từ DTO
            khachHang.setHoVaTen(khachHangDTO.getHoVaTen());
            khachHang.setEmail(khachHangDTO.getEmail());
            khachHang.setSoDienThoai(khachHangDTO.getSoDienThoai());
            khachHang.setGioiTinh(khachHangDTO.isGioiTinh());
            khachHangRepository.save(khachHang);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsByTenDangNhap(String tenDangNhap) {
        return taiKhoanRepo.findByTenDangNhap(tenDangNhap) != null;
    }

    @Override
    public String createMaKH() {
        List<String> allMaKhachHang = khachHangRepository.findAll().stream().map(KhachHang::getMaKhachHang)
                .collect(Collectors.toList());

        if (allMaKhachHang.isEmpty()) {
            return "KH001";
        }

        int maxNumber = allMaKhachHang.stream().filter(maKH -> maKH.startsWith("KH"))
                .mapToInt(maKH -> Integer.parseInt(maKH.substring(2))).max().orElse(0);

        return String.format("KH%03d", maxNumber + 1);
    }

    @Override
    public void insert(KhachHang khachHang) {
        taiKhoanRepo.save(khachHang.getTaiKhoan());
        khachHangRepository.save(khachHang);
    }

    @Override
    public KhachHang findByTenDangNhap(String tenDangNhap) {
        return khachHangRepository.findByTenDangNhap(tenDangNhap);
    }

    @Override
    public void updateKH(KhachHang khachHang) {
        khachHangRepository.save(khachHang);

    }



}
