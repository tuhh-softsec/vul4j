package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.Disease;

public interface DiseaseRepository extends BaseRepository<Disease, Long> {

  @Query("select d from Disease d where unaccent(d.name) = unaccent(:name)")
  Optional<Disease> findByName(String name);

  @Query("select d from Disease d where d.id = :id")
  Disease findDiseaseById(Long id);
}
