package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.response.FlyResponse;
import com.debuggeando_ideas.best_travel.api.models.response.HotelResponse;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IHotelService;
import com.debuggeando_ideas.best_travel.util.enums.SortType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/hotel")
@AllArgsConstructor
public class HotelController {
    private final IHotelService hotelService;

    @GetMapping
    public ResponseEntity<Page<HotelResponse>> getAll(@RequestParam Integer page,
                                                      @RequestParam Integer size,
                                                      @RequestParam(required = false) SortType sortType){

        if(Objects.isNull(sortType)) sortType = SortType.NONE;
        var response = hotelService.readAll(page,size,sortType);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);

    }

    @GetMapping("/less_price")
    public ResponseEntity<Set<HotelResponse>> getLessPrice(@RequestParam BigDecimal price) {
        var response = hotelService.readLessPrice(price);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/between_price")
    public ResponseEntity<Set<HotelResponse>> getBetweenPrice(@RequestParam BigDecimal min,@RequestParam BigDecimal max){
        var response = hotelService.readBetweenPrices(min,max);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/rating")
    public ResponseEntity<Set<HotelResponse>> getByRating(@RequestParam Integer rating){
        if (rating > 4) rating = 4;
        if (rating < 1) rating = 1;
        var response = hotelService.readByRating(rating);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }
}
