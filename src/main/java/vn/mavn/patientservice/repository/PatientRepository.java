package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

  @Query("select p from Patient p where unaccent(p.name) =unaccent(:name) "
      + "and unaccent(p.address) =unaccent(:address) ")
  Optional<Patient> findByNameAndAdress(String name, String address);
}
