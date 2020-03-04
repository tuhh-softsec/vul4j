package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.Pathology;

public interface PathologyRepository extends BaseRepository<Pathology, Long> {

  @Query("select p from Pathology p where p.name = :name")
  Optional<Pathology> findByName(String name);
}
