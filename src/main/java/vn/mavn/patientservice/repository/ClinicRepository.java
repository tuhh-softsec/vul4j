package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.Clinic;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long>,
    JpaSpecificationExecutor<Clinic> {

  Optional<Clinic> findByName(String name);

  Optional<Clinic> findByPhone(String phone);

  Optional<Clinic> findByNameAndIdNot(String name, Long id);

  Optional<Clinic> findByPhoneAndIdNot(String phone, Long id);
}
