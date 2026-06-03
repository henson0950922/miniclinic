package tw.edu.fju.miniclinic.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.PasswordForm;

@Controller
public class PasswordController {

    @Autowired
    private DoctorRepository doctorRepo;

    @GetMapping("/password")
    public String passwordForm(HttpSession session, Model model) {
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordForm());
        }
        model.addAttribute("loggedInDoctorName", session.getAttribute("loggedInDoctorName"));
        return "password";
    }

    @PostMapping("/password")
    public String changePassword(
            @Valid @ModelAttribute("passwordForm") PasswordForm form,
            BindingResult result,
            HttpSession session,
            Model model) {

        String doctorId = (String) session.getAttribute("loggedInDoctorId");
        Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

        if (doctor == null) {
            session.invalidate();
            return "redirect:/login";
        }

        if (!result.hasErrors()) {
            String oldPassword = form.getOldPassword();
            String newPassword = form.getNewPassword();
            String confirmPassword = form.getConfirmPassword();

            if (oldPassword == null || !BCrypt.checkpw(oldPassword, doctor.getPasswordHash())) {
                result.rejectValue("oldPassword", "invalid.oldPassword", "舊密碼錯誤");
            }
            if (newPassword == null || confirmPassword == null || !newPassword.equals(confirmPassword)) {
                result.rejectValue("confirmPassword", "mismatch.confirmPassword", "兩次密碼不相符");
            }
            if (newPassword == null || newPassword.length() < 8) {
                result.rejectValue("newPassword", "short.newPassword", "密碼至少需要 8 個字元");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("loggedInDoctorName", doctor.getName());
            return "password";
        }

        String newPassword = form.getNewPassword();
        doctor.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        doctorRepo.save(doctor);

        // 密碼修改完成後，強制清除 Session (登出)
        session.invalidate();
        
        // 導回登入頁面，讓使用者用新密碼重新登入
        return "redirect:/login";
    }
}
