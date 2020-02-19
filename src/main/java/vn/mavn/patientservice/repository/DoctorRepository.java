package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

  Optional<Doctor> findByName(String name);

  Optional<Doctor> findByNameAndIdNot(String name, Long id);

  Optional<Doctor> findByPhone(String phone);

  Optional<Doctor> findByPhoneAndIdNot(String phone, Long id);
}
