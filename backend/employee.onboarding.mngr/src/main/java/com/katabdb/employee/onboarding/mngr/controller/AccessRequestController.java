package com.katabdb.employee.onboarding.mngr.controller;

import com.katabdb.employee.onboarding.mngr.domain.entities.AccessRequestEntity;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsRequest;
import com.katabdb.employee.onboarding.mngr.dto.accessRequests.AccessRequestsResponse;
import com.katabdb.employee.onboarding.mngr.services.spec.IAccessRequestsQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/access-requests")
public class AccessRequestController {

    private final IAccessRequestsQueryService accessRequestsQueryService;

    @Autowired
    public AccessRequestController(IAccessRequestsQueryService accessRequestsQueryService) {
        this.accessRequestsQueryService = accessRequestsQueryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccessRequestsResponse> getById(@Validated @PathVariable Integer id) {
        return ResponseEntity.ok(accessRequestsQueryService.getAccessRequestById(id));
    }

    @GetMapping
    public List<AccessRequestsResponse> getAll() {
        return accessRequestsQueryService.getAllAccessRequests();
    }

    @PostMapping
    public ResponseEntity<AccessRequestsResponse> createAccessRequest(@RequestBody @Validated AccessRequestsRequest request) {
        AccessRequestsResponse created = accessRequestsQueryService.createAccessRequest(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccessRequestsResponse> updateAccessRequest(
            @PathVariable Integer id,
            @RequestBody @Validated AccessRequestsRequest accessRequestsRequest) {
        AccessRequestsResponse updated = accessRequestsQueryService.updateAccessRequest(id, accessRequestsRequest);
        return ResponseEntity.ok(updated);
    }
}
