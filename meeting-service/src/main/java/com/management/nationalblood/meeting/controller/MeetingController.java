package com.management.nationalblood.meeting.controller;

import com.management.nationalblood.meeting.dto.*;
import com.management.nationalblood.meeting.enums.MeetingStatus;
import com.management.nationalblood.meeting.exception.BadRequestException;
import com.management.nationalblood.meeting.exception.NotFoundException;
import com.management.nationalblood.meeting.service.impl.MeetingServiceImpl;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Meetings", description = "Endpoints for managing blood donation meetings")
@RestController
@RequestMapping("")
public class MeetingController {
    private final MeetingServiceImpl meetingService;

    public MeetingController(MeetingServiceImpl meetingService) {
        this.meetingService = meetingService;
    }

    @Operation(summary = "Create a new meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meeting created successfully",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - Organizer role required")
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    @PostMapping
    public ResponseEntity<MeetingResponseDTO<Map<String, Object>>> createMeeting(
            @Valid @RequestBody() CreateMeetingDTO createMeetingDTO,
            HttpServletRequest request
    ) {
        MeetingResponseDTO<Map<String, Object>> response = MeetingResponseDTO.ok(
                Map.of("meetingId", meetingService.createMeeting(createMeetingDTO)),
                "Meeting created successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get meeting by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Meeting not found")
    })
    @GetMapping("meeting/{meetingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_SUPER_USER', 'ROLE_ADMIN', 'ROLE_COUNSELOR', 'ROLE_LAB_TECHNICIAN', 'ROLE_ORGANIZER')")
    public ResponseEntity<MeetingResponseDTO<MeetingDTO>> getMeeting(
            @PathVariable UUID meetingId,
            HttpServletRequest request
    ) {
        MeetingResponseDTO<MeetingDTO> response = MeetingResponseDTO.ok(
                meetingService.getMeeting(meetingId),
                "Meeting retrieved successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get meetings by organizer with optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meetings retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDTO.class)))
    })
    @GetMapping("organizer/{organizerId}/meetings")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_SUPER_USER', 'ROLE_ADMIN', 'ROLE_COUNSELOR', 'ROLE_LAB_TECHNICIAN', 'ROLE_ORGANIZER')")
    public ResponseEntity<MeetingResponseDTO<Page<MeetingDTO>>> getMeetingsByOrganizer(
            @PathVariable UUID organizerId,
            @RequestParam(required = false) MeetingStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) LocalDateTime scheduledAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "scheduledAt") String sortBy,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<MeetingDTO> meetings = meetingService.getMeetingsByOrganizer(organizerId, status, location, scheduledAt, pageable);
        MeetingResponseDTO<Page<MeetingDTO>> response = MeetingResponseDTO.ok(
                meetings,
                "Meetings retrieved successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get meetings by staff with optional filtering")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meetings retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDTO.class)))
    })
    @GetMapping("staff/{staffId}/meetings")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_SUPER_USER', 'ROLE_ADMIN', 'ROLE_COUNSELOR', 'ROLE_LAB_TECHNICIAN', 'ROLE_ORGANIZER')")
    public ResponseEntity<MeetingResponseDTO<Page<MeetingDTO>>> getMeetingsByStaff(
            @PathVariable UUID staffId,
            @RequestParam(required = false) MeetingStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) LocalDateTime scheduledAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "scheduledAt") String sortBy,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<MeetingDTO> meetings = meetingService.getMeetingsByStaff(staffId, status, location, scheduledAt, pageable);
        MeetingResponseDTO<Page<MeetingDTO>> response = MeetingResponseDTO.ok(
                meetings,
                "Meetings retrieved successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Assign staff to meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff assigned successfully",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    @PostMapping("/{meetingId}/assign-staff")
    public ResponseEntity<MeetingResponseDTO<Map<String, Object>>> assignStaffToMeeting(
            @PathVariable UUID meetingId,
            @Valid @RequestBody MeetingStaffAssignmentDTO staffAssignmentDTO,
            HttpServletRequest request
    ) throws NotFoundException, BadRequestException {
        meetingService.assignStaffToMeeting(meetingId, staffAssignmentDTO);

        MeetingResponseDTO<Map<String, Object>> response = MeetingResponseDTO.ok(
                Map.of("meetingId", meetingId),
                "Staff assigned successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Remove staff from meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Staff removed successfully",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDTO.class)))
    })
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    @PostMapping("{meetingId}/remove-staff")
    public ResponseEntity<MeetingResponseDTO<Map<String, Object>>> removeStaffFromMeeting(
            @PathVariable UUID meetingId,
            @Valid @RequestBody MeetingStaffAssignmentDTO staffAssignmentDTO,
            HttpServletRequest request
    ) throws NotFoundException, BadRequestException {
        meetingService.removeStaffFromMeeting(meetingId, staffAssignmentDTO);

        MeetingResponseDTO<Map<String, Object>> response = MeetingResponseDTO.ok(
                Map.of("meetingId", meetingId),
                "Staff removed successfully",
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Reschedule meeting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting rescheduled successfully",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Meeting not found")
    })
    @PutMapping("{meetingId}/reschedule")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<MeetingResponseDTO<Map<String, Object>>> rescheduleMeeting(
            @PathVariable UUID meetingId,
            @Valid @RequestBody RescheduleMeetingDTO dto,
            HttpServletRequest request
    ) throws NotFoundException, BadRequestException {
        meetingService.rescheduleMeeting(meetingId, dto.getNewScheduledAt());
        return new ResponseEntity<>(
                MeetingResponseDTO.ok(
                        Map.of("meetingId", meetingId),
                        "Meeting rescheduled successfully",
                        request.getRequestURI()
                ),
                HttpStatus.OK
        );
    }

    @Operation(summary = "Update meeting status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meeting status updated successfully",
                    content = @Content(schema = @Schema(implementation = MeetingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Meeting not found")
    })
    @PutMapping("{meetingId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<MeetingResponseDTO<Map<String, Object>>> updateMeetingStatus(
            @PathVariable UUID meetingId,
            @Valid @RequestBody UpdateMeetingStatusDTO statusDTO,
            HttpServletRequest request
    ) throws NotFoundException, BadRequestException {
        meetingService.updateMeetingStatus(meetingId, statusDTO.getStatus());
        return new ResponseEntity<>(
                MeetingResponseDTO.ok(
                        Map.of("meetingId", meetingId),
                        "Meeting status updated to " + statusDTO.getStatus(),
                        request.getRequestURI()
                ),
                HttpStatus.OK
        );
    }

}
