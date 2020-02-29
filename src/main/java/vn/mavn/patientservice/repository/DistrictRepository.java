package vn.mavn.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

}
