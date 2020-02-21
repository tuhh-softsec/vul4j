package vn.mavn.patientservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.ClinicUser;

@Repository
public interface ClinicUserRepository extends JpaRepository<ClinicUser, Long> {

  @Query("select cu.id from ClinicUser cu where cu.clinicId = :clinicId")
  List<Long> findAllClinicById(Long clinicId);
}
