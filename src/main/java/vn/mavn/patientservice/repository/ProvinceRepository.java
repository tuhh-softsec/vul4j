package vn.mavn.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.Province;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {

}
