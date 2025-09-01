package pe.edu.vallegrande.projectPruebasApis.model.jsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchResponse {
    private String status;
    private List<JobData> data;
    private Integer num_pages;
    private Integer page;
    private Integer total_jobs;
}