package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.Medicine;

public interface MedicineRepository extends BaseRepository<Medicine, Long> {

  @Query("select m from Medicine m where unaccent(m.name) = unaccent(:name)")
  Optional<Medicine> findByName(String name);

  @Query("select m from Medicine m where m.id =:id and m.isActive = true")
  Optional<Medicine> findActiveById(Long id);
}
