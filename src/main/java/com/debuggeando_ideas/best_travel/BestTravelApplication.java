package com.debuggeando_ideas.best_travel;

import com.debuggeando_ideas.best_travel.domain.entities.jpa.*;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class BestTravelApplication implements CommandLineRunner {

	private final FlyRepository flyRepository;
	private final HotelRepository hotelRepository;
	private final CustomerRepository customerRepository;
	private final ReservationRepository reservationRepository;
	private final TicketRepository ticketRepository;
	private final TourRepository tourRepository;

	public static void main(String[] args) {
		SpringApplication.run(BestTravelApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {

		CustomerEntity customer = customerRepository.findById("VIKI771012HMCRG093").orElseThrow();
		log.info("Client name: {}", customer.getFullName());

		FlyEntity fly = flyRepository.findById(1L).orElseThrow();
		log.info("Fly : {}  - {}", fly.getOriginName(), fly.getDestinyName());

		HotelEntity hotel = hotelRepository.findById(2L).orElseThrow();
		log.info("Hotel: {}", hotel.getName());

		TourEntity tour = TourEntity.builder().customer(customer).build();

		TicketEntity ticket = TicketEntity.builder()
				.id(UUID.randomUUID())
				.price(fly.getPrice().multiply(BigDecimal.TEN))
				.arrivalDate(LocalDateTime.now())
				.departureDate(LocalDateTime.now())
				.purchaseDate(LocalDate.now())
				.customer(customer)
				.tour(tour)
				.fly(fly)
				.build();

		ReservationEntity reservation = ReservationEntity.builder()
				.id(UUID.randomUUID())
				.dateTimeReservation(LocalDateTime.now())
				.dateStart(LocalDate.now().plusDays(1))
				.dateEnd(LocalDate.now().plusDays(2))
				.hotel(hotel)
				.customer(customer)
				.tour(tour)
				.totalDays(1)
				.price(hotel.getPrice().multiply(BigDecimal.TEN))
				.build();

		System.out.println("-----------SAVING-----------");
		tour.addReservation(reservation);
//        tour.updateReservations();
		tour.addTicket(ticket);
//        tour.updateTickets();
		System.out.println("------TOUR-----");
		System.out.println(tour);
		TourEntity tourSaved = tourRepository.save(tour);
		System.out.println(tourSaved);
		tourRepository.deleteById(tourSaved.getId());
	}
}
