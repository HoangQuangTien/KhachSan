package com.example.DuAnTotNghiepKs.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdleRoomDTO {
    private Long phongId;
    private String phongName;
    private Date lastCheckOut;
    private Date nextCheckIn;
    private Integer idleDays;

//    // Constructor, getters và setters
//    public IdleRoomDTO(Long phongId, Date lastCheckOut, Date nextCheckIn, Integer idleDays) {
//        this.phongId = phongId;
//        this.lastCheckOut = lastCheckOut;
//        this.nextCheckIn = nextCheckIn;
//        this.idleDays = idleDays;
//    }


}
