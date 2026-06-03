package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.edu.fju.miniclinic.model.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentApiController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

  
    @GetMapping
    public List<Appointment> getAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctorId) {
        
        // 情況 A：依日期篩選
        if (date != null && !date.isBlank()) {
            LocalDate apptDate = LocalDate.parse(date);
            return appointmentRepo.findByApptDate(apptDate);
        }
        
        // 情況 B：依醫師篩選
        if (doctorId != null && !doctorId.isBlank()) {
            Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
            if (doctor != null) {
                return appointmentRepo.findByDoctor(doctor);
            }
            return List.of(); // 找不到醫師就回傳空陣列
        }
        
        // 情況 C：無參數，查全部
        return appointmentRepo.findAll();
    }

    /**
     * 2. 回傳總掛號數
     */
    @GetMapping("/count")
    public long getAppointmentCount() {
        return appointmentRepo.count();
    }

    /**
     * 3. 建立新掛號
     */
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Map<String, String> request) {
        String chartNo = request.get("chartNo");
        String doctorId = request.get("doctorId");
        
        if (request.get("apptDate") == null) {
            return ResponseEntity.badRequest().build();
        }
        LocalDate apptDate = LocalDate.parse(request.get("apptDate"));
        String timeSlot = request.get("timeSlot");

        Patient patient = patientRepo.findById(chartNo).orElse(null);
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (patient == null || doctor == null) {
            return ResponseEntity.badRequest().build();
        }

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setApptDate(apptDate);
        appt.setTimeSlot(timeSlot);
        appt.setStatus("BOOKED");

        Appointment saved = appointmentRepo.save(appt);
        return ResponseEntity.status(201).body(saved);
    }

    /**
     * 4. 修改掛號狀態
     * 你提供的代碼：包含 Session 安全性檢查與狀態合法性驗證
     */
    @RequestMapping(value = "/{apptId}/status", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Appointment> updateStatus(
            @PathVariable Long apptId,
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        // 從 Session 中取得當前登入的醫師 ID
        String loggedInDoctorId = (String) session.getAttribute("loggedInDoctorId");

        // 檢查掛號是否存在
        Appointment appt = appointmentRepo.findById(apptId).orElse(null);
        if (appt == null) {
            return ResponseEntity.notFound().build(); // 404
        }

        // 安全檢查：權限驗證，醫師只能修改屬於自己的掛號
        if (!appt.getDoctor().getDoctorId().equals(loggedInDoctorId)) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        // 檢查欲修改的狀態字串是否合法
        String newStatus = payload.get("status");
        if (newStatus == null || !List.of("BOOKED", "COMPLETED", "CANCELLED").contains(newStatus)) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }

        // 更新並儲存
        appt.setStatus(newStatus);
        return ResponseEntity.ok(appointmentRepo.save(appt)); // 200 OK
    }
}