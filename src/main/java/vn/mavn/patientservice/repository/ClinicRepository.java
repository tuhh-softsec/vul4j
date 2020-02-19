package vn.mavn.patientservice.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.Clinic;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long>,
    JpaSpecificationExecutor<Clinic> {

  @Query("select c from Clinic c where unaccent(c.name) = unaccent(:name)")
  Optional<Clinic> findByName(String name);

  @Query("select c from Clinic c where c.phone = :phone")
  Optional<Clinic> findByPhone(String phone);

  @Query("select c from Clinic c where unaccent(c.name) = unaccent(:name) and c.id <> :id")
  Optional<Clinic> findByNameAndIdNot(String name, Long id);

  @Query("select c from Clinic c where c.phone = :phone and c.id <> :id ")
  Optional<Clinic> findByPhoneAndIdNot(String phone, Long id);

  @Query("select c from Clinic c where c.doctorId = :doctorId")
  List<Clinic> findAllClinicById(Long doctorId);
}
