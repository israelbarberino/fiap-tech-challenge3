package br.com.fiap.hospital.appointment.controller;

import br.com.fiap.hospital.appointment.domain.AppointmentStatus;
import br.com.fiap.hospital.appointment.dto.AppointmentRequest;
import br.com.fiap.hospital.appointment.dto.AppointmentResponse;
import br.com.fiap.hospital.appointment.dto.AppointmentUpdateRequest;
import br.com.fiap.hospital.appointment.security.AuthenticatedUser;
import br.com.fiap.hospital.appointment.service.AppointmentSearchCriteria;
import br.com.fiap.hospital.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public AppointmentResponse create(@Valid @RequestBody AppointmentRequest request,
                                      @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return appointmentService.create(request, currentUser);
    }

    @PutMapping("/{id}")
    public AppointmentResponse update(@PathVariable UUID id,
                                      @Valid @RequestBody AppointmentUpdateRequest request,
                                      @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return appointmentService.update(id, request, currentUser);
    }

    @GetMapping("/{id}")
    public AppointmentResponse get(@PathVariable UUID id,
                                   @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return appointmentService.get(id, currentUser);
    }

    @GetMapping
    public Page<AppointmentResponse> list(@RequestParam(required = false) UUID patientId,
                                          @RequestParam(required = false) UUID doctorId,
                                          @RequestParam(required = false) AppointmentStatus status,
                                          @RequestParam(required = false) OffsetDateTime from,
                                          @RequestParam(required = false) OffsetDateTime to,
                                          @PageableDefault(size = 20) Pageable pageable,
                                          @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return appointmentService.list(new AppointmentSearchCriteria(patientId, doctorId, status, from, to), pageable, currentUser);
    }

    @DeleteMapping("/{id}")
    public void cancel(@PathVariable UUID id,
                       @AuthenticationPrincipal AuthenticatedUser currentUser) {
        appointmentService.cancel(id, currentUser);
    }
}
