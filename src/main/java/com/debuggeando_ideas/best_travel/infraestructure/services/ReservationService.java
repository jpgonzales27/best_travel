package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.request.ReservationRequest;
import com.debuggeando_ideas.best_travel.api.models.response.FlyResponse;
import com.debuggeando_ideas.best_travel.api.models.response.HotelResponse;
import com.debuggeando_ideas.best_travel.api.models.response.ReservationResponse;
import com.debuggeando_ideas.best_travel.api.models.response.TicketResponse;
import com.debuggeando_ideas.best_travel.domain.entities.jpa.ReservationEntity;
import com.debuggeando_ideas.best_travel.domain.entities.jpa.TicketEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.HotelRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.ReservationRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.TicketRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.IReservationService;
import com.debuggeando_ideas.best_travel.infraestructure.helpers.CustomerHelper;
import com.debuggeando_ideas.best_travel.util.BestTravelUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class ReservationService implements IReservationService {

    private final HotelRepository hotelRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final CustomerHelper customerHelper;

    public static final BigDecimal charges_price_percentage = BigDecimal.valueOf(0.20);

    @Override
    public ReservationResponse created(ReservationRequest request) {

        var hotel = hotelRepository.findById(request.getIdHotel()).orElseThrow();
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow();

        var reservationToPersist = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .dateTimeReservation(LocalDateTime.now())
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(request.getTotalDays()))
                .totalDays(request.getTotalDays())
                .price(hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage)))
                .hotel(hotel)
                .customer(customer)
                .build();

        var reservationPersisted = reservationRepository.save(reservationToPersist);
        customerHelper.incrase(customer.getDni(), ReservationService.class);

        log.info("Reservation saved with id: {}", reservationPersisted.getId());
        return entityToResponse(reservationPersisted);
    }

    @Override
    public ReservationResponse read(UUID uuid) {
        ReservationEntity reservation = reservationRepository.findById(uuid).orElseThrow();
        return entityToResponse(reservation);
    }

    @Override
    public ReservationResponse update(ReservationRequest request, UUID uuid) {

        var reservationToUpdate = reservationRepository.findById(uuid).orElseThrow();
        var hotel = hotelRepository.findById(request.getIdHotel()).orElseThrow();
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow();

        reservationToUpdate.setHotel(hotel);
        reservationToUpdate.setCustomer(customer);
        reservationToUpdate.setTotalDays(request.getTotalDays());
        reservationToUpdate.setDateTimeReservation(LocalDateTime.now());
        reservationToUpdate.setDateStart(LocalDate.now());
        reservationToUpdate.setDateEnd(LocalDate.now().plusDays(request.getTotalDays()));
        reservationToUpdate.setPrice(hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage)));

        var reservationUpdated = reservationRepository.save(reservationToUpdate);

        log.info("Reservation updated with id {}", reservationUpdated.getId());
        return entityToResponse(reservationUpdated);
    }

    @Override
    public void delete(UUID uuid) {
        ReservationEntity reservation = reservationRepository.findById(uuid).orElseThrow();
        customerHelper.decrease(reservation.getCustomer().getDni(), ReservationService.class);
        reservationRepository.delete(reservation);
    }

    private ReservationResponse entityToResponse(ReservationEntity entity) {
        var response = new ReservationResponse();
        BeanUtils.copyProperties(entity, response);

        var hotelResponse = new HotelResponse();
        BeanUtils.copyProperties(entity.getHotel(), hotelResponse);
        response.setHotel(hotelResponse);

        return response;
    }

    @Override
    public BigDecimal findPrice(Long hotelId) {
        var fly = this.hotelRepository.findById(hotelId).orElseThrow();
        return fly.getPrice().add(fly.getPrice().multiply(charges_price_percentage));
    }

}
