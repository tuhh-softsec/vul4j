package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long>,
    JpaSpecificationExecutor<Patient> {

  @Query("select p from Patient p where p.id =:id")
  Optional<Patient> findActiveById(Long id);

  @Query("select p from Patient p where p.id =:id")
  Patient findByIdForGetData(Long id);

  @Modifying
  @Query("delete from Patient p where p.id =:id")
  void deletePatient(Long id);
}
