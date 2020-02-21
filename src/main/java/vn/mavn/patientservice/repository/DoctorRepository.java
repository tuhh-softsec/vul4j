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

  @Query("select d from Doctor d where unaccent(d.name) = unaccent(:name)")
  Optional<Doctor> findByName(String name);

  @Query("select d from Doctor d where unaccent(d.name) = unaccent(:name) and d.id <> :id")
  Optional<Doctor> findByNameAndIdNot(String name, Long id);

  @Query("select d from Doctor d where d.phone = :phone")
  Optional<Doctor> findByPhone(String phone);

  @Query("select d from Doctor d where d.phone = :phone and d.id <> :id")
  Optional<Doctor> findByPhoneAndIdNot(String phone, Long id);

  @Modifying
  @Query("delete from Doctor ad where ad.id = :id")
  void deleteDoctor(Long id);

  Doctor findDoctorById(Long id);

  @Query("select d from Doctor d where d.id =:id and d.isActive = true")
  Doctor findByIdForGetData(Long id);

}
