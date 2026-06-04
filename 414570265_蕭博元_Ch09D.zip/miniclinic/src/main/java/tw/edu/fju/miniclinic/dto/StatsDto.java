package tw.edu.fju.miniclinic.dto;

import java.util.Map;

/**
 * 統計摘要 DTO
 * 用於 /api/stats 端點的回傳資料
 */
public class StatsDto {
    private long totalDoctors;
    private long totalPatients;
    private long totalAppointments;
    private Map<String, Long> byStatus;

    public StatsDto() {}

    public StatsDto(long totalDoctors, long totalPatients, long totalAppointments, Map<String, Long> byStatus) {
        this.totalDoctors = totalDoctors;
        this.totalPatients = totalPatients;
        this.totalAppointments = totalAppointments;
        this.byStatus = byStatus;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Map<String, Long> getByStatus() {
        return byStatus;
    }

    public void setByStatus(Map<String, Long> byStatus) {
        this.byStatus = byStatus;
    }
}
