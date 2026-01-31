package com.nbtsms.zone_service.controller;

import com.nbtsms.zone_service.dto.CreateRegionDTO;
import com.nbtsms.zone_service.dto.RegionResponseDTO;
import com.nbtsms.zone_service.dto.ZoneResponseWrapperDTO;
import com.nbtsms.zone_service.service.impl.RegionServiceImpl;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Regions", description = "Endpoints for managing regions within zones")
@RestController
@RequestMapping("regions")
public class RegionController {
    private final RegionServiceImpl regionService;

    public RegionController(RegionServiceImpl regionService) {
        this.regionService = regionService;
    }

    @Operation(summary = "Check if region exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Region existence status")
    })
    @GetMapping("{regionId}/exists")
    public boolean regionExists(@PathVariable("regionId") UUID regionId) {
        return regionService.regionExists(regionId);
    }

    @Operation(summary = "Create a new region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Region created successfully",
                    content = @Content(schema = @Schema(implementation = ZoneResponseWrapperDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_USER')")
    public ResponseEntity<ZoneResponseWrapperDTO<Map<String, Object>>> createRegion(
            @Valid @RequestBody CreateRegionDTO createRegionDTO,
            HttpServletRequest request
    ) {
        ZoneResponseWrapperDTO<Map<String, Object>> response = ZoneResponseWrapperDTO.ok(
                Map.of("regionId", regionService.create(createRegionDTO)),
                "Region added successfully to the zone",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all regions by zone with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Regions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ZoneResponseWrapperDTO.class)))
    })
    @GetMapping("{zoneId}/zone")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_SUPER_USER', 'ROLE_ADMIN', 'ROLE_COUNSELOR', 'ROLE_LAB_TECHNICIAN', 'ROLE_ORGANIZER')")
    public ResponseEntity<ZoneResponseWrapperDTO<Page<RegionResponseDTO>>> getAllByZone(
            @PathVariable UUID zoneId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        ZoneResponseWrapperDTO<Page<RegionResponseDTO>> response = ZoneResponseWrapperDTO.ok(
                regionService.getAllByZone(name, zoneId, pageable),
                "Regions retrieved successful",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all regions by zone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Regions retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ZoneResponseWrapperDTO.class)))
    })
    @GetMapping("region/{zoneId}/all")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_SUPER_USER', 'ROLE_ADMIN', 'ROLE_COUNSELOR', 'ROLE_LAB_TECHNICIAN', 'ROLE_ORGANIZER')")
    public ResponseEntity<ZoneResponseWrapperDTO<List<RegionResponseDTO>>> getRegionsByZoneId(
            @PathVariable UUID zoneId,
            HttpServletRequest request
    ) {
        ZoneResponseWrapperDTO<List<RegionResponseDTO>> response = ZoneResponseWrapperDTO.ok(
                regionService.getRegionsByZone(zoneId),
                "Regions retrieved successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get region by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Region retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ZoneResponseWrapperDTO.class))),
            @ApiResponse(responseCode = "404", description = "Region not found")
    })
    @GetMapping("{regionId}/region")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_SUPER_USER', 'ROLE_ADMIN', 'ROLE_COUNSELOR', 'ROLE_LAB_TECHNICIAN', 'ROLE_ORGANIZER')")
    public ResponseEntity<ZoneResponseWrapperDTO<RegionResponseDTO>> getRegionById(
            @PathVariable UUID regionId,
            HttpServletRequest request
    ) {
        ZoneResponseWrapperDTO<RegionResponseDTO> response = ZoneResponseWrapperDTO.ok(
                regionService.getRegionById(regionId),
                "Region retrieved successful",
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
