package com.nbts.management.donor_service.controller;

import com.nbts.management.donor_service.dto.CreateDonorDTO;
import com.nbts.management.donor_service.dto.DonorResponseDTO;
import com.nbts.management.donor_service.dto.DonorServiceResponseDTO;
import com.nbts.management.donor_service.dto.UpdateDonorDTO;
import com.nbts.management.donor_service.enums.Gender;
import com.nbts.management.donor_service.exception.UnauthorizedAccessException;
import com.nbts.management.donor_service.service.impl.DonorServiceImpl;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Donors", description = "Endpoints for managing donors")
@RestController
@RequestMapping("")
public class DonorController {

    private final DonorServiceImpl donorService;

    public DonorController(DonorServiceImpl donorService) {
        this.donorService = donorService;
    }

    private UUID getDonorIdFromRequest(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedAccessException("User ID not found in request attributes");
        }
        return UUID.fromString(userId);
    }

    @Operation(summary = "Create a new donor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Donor created successfully",
                    content = @Content(schema = @Schema(implementation = DonorServiceResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<DonorServiceResponseDTO<Map<String, Object>>> create(
            @Valid @RequestBody CreateDonorDTO createDonorDTO,
            HttpServletRequest request
    ) {
        DonorServiceResponseDTO<Map<String, Object>> response = DonorServiceResponseDTO.ok(
                Map.of("donorId", donorService.createDonor(createDonorDTO)),
                "Donor created successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update donor information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donor updated successfully",
                    content = @Content(schema = @Schema(implementation = DonorServiceResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping
    public ResponseEntity<DonorServiceResponseDTO<Map<String, Object>>> updateDonor(
            @Valid @RequestBody UpdateDonorDTO updateDonorDTO,
            HttpServletRequest request
    ) {
        donorService.updateDonor(updateDonorDTO);

        DonorServiceResponseDTO<Map<String, Object>> response = DonorServiceResponseDTO.ok(
                null,
                "Donor updated successfully",
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all donors with optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donors retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DonorServiceResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<DonorServiceResponseDTO<Page<DonorResponseDTO>>> getAllDonors(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) Gender gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        DonorServiceResponseDTO<Page<DonorResponseDTO>> response = DonorServiceResponseDTO.ok(
                donorService.getAllDonors(pageable, fullName, gender),
                "Donor fetched successfully",
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get donor by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donor retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DonorServiceResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Donor not found")
    })
    @GetMapping("{donorId}/donor")
    public ResponseEntity<DonorServiceResponseDTO<DonorResponseDTO>> getDonorById(
            @PathVariable UUID donorId,
            HttpServletRequest request
    ) {
        DonorServiceResponseDTO<DonorResponseDTO> response = DonorServiceResponseDTO.ok(
                donorService.getDonor(donorId),
                "Donor retrieved successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get logged-in donor information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donor information retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DonorServiceResponseDTO.class)))
    })
    @GetMapping("donor")
    public ResponseEntity<DonorServiceResponseDTO<DonorResponseDTO>> getLoggedInDonorInfo(
            HttpServletRequest request
    ) {
        UUID donorId = getDonorIdFromRequest(request);
        DonorServiceResponseDTO<DonorResponseDTO> response = DonorServiceResponseDTO.ok(
                donorService.getDonor(donorId),
                "Donor information retrieved successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Check if donor exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Donor existence status")
    })
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    @GetMapping("{donorId}/exists")
    public boolean checkDonorExists(@PathVariable UUID donorId) {
        return donorService.donorExists(donorId);
    }
}
