package com.example.DuAnTotNghiepKs.rest;

import com.example.DuAnTotNghiepKs.DTO.ThamSoDTO;
import com.example.DuAnTotNghiepKs.entity.ThamSo;
import com.example.DuAnTotNghiepKs.service.ThamSoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/thamSo")
public class ThamSoRest {

    @Autowired
    private ThamSoService thamSoService;

    @GetMapping
    public String viewThamSoList(Model model) {
        model.addAttribute("thamSoList", thamSoService.getAllThamSo());
        return "list/QuanLyThamSo/thamSo";
    }

    @PostMapping("/add")
    public String addThamSo(@ModelAttribute ThamSoDTO thamSoDTO) {
        thamSoService.save(thamSoDTO);
        return "redirect:/thamSo";
    }

    @PostMapping("/update")
    public String updateThamSo(@ModelAttribute ThamSoDTO thamSoDTO) {
        thamSoService.save(thamSoDTO);
        return "redirect:/thamSo";
    }
}

