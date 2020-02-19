package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.Disease;

public interface DiseaseRepository extends JpaRepository<Disease, Long>,
    JpaSpecificationExecutor<Disease> {

  @Query("select d from Disease d where unaccent(d.name) = unaccent(:name)")
  Optional<Disease> findByName(String name);
}
