package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.api.models.request.TicketRequest;
import com.debuggeando_ideas.best_travel.api.models.response.FlyResponse;
import com.debuggeando_ideas.best_travel.api.models.response.TicketResponse;
import com.debuggeando_ideas.best_travel.domain.entities.jpa.TicketEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.FlyRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.jpa.TicketRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ITicketService;
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
public class TicketService implements ITicketService {

    private final FlyRepository flyRepository;
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final CustomerHelper customerHelper;

    public static final BigDecimal charger_price_percentage = BigDecimal.valueOf(0.25);

    @Override
    public TicketResponse created(TicketRequest request) {
        var fly = flyRepository.findById(request.getIdFly()).orElseThrow();
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow();

        var ticketToPersist = TicketEntity.builder()
                .id(UUID.randomUUID())
                .fly(fly)
                .customer(customer)
                .price(fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage)))
                .purchaseDate(LocalDate.now())
                .departureDate(BestTravelUtil.getRandomSoon())
                .arrivalDate(BestTravelUtil.getRandomLatter())
                .build();

        var ticketPersisted = ticketRepository.save(ticketToPersist);
        customerHelper.incrase(customer.getDni(), TicketService.class);

        log.info("Ticket saved with id: {}", ticketPersisted.getId());
        return entityToResponse(ticketPersisted);
    }

    @Override
    public TicketResponse read(UUID uuid) {
        var ticketFromDB = ticketRepository.findById(uuid).orElseThrow();
        return entityToResponse(ticketFromDB);
    }

    @Override
    public TicketResponse update(TicketRequest request, UUID uuid) {
        var ticketToUpdate = ticketRepository.findById(uuid).orElseThrow();
        var fly  = flyRepository.findById(request.getIdFly()).orElseThrow();

        ticketToUpdate.setFly(fly);
        ticketToUpdate.setPrice(fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage)));
        ticketToUpdate.setDepartureDate(BestTravelUtil.getRandomSoon());
        ticketToUpdate.setArrivalDate(BestTravelUtil.getRandomLatter());

        var ticketUpdated = this.ticketRepository.save(ticketToUpdate);

        log.info("Ticket updated with id {}", ticketUpdated.getId());

        return this.entityToResponse(ticketToUpdate);
    }

    @Override
    public void delete(UUID uuid) {
        var ticketToDelete = ticketRepository.findById(uuid).orElseThrow();
        customerHelper.decrease(ticketToDelete.getCustomer().getDni(), TicketService.class);
        ticketRepository.delete(ticketToDelete);
    }

    private TicketResponse entityToResponse(TicketEntity entity) {
        var response = new TicketResponse();
        BeanUtils.copyProperties(entity,response);

        var flyResponse = new FlyResponse();
        BeanUtils.copyProperties(entity.getFly(),flyResponse);
        response.setFly(flyResponse);

        return response;
    }

    @Override
    public BigDecimal findPrice(Long flyId) {
        var fly = this.flyRepository.findById(flyId).orElseThrow();
        return fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage));
    }
}
