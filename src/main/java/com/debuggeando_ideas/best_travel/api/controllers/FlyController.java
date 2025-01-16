package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.response.FlyResponse;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IFlyService;
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
@RequestMapping("/fly")
@AllArgsConstructor
public class FlyController {

    private final IFlyService flyService;

    @GetMapping
    public ResponseEntity<Page<FlyResponse>> getAll(@RequestParam Integer page,
                                                    @RequestParam Integer size,
                                                    @RequestParam(required = false) SortType sortType){

        if(Objects.isNull(sortType)) sortType = SortType.NONE;
        var response = flyService.readAll(page,size,sortType);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);

    }

    @GetMapping("/less_price")
    public ResponseEntity<Set<FlyResponse>> getLessPrice(@RequestParam BigDecimal price) {
        var response = flyService.readLessPrice(price);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/between_price")
    public ResponseEntity<Set<FlyResponse>> getBetweenPrice(@RequestParam BigDecimal min,@RequestParam BigDecimal max){
        var response = flyService.readBetweenPrices(min,max);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/origin_destiny")
    public ResponseEntity<Set<FlyResponse>> getByOriginDestiny(@RequestParam String origin,@RequestParam String destiny){
        var response = flyService.readByOriginDestiny(origin,destiny);
        return response.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }
}
