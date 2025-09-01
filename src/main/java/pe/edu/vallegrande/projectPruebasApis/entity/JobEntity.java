package pe.edu.vallegrande.projectPruebasApis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("jobs")
public class JobEntity {
    @Id
    private Long id;
    
    @Column("job_id")
    private String jobId;
    
    @Column("employer_name")
    private String employerName;
    
    @Column("job_title")
    private String jobTitle;
    
    @Column("job_description")
    private String jobDescription;
    
    @Column("job_country")
    private String jobCountry;
    
    @Column("job_city")
    private String jobCity;
    
    @Column("job_posted_at")
    private String jobPostedAt;
    
    @Column("job_apply_link")
    private String jobApplyLink;
    
    @Column("job_employment_type")
    private String jobEmploymentType;
    
    @Column("search_date")
    private String searchDate;
}