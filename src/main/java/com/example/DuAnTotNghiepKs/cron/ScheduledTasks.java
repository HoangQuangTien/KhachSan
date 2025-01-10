package com.example.DuAnTotNghiepKs.cron;

import com.example.DuAnTotNghiepKs.entity.DatPhong;
import com.example.DuAnTotNghiepKs.service.Imp.DatPhongServiceImp;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class ScheduledTasks {

    private final DatPhongServiceImp datPhongService;


    public ScheduledTasks(DatPhongServiceImp datPhongService) {
        this.datPhongService = datPhongService;

    }
    //    @Scheduled(cron = "0 * * * * ?") // Chạy mỗi phút
    @Scheduled(cron = "0 55 23 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void generateNightlyReport() throws IOException {
        List<DatPhong> guests = datPhongService.getDatPhongAndNganBeetwen();
        String filePath = new File("src/main/resources/static/luutru/khach_luu_tru_" + LocalDate.now() + ".xlsx").getAbsolutePath();
        System.out.println("guest"+filePath);
        File directory = new File("src/main/resources/static/luutru");
        if (!directory.exists()) {
            directory.mkdirs(); // Tạo thư mục nếu chưa tồn tại
        }

        datPhongService.exportGuestReport(guests, filePath);

    }

}
