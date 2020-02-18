package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.AdvertisingSource;

public interface AdvertisingSourceRepository extends JpaRepository<AdvertisingSource, Long>,
    JpaSpecificationExecutor<AdvertisingSource> {

  @Query("select a from AdvertisingSource a where upper(a.name) =:name ")
  Optional<AdvertisingSource> findByName(String name);

  @Query("select a from AdvertisingSource a where upper(a.name) =:name and a.id <> :id ")
  Optional<AdvertisingSource> findByNameNotEqualId(String name, Long id);

  @Modifying
  @Query("delete from AdvertisingSource ad where ad.id = :id")
  void deleteAdvert(Long id);

}
