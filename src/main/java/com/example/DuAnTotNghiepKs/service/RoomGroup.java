package com.example.DuAnTotNghiepKs.service;

import com.example.DuAnTotNghiepKs.entity.Phong;

import java.util.List;

public class RoomGroup {
    private List<Phong> rooms; // Danh sách các phòng trong nhóm
    private String roomType; // Loại phòng
    private int quantity; // Số lượng phòng

    // Constructor, getters và setters
    public RoomGroup(List<Phong> rooms, String roomType, int quantity) {
        this.rooms = rooms;
        this.roomType = roomType;
        this.quantity = quantity;
    }

    public List<Phong> getRooms() {
        return rooms;
    }

    public void setRooms(List<Phong> rooms) {
        this.rooms = rooms;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
