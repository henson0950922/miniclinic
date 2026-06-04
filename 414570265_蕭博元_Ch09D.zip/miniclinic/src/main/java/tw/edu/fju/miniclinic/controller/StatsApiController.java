package tw.edu.fju.miniclinic.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.edu.fju.miniclinic.dto.StatsDto;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

/**
 * 統計摘要 REST API Controller
 * 提供 /api/stats 端點，不需要認證
 */
@RestController
@RequestMapping("/api/stats")
public class StatsApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    /**
     * GET /api/stats
     * 回傳系統統計摘要：醫師總數、病患總數、掛號總數、按狀態分類的掛號數
     * 不需要認證
     */
    @GetMapping
    public StatsDto getStats() {
        long totalDoctors = doctorRepo.count();
        long totalPatients = patientRepo.count();
        long totalAppointments = appointmentRepo.count();

        // 按狀態分類計算掛號數
        Map<String, Long> byStatus = appointmentRepo.findAll().stream()
                .collect(Collectors.groupingBy(appt -> appt.getStatus(), Collectors.counting()));

        // 確保包含所有可能的狀態，即使數量為 0
        Map<String, Long> result = new HashMap<>();
        result.put("BOOKED", byStatus.getOrDefault("BOOKED", 0L));
        result.put("COMPLETED", byStatus.getOrDefault("COMPLETED", 0L));
        result.put("CANCELLED", byStatus.getOrDefault("CANCELLED", 0L));

        return new StatsDto(totalDoctors, totalPatients, totalAppointments, result);
    }
}
