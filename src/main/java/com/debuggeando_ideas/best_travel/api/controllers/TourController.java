package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.request.TourRequest;
import com.debuggeando_ideas.best_travel.api.models.response.TourResponse;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ITourService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tour")
@AllArgsConstructor
public class TourController {

    private final ITourService tourService;


    @PostMapping
    public ResponseEntity<TourResponse> post(@Valid @RequestBody TourRequest request) {
        return ResponseEntity.ok(this.tourService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TourResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(this.tourService.read(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tourService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{tourId}/remove_ticket/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long tourId, @PathVariable UUID ticketId){
        tourService.removeTicket(tourId, ticketId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{tourId}/add_ticket/{flyId}")
    public ResponseEntity<Map<String, UUID>> addTicket(@PathVariable Long tourId, @PathVariable Long flyId){
        var response = Collections.singletonMap("tickedId",tourService.addTicket(tourId,flyId));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{tourId}/remove_reservation/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long tourId, @PathVariable UUID reservationId){
        tourService.removeReservation(tourId, reservationId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{tourId}/add_reservation/{hotelId}")
    public ResponseEntity<Map<String, UUID>> addReservation(@PathVariable Long tourId, @PathVariable Long hotelId,@RequestParam Integer totalDays){
        var response = Collections.singletonMap("reservationId",tourService.addReservation(tourId,hotelId,totalDays));
        return ResponseEntity.ok(response);
    }
}
