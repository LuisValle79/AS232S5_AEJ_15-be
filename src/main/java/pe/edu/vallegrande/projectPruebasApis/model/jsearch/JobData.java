package pe.edu.vallegrande.projectPruebasApis.model.jsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobData {
    private String job_id;
    private String employer_name;
    private String job_title;
    private String job_description;
    private String job_country;
    private String job_city;
    private String job_posted_at_datetime_utc;
    private String job_apply_link;
    private String job_employment_type;
}