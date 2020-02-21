package vn.mavn.patientservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.ClinicUser;

@Repository
public interface ClinicUserRepository extends JpaRepository<ClinicUser, Long> {

  @Query("select cu from ClinicUser cu where cu.clinicId = :clinicId")
  List<ClinicUser> findAllClinicById(Long clinicId);

  @Modifying
  @Query("delete from ClinicUser cu where cu.clinicId =:clinicId")
  void deleteAllByClinicId(Long clinicId);

  @Query("select cu.userId from ClinicUser cu where cu.clinicId = :clinicId")
  List<Long> findAllUserIdByClinicId(Long clinicId);

  @Query("select cu from ClinicUser cu where cu.userId = :userId")
  List<ClinicUser> findAllClinicByUserId(Long userId);
}
