package pe.edu.vallegrande.projectPruebasApis.model.jsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchResponse {
    private String status;
    private String request_id;
    private Map<String, Object> parameters; // Para manejar el objeto parameters
    private List<JobData> data;
    private Integer num_pages;
    private Integer page;
    private Integer total_jobs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobData {
        private String job_id;
        private String job_title;
        private String employer_name;
        private String employer_logo;
        private String employer_website;
        private String job_publisher;
        private String job_employment_type;
        private List<String> job_employment_types;
        private String job_apply_link;
        private Boolean job_apply_is_direct;
        private List<ApplyOption> apply_options;
        private String job_description;
        private Boolean job_is_remote;
        private String job_posted_at;
        private Long job_posted_at_timestamp;
        private String job_posted_at_datetime_utc;
        private String job_location;
        private String job_city;
        private String job_state;
        private String job_country;
        private Double job_latitude;
        private Double job_longitude;
        private List<String> job_benefits;
        private String job_google_link;
        private Double job_min_salary;
        private Double job_max_salary;
        private String job_salary_period;
        private JobHighlights job_highlights;
        private String job_onet_soc;
        private String job_onet_job_zone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyOption {
        private String publisher;
        private String apply_link;
        private Boolean is_direct;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobHighlights {
        private List<String> Qualifications;
        private List<String> Benefits;
        private List<String> Responsibilities;
    }
}