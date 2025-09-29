package pe.edu.vallegrande.projectPruebasApis.model.jsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {
    
    @NotBlank(message = "Job ID cannot be blank")
    private String jobId;
    
    @NotBlank(message = "Employer name cannot be blank")
    private String employerName;
    
    @NotBlank(message = "Job title cannot be blank")
    private String jobTitle;
    
    private String jobDescription;
    
    private String jobCountry;
    
    private String jobCity;
    
    private String jobPostedAt;
    
    private String jobApplyLink;
    
    private String jobEmploymentType;
}