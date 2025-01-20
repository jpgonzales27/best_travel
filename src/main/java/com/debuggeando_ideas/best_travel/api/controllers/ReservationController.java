package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.api.models.request.ReservationRequest;
import com.debuggeando_ideas.best_travel.api.models.request.TicketRequest;
import com.debuggeando_ideas.best_travel.api.models.response.ErrorsResponse;
import com.debuggeando_ideas.best_travel.api.models.response.ReservationResponse;
import com.debuggeando_ideas.best_travel.api.models.response.TicketResponse;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/reservation")
@AllArgsConstructor
@Tag(name = "Reservation")
public class ReservationController {

    private final IReservationService reservationService;

    @ApiResponse(
            responseCode = "400",
            description = "When the request have a field invalid we response this",
            content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorsResponse.class))
            }
    )
    @Operation(summary = "Save in system un reservation with the fly passed in parameter")
    @PostMapping
    public ResponseEntity<ReservationResponse> post(@Valid @RequestBody ReservationRequest request){
        return ResponseEntity.ok(reservationService.created(request));
    }

    @Operation(summary = "Return a reservation with of passed")
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getById(@PathVariable UUID id){
        return ResponseEntity.ok(reservationService.read(id));
    }

    @Operation(summary = "Update a reservation")
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> update(@PathVariable UUID id, @Valid @RequestBody ReservationRequest request){
        return ResponseEntity.ok(reservationService.update(request, id));
    }

    @Operation(summary = "Delete a reservation")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "return a reservation price given a hotel id")
    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getReservationPrice(@RequestParam Long hotelId,
                                                                       @RequestHeader(required = false) Currency currency) {
        if (Objects.isNull(currency)) currency = Currency.getInstance("USD");
        return ResponseEntity.ok(Collections.singletonMap("reservation_price", reservationService.findPrice(hotelId, currency)));
    }
}
