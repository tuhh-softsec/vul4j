package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>,
    JpaSpecificationExecutor<Doctor> {

  Optional<Doctor> findByName(String name);

  Optional<Doctor> findByNameAndIdNot(String name, Long id);

  Optional<Doctor> findByPhone(String phone);

  Optional<Doctor> findByPhoneAndIdNot(String phone, Long id);

  @Modifying
  @Query("delete from Doctor ad where ad.id = :id")
  void deleteDoctor(Long id);
}
