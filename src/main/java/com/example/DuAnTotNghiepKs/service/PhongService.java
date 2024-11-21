package com.example.DuAnTotNghiepKs.service;
import com.example.DuAnTotNghiepKs.DTO.PhongDTO;
import com.example.DuAnTotNghiepKs.entity.LoaiPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;
import com.example.DuAnTotNghiepKs.repository.LoaiPhongRepo;
import com.example.DuAnTotNghiepKs.repository.PhongRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhongService {
    @Autowired
    private PhongRepo phongRepository;

    @Autowired
    private LoaiPhongRepo loaiPhongRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<Phong> getAllPhongs(Pageable pageable) {
        return phongRepository.findAll(pageable);
    }

    public Page<Phong> getPhongsByLoaiPhong(Integer loaiPhongId, Pageable pageable) {
        return phongRepository.findByLoaiPhong_IdLoaiPhong(loaiPhongId, pageable);
    }

    public Page<Phong> getPhongsByLoaiPhongAndTinhTrang(Integer loaiPhongId, Boolean tinhTrang, Pageable pageable) {
        return phongRepository.findByLoaiPhong_IdLoaiPhongAndTinhTrang(loaiPhongId, tinhTrang, pageable);
    }

    public Page<Phong> getPhongsByTinhTrang(Boolean tinhTrang, Pageable pageable) {
        return phongRepository.findByTinhTrang(tinhTrang, pageable);
    }

    public Phong findById(Integer id) {
        return phongRepository.findById(id).orElse(null);
    }

    public List<Phong> findByLoaiPhongId(Integer loaiPhongId){
        return phongRepository.findByLoaiPhongIdLoaiPhong(loaiPhongId);
    }

    public Phong savePhong(Phong phong) {
        // Kiểm tra trùng mã phòng
//        if (isMaPhongTrung(phong.getMaPhong())) {
//            throw new IllegalArgumentException("Mã phòng đã tồn tại.");
//        }
        return phongRepository.save(phong);
    }

    public boolean isMaPhongTrung(String maPhong) {
        return phongRepository.existsByMaPhong(maPhong);
    }

    public Optional<Phong> getPhongById(Integer id) {
        return phongRepository.findById(id);
    }


    public long countInactivePhongs1() {
        return phongRepository.countByTrangThai(false); // Tổng số phòng hết phòng
    }


    // Thêm phương thức tính tổng số phòng đang hoạt động
    public long countActivePhongs1() {
        return phongRepository.countByTrangThai(true);
    }


    // Thêm phương thức tính tổng số phòng đang hoạt động
    public long countActivePhongs() {
        return phongRepository.countByTinhTrang(true);
    }




    public void updatePhong(Phong phong) {
        if (phong.getIdPhong() == null) {
            throw new IllegalArgumentException("Phong ID must not be null.");
        }

        if (!phongRepository.existsById(phong.getIdPhong())) {
            throw new IllegalArgumentException("Phong with ID " + phong.getIdPhong() + " does not exist.");
        }

        // Xử lý loại phòng
        if (phong.getLoaiPhong() != null && phong.getLoaiPhong().getIdLoaiPhong() != null) {
            LoaiPhong loaiPhong = loaiPhongRepository.findById(phong.getLoaiPhong().getIdLoaiPhong()).orElse(null);
            phong.setLoaiPhong(loaiPhong);
        } else {
            throw new IllegalArgumentException("LoaiPhong must be selected.");
        }

        phongRepository.save(phong);
    }


    public boolean updateTinhTrang(Integer idPhong, Boolean tinhTrang) {
        Phong phong = phongRepository.findById(idPhong).orElse(null);
        if (phong == null) {
            return false; // Hoặc có thể ném ngoại lệ tùy vào logic ứng dụng
        }
        phong.setTinhTrang(tinhTrang);
        phongRepository.save(phong);
        return true;
    }



    public List<Phong> getPhongsByLoaiPhong1(Integer loaiPhongId) {
        return phongRepository.findByLoaiPhong_IdLoaiPhongAndTrangThaiTrue(loaiPhongId);
    }

    public PhongDTO convertToPhongDTO(Phong phong) {
        PhongDTO dto = new PhongDTO();
        dto.setIdPhong(phong.getIdPhong());
        dto.setTenPhong(phong.getTenPhong());
        dto.setMaPhong(phong.getMaPhong());
        dto.setMoTa(phong.getMoTa());
        dto.setTinhTrang(phong.getTinhTrang());
        dto.setTrangThai(phong.getTrangThai());
        dto.setGia(phong.getGia());

        if (phong.getLoaiPhong() != null) {
            dto.setIdLoaiPhong(phong.getLoaiPhong().getIdLoaiPhong());
            dto.setTenLoaiPhong(phong.getLoaiPhong().getTenLoaiPhong());
            dto.setGiaLoaiPhong(phong.getLoaiPhong().getGia());
            dto.setSoNguoiToiDa(phong.getLoaiPhong().getSoNguoiToiDa());
        }

        return dto;
    }

    // Phương thức để đếm số lượng phòng còn trống và đang hoạt động
    public long countAvailableActiveRooms() {
        return phongRepository.countByTrangThaiAndTinhTrang(true, true); // true: phòng còn trống, true: phòng đang hoạt động
    }




    public List<PhongDTO> search(String query) {
        List<Phong> results = phongRepository.searchByMaOrTenPhong(query);
        return results.stream()
                .map(phong -> {
                    PhongDTO phongDTO = modelMapper.map(phong, PhongDTO.class);
//
//                    phongDTO.setTenDienTich(phong.getDienTich().getTenDienTich()); // Giả sử dienTich là kiểu Float
//
//
//                    phongDTO.setTenTang(phong.getTang() != null ? phong.getTang().getTenTang() : "Chưa có tầng");

                    phongDTO.setTenLoaiPhong(phong.getLoaiPhong() != null ? phong.getLoaiPhong().getTenLoaiPhong() : "Chưa có Loại Phon");

                    return phongDTO;
                })
                .collect(Collectors.toList());
    }

    public List<Phong> getAllPhongs1() {
        return phongRepository.findAll();
    }


    public String updateRoomStatusToAvailable(Integer roomId) {
        // Tìm phòng theo ID
        Optional<Phong> optionalRoom = phongRepository.findById(roomId);

        // Kiểm tra xem phòng có tồn tại hay không
        if (optionalRoom.isPresent()) {
            Phong room = optionalRoom.get();
            room.setTrangThai(true); // Cập nhật trạng thái phòng
            phongRepository.save(room);
            return "Cập nhật trạng thái phòng thành công!";
        } else {
            return "Không tìm thấy phòng với ID: " + roomId; // Trả về thông báo nếu không tìm thấy
        }
    }


    public int countByLoaiPhongId(Integer idLoaiPhong) {
        return phongRepository.countByLoaiPhongId(idLoaiPhong);
    }


    public List<Phong> getAvailableRoomsForEdit(Integer currentRoomId, LocalDateTime startDate, LocalDateTime endDate) {
        return phongRepository.findAvailableRoomsForTimeRange(currentRoomId, startDate, endDate);
    }

    // tìm kiếm phòng theo ngày ,số lượng người
    public List<Phong> searchPhongs(LocalDateTime ngayNhan, LocalDateTime ngayTra, Integer soLuongNguoi) {
        return phongRepository.findAvailablePhongs(ngayNhan, ngayTra, soLuongNguoi);
    }


    public List<Phong> getAllByTrangThai(){
        boolean trangThai = false;
        return phongRepository.findByTrangThai(trangThai);
    }

    public Phong getPhongById1(Integer roomId) {
        return phongRepository.findById(roomId).orElse(null);
    }

    public List<PhongDTO> getPhongByIds(List<Integer> roomIds) {
        List<Phong> rooms = phongRepository.findAllById(roomIds); // Lấy tất cả phòng từ repository
        return rooms.stream()
                .filter(Objects::nonNull) // Lọc phòng null nếu có
                .map(this::convertToPhongDTO) // Chuyển đổi thành PhongDTO
                .collect(Collectors.toList());
    }


    public boolean isTenPhongTrung(String tenPhong) {
        return phongRepository.existsByTenPhong(tenPhong);
    }

    public String generateMaPhong() {
        String maxMaPhong = phongRepository.findMaxMaPhong(); // Lấy mã lớn nhất hiện có
        if (maxMaPhong != null) {
            // Tách số từ mã phòng và tăng lên
            int nextNumber = Integer.parseInt(maxMaPhong.replaceAll("[^\\d]", "")) + 1;
            return String.format("P%03d", nextNumber); // Định dạng: P001, P002,...
        }
        return "P001"; // Giá trị mặc định khi chưa có phòng nào
    }


    // Trong lớp PhongService
    public List<PhongDTO> findAvailableRooms(LocalDateTime ngayNhan, LocalDateTime ngayTra, int soLuongNguoi) {
        // Gọi repo để lấy danh sách phòng trống dựa trên các tiêu chí tìm kiếm
        List<Phong> availableRooms = phongRepository.findAvailablePhongs(ngayNhan, ngayTra, soLuongNguoi);

        // Chuyển đổi các đối tượng Phong sang PhongDTO nếu cần thiết
        List<PhongDTO> availableRoomDTOs = availableRooms.stream()
                .map(room -> new PhongDTO(room.getIdPhong(), room.getTenPhong(), room.getMaPhong() ,room.getMoTa(),room.getTinhTrang(),room.getTrangThai(),room.getGia(),room.getImg(),room.getLoaiPhong().getSoNguoiToiDa(),room.getLoaiPhong().getIdLoaiPhong(),
                        room.getLoaiPhong().getTenLoaiPhong(),room.getLoaiPhong().getGia()))
                .collect(Collectors.toList());

        return availableRoomDTOs;
    }


    // Phương thức lấy phòng có sẵn cho ngày nhận phòng và trả phòng, và số người yêu cầu
    public List<Phong> getAvailableRoomsWithMaxGuests(LocalDateTime startDate, LocalDateTime endDate, int soNguoi, int soPhong) {
        // Lấy danh sách phòng có sẵn trong khoảng thời gian
        List<Phong> availableRooms = phongRepository.findAvailableRooms(startDate, endDate);

        // Lọc phòng theo số người tối đa mà phòng có thể chứa
        List<Phong> filteredRooms = availableRooms.stream()
                .filter(room -> room.getLoaiPhong().getSoNguoiToiDa() >= soNguoi)  // Kiểm tra phòng có thể chứa số khách yêu cầu
                .collect(Collectors.toList());

        // Nếu có yêu cầu về số phòng, lọc thêm số phòng cần thiết
        if (filteredRooms.size() > soPhong) {
            return filteredRooms.subList(0, soPhong); // Lấy đúng số phòng yêu cầu
        }

        return filteredRooms;
    }

}

