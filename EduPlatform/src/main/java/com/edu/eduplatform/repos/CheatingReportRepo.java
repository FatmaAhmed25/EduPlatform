package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.CheatingReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheatingReportRepo extends JpaRepository<CheatingReport,Long> {

}
