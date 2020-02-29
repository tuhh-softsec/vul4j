package vn.mavn.patientservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

  @Query("select d from District d where d.provinceId =:provinceId")
  List<District> findAllByProvinceId(Long provinceId);
}
