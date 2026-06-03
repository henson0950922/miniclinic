package tw.edu.fju.miniclinic;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAppointment_returnsCreatedAppointment() throws Exception {
        String payload = """
                {
                  \"chartNo\": \"TEST00001\",
                  \"doctorId\": \"D001\",
                  \"apptDate\": \"2026-05-10\",
                  \"timeSlot\": \"PM\"
                }
                """;

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.apptId").exists())
                .andExpect(jsonPath("$.patient.chartNo").value("TEST00001"))
                .andExpect(jsonPath("$.doctor.doctorId").value("D001"))
                .andExpect(jsonPath("$.apptDate").value("2026-05-10"))
                .andExpect(jsonPath("$.timeSlot").value("PM"))
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void updateAppointmentStatus_returnsUpdatedAppointment() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInDoctorId", "D001");

        String payload = """
                {
                  \"status\": \"COMPLETED\"
                }
                """;

        mockMvc.perform(patch("/api/appointments/1/status")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apptId").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
