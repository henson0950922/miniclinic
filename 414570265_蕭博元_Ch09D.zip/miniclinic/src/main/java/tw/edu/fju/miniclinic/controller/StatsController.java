package tw.edu.fju.miniclinic.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PatientRepository;

@Controller
public class StatsController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @GetMapping("/stats")
    public String statsPage(Model model) {
        long doctorCount = doctorRepo.count();
        long patientCount = patientRepo.count();
        long appointmentCount = appointmentRepo.count();

        Map<String, Long> appointmentsByDepartment = appointmentRepo.findAll().stream()
                .collect(Collectors.groupingBy(appt -> appt.getDoctor().getDepartment(), Collectors.counting()));

        model.addAttribute("doctorCount", doctorCount);
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("appointmentCount", appointmentCount);
        model.addAttribute("appointmentsByDepartment", appointmentsByDepartment);
        return "stats";
    }
}
