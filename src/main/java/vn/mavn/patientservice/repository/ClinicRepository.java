package vn.mavn.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.Clinic;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long>,
    JpaSpecificationExecutor<Clinic> {

}
