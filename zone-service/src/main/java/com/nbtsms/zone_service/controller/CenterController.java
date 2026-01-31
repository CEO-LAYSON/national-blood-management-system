package com.nbtsms.zone_service.controller;

import com.nbtsms.zone_service.dto.CenterResponseDTO;
import com.nbtsms.zone_service.dto.CreateCenterDTO;
import com.nbtsms.zone_service.dto.ZoneIdDTO;
import com.nbtsms.zone_service.dto.ZoneResponseWrapperDTO;
import com.nbtsms.zone_service.service.impl.CenterServiceImpl;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Centers", description = "Endpoints for managing blood collection centers")
@RestController
@RequestMapping("centers")
public class CenterController {
    private final CenterServiceImpl centerService;

    public CenterController(CenterServiceImpl centerService) {
        this.centerService = centerService;
    }

    @Operation(summary = "Check if center exists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Center existence status")
    })
    @GetMapping("{centerId}/exists")
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    public boolean centerExists(@PathVariable("centerId") UUID centerId) {
        return centerService.centerExists(centerId);
    }

    @Operation(summary = "Get all centers by region with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Centers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ZoneResponseWrapperDTO.class)))
    })
    @GetMapping("{regionId}/region")
    public ResponseEntity<ZoneResponseWrapperDTO<Page<CenterResponseDTO>>> getAllByRegion(
            @PathVariable UUID regionId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        ZoneResponseWrapperDTO<Page<CenterResponseDTO>> response = ZoneResponseWrapperDTO.ok(
                centerService.getAllByRegion(name, regionId, pageable),
                "Center retrieved successful",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all centers by region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Centers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ZoneResponseWrapperDTO.class)))
    })
    @GetMapping("region/{regionId}/all")
    public ResponseEntity<ZoneResponseWrapperDTO<List<CenterResponseDTO>>> getAllCentersByRegion(
            @PathVariable UUID regionId,
            HttpServletRequest request
    ) {
        List<CenterResponseDTO> centers = centerService.getCentersByRegion(regionId);
        ZoneResponseWrapperDTO<List<CenterResponseDTO>> response = ZoneResponseWrapperDTO.ok(
                centers,
                "Centers retrieved successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(summary = "Create a new center")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Center created successfully",
                    content = @Content(schema = @Schema(implementation = ZoneResponseWrapperDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ZoneResponseWrapperDTO<Map<String, Object>>> createCenter(
            @Valid @RequestBody CreateCenterDTO createCenterDTO,
            HttpServletRequest request
    ) {
        ZoneResponseWrapperDTO<Map<String, Object>> response = ZoneResponseWrapperDTO.ok(
                Map.of("centerId", centerService.create(createCenterDTO)),
                "Center added successful to region",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all centers by zone with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Centers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ZoneResponseWrapperDTO.class)))
    })
    @GetMapping("{zoneId}/zone")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_USER')")
    public ResponseEntity<ZoneResponseWrapperDTO<Page<CenterResponseDTO>>> getAllCentersByZone(
            @PathVariable UUID zoneId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            HttpServletRequest request
            ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        ZoneResponseWrapperDTO<Page<CenterResponseDTO>> response = ZoneResponseWrapperDTO.ok(
                centerService.getAllCenterByZoneId(zoneId, name, pageable),
                "Center retrieved successful",
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @Operation(summary = "Get center by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Center retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ZoneResponseWrapperDTO.class))),
            @ApiResponse(responseCode = "404", description = "Center not found")
    })
    @GetMapping("{centerId}/center")
    public ResponseEntity<ZoneResponseWrapperDTO<CenterResponseDTO>> getCenter(
            @PathVariable UUID centerId,
            HttpServletRequest request
    ) {
        ZoneResponseWrapperDTO<CenterResponseDTO> response = ZoneResponseWrapperDTO.ok(
                centerService.getCenter(centerId),
                "Center retrieved successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Check if center belongs to zone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Center zone association status")
    })
    @PreAuthorize("hasAuthority('ROLE_INTERNAL')")
    @GetMapping("{zoneId}/center/{centerId}/is-associated")
    public boolean centerBelongsToZone(
            @PathVariable("zoneId") UUID zoneId,
            @PathVariable("centerId") UUID centerId
    ) {
        return centerService.centerBelongToZone(zoneId, centerId);
    }

}
