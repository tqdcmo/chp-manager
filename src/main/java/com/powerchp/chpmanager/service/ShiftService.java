package com.powerchp.chpmanager.service;

import com.powerchp.chpmanager.model.Shift;
import com.powerchp.chpmanager.repository.ShiftRepository;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import java.time.LocalDateTime;
=======
>>>>>>> origin/main
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public List<Shift> findAllShifts() {
        return shiftRepository.findAll();
    }

    public List<Shift> findShiftsByOperatorName(String operatorName) {
        return shiftRepository.findByOperatorName(operatorName);
    }

    public Shift saveShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    public void deleteShift(Shift shift) {
        shiftRepository.delete(shift);
    }

    public Shift findById(Long id) {
        return shiftRepository.findById(id).orElse(null);
    }
<<<<<<< HEAD
    public boolean hasOverlappingShift(String operatorName, LocalDateTime start, LocalDateTime end, Long excludeId) {
        return shiftRepository.findByOperatorName(operatorName).stream()
                .filter(shift -> !shift.getId().equals(excludeId))
                .anyMatch(shift ->
                        (start.isBefore(shift.getEndTime()) && end.isAfter(shift.getStartTime()))
                );
    }

=======
>>>>>>> origin/main
}
