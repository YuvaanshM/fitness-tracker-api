package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.AddRoutineExerciseRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateRoutineDayRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateRoutineRequest;
import com.yuvaansh.fitness_tracker_api.dto.RoutineResponse;
import com.yuvaansh.fitness_tracker_api.service.RoutineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    private final RoutineService routineService;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @PostMapping
    public ResponseEntity<RoutineResponse> createRoutine(
            Principal principal,
            @Valid @RequestBody CreateRoutineRequest request) {
        RoutineResponse response = routineService.createRoutine(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RoutineResponse>> listRoutines(Principal principal) {
        return ResponseEntity.ok(routineService.listRoutines(principal));
    }

    @GetMapping("/{routineId}")
    public ResponseEntity<RoutineResponse> getRoutine(
            Principal principal,
            @PathVariable Long routineId) {
        return ResponseEntity.ok(routineService.getRoutine(principal, routineId));
    }

    @PutMapping("/{routineId}")
    public ResponseEntity<RoutineResponse> updateRoutine(
            Principal principal,
            @PathVariable Long routineId,
            @Valid @RequestBody CreateRoutineRequest request) {
        return ResponseEntity.ok(routineService.updateRoutine(principal, routineId, request));
    }

    @DeleteMapping("/{routineId}")
    public ResponseEntity<Void> deleteRoutine(
            Principal principal,
            @PathVariable Long routineId) {
        routineService.deleteRoutine(principal, routineId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{routineId}/days")
    public ResponseEntity<RoutineResponse> addDay(
            Principal principal,
            @PathVariable Long routineId,
            @Valid @RequestBody CreateRoutineDayRequest request) {
        RoutineResponse response = routineService.addDay(principal, routineId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{routineId}/days/{dayId}/exercises")
    public ResponseEntity<RoutineResponse> addExercise(
            Principal principal,
            @PathVariable Long routineId,
            @PathVariable Long dayId,
            @Valid @RequestBody AddRoutineExerciseRequest request) {
        RoutineResponse response = routineService.addExercise(principal, routineId, dayId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
