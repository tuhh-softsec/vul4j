package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ReservationMvcController {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationMvcController(ReservationRepository rr) {
        this.reservationRepository = rr;
    }

    @RequestMapping(method = RequestMethod.GET, value = ".reservations.mvc")
    public String renderReservations(Model model) {
        model.addAttribute("reservations", this.reservationRepository.findAll());
        // find template named 'reservations'
        return "reservations";
    }

}
