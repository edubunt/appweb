package com.application.appweb.controller;

import com.application.appweb.dto.request.MembroRequest;
import com.application.appweb.dto.response.ApiResponse;
import com.application.appweb.dto.response.MembroResponse;
import com.application.appweb.service.MembroService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membros")
@Slf4j
public class MembroController {

    private final MembroService membroService;

    public MembroController(MembroService membroService) {
        this.membroService = membroService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MembroResponse>>> getAllMembros() {
        log.info("Fetching all members");
        List<MembroResponse> membros = membroService.getAllMembros().stream()
                .map(MembroResponse::fromMembro)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(membros, "Members retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<MembroResponse>> getMembroById(@PathVariable Long id) {
        log.info("Fetching member with id: {}", id);
        MembroResponse membro = MembroResponse.fromMembro(membroService.findMembroById(id));
        return ResponseEntity.ok(ApiResponse.success(membro, "Member retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembroResponse>> createMembro(@Valid @RequestBody MembroRequest membroRequest) {
        log.info("Creating new member: {}", membroRequest.nome());
        MembroResponse membro = MembroResponse.fromMembro(membroService.creatMembro(membroRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(membro, "Member created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MembroResponse>> updateMembro(@PathVariable Long id, @Valid @RequestBody MembroRequest membroRequest) {
        log.info("Updating member with id: {}", id);
        MembroResponse membro = MembroResponse.fromMembro(membroService.updateMembro(id, membroRequest));
        return ResponseEntity.ok(ApiResponse.success(membro, "Member updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMembro(@PathVariable Long id) {
        log.info("Deleting member with id: {}", id);
        membroService.deleteMembro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/age-range")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MembroResponse>>> getMembrosByAge(
            @RequestParam int minAge,
            @RequestParam int maxAge) {
        log.info("Fetching members between ages {} and {}", minAge, maxAge);
        List<MembroResponse> membros = membroService.getMembrosByIdade(minAge, maxAge).stream()
                .map(MembroResponse::fromMembro)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(membros, "Members retrieved successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MembroResponse>>> getMembrosByNome(@RequestParam String nome) {
        log.info("Searching members by name: {}", nome);
        List<MembroResponse> membros = membroService.getMembrosByNome(nome).stream()
                .map(MembroResponse::fromMembro)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(membros, "Members found successfully"));
    }
}
