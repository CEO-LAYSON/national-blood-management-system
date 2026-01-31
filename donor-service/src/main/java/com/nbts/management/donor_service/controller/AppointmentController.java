package com.nbts.management.donor_service.controller;

import com.nbts.management.donor_service.dto.AppointmentResponseDTO;
import com.nbts.management.donor_service.dto.CreateAppointmentDTO;
import com.nbts.management.donor_service.dto.DonorServiceResponseDTO;
import com.nbts.management.donor_service.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Appointments", description = "Endpoints for managing donor appointments")
@RestController
@RequestMapping("appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Create a new appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Appointment created successfully",
                    content = @Content(schema = @Schema(implementation = DonorServiceResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("create")
    public ResponseEntity<DonorServiceResponseDTO<Map<String, Object>>> createAppointment(
            @Valid @RequestBody CreateAppointmentDTO dto,
            HttpServletRequest request
    ) {
        appointmentService.createAppointment(dto);
        DonorServiceResponseDTO<Map<String, Object>> response = DonorServiceResponseDTO.ok(
                Map.of("message", "Appointment created successfully"),
                "Appointment created successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get appointment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointment retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DonorServiceResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    @GetMapping("{appointmentId}")
    public ResponseEntity<DonorServiceResponseDTO<AppointmentResponseDTO>> getAppointmentById(
            @PathVariable UUID appointmentId,
            HttpServletRequest request
    ) {
        AppointmentResponseDTO appointment = appointmentService.appointment(appointmentId);
        DonorServiceResponseDTO<AppointmentResponseDTO> response = DonorServiceResponseDTO.ok(
                appointment,
                "Appointment retrieved successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all appointments with optional filtering by date range and donor name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DonorServiceResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<DonorServiceResponseDTO<Page<AppointmentResponseDTO>>> findAppointmentsByOptionalDateRangeAndDonorName(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) String fullName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<AppointmentResponseDTO> results = appointmentService.findAppointmentsByOptionalDateRangeAndDonorName(startDate, endDate, fullName, pageable);

        DonorServiceResponseDTO<Page<AppointmentResponseDTO>> response = DonorServiceResponseDTO.ok(
                results,
                "Appointments fetched successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get appointments by donor ID with optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Appointments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DonorServiceResponseDTO.class)))
    })
    @GetMapping("donor/{donorId}")
    public ResponseEntity<DonorServiceResponseDTO<Page<AppointmentResponseDTO>>> findAppointmentsByDonorIdAndOptionalDateAndName(
            @PathVariable UUID donorId,
            @RequestParam(required = false) LocalDateTime appointmentDate,
            @RequestParam(required = false) String fullName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<AppointmentResponseDTO> results = appointmentService.findAppointmentsByDonorIdAndOptionalDateAndName(donorId, appointmentDate, fullName, pageable);

        DonorServiceResponseDTO<Page<AppointmentResponseDTO>> response = DonorServiceResponseDTO.ok(
                results,
                "Appointments fetched successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
