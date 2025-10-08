package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaRatingService;

    @Autowired
    public MpaController(MpaService mpaRatingService) {
        this.mpaRatingService = mpaRatingService;
    }

    @GetMapping
    public List<Mpa> getAllMpaRatings() {
        return mpaRatingService.getAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaRating(@PathVariable int id) {
        return mpaRatingService.getMpa(id);
    }

}
