package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.entity.Phong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RoomStatusUpdater {

    @Autowired
    private PhongService phongService;

    @Autowired DatPhongService datPhongService;

    // Map để lưu trữ các phòng cần cập nhật trạng thái và thời gian cần cập nhật
    private Map<Integer, Long> pendingUpdates = new HashMap<>();

    // Phương thức để lên lịch cập nhật trạng thái phòng sau 30 phút
    public void scheduleRoomStatusUpdate(Integer idPhong) {
        long updateTime = System.currentTimeMillis() + (30 * 60 * 1000); // 30 phút sau
        pendingUpdates.put(idPhong, updateTime);
    }

    // Phương thức để thực hiện việc cập nhật trạng thái phòng
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // Kiểm tra mỗi ngày
    public void updateRoomStatuses() {
        LocalDateTime currentDate = LocalDateTime.now();

        // Lấy tất cả các đặt phòng hiện tại
        List<DatPhong> datPhongs = datPhongService.getAllDatPhong();

        // Cập nhật trạng thái phòng nếu ngày hiện tại đã vượt qua ngày kết thúc đặt phòng
        for (DatPhong datPhong : datPhongs) {
            LocalDateTime ngayTra = datPhong.getNgayTra().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
            Phong phong = datPhong.getPhong();

            if (phong != null && ngayTra.isBefore(currentDate) && !phong.getTrangThai()) {
                phong.setTrangThai(true); // true có thể là trạng thái còn phòng
                phongService.savePhong(phong);
            }
        }
    }
}

