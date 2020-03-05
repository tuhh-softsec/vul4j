package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.mavn.patientservice.entity.ClinicBranch;

@Repository
public interface ClinicBranchRepository extends JpaRepository<ClinicBranch, Long>,
    JpaSpecificationExecutor<ClinicBranch> {

  @Query("select c from ClinicBranch c where unaccent(c.name) = unaccent(:name)")
  Optional<ClinicBranch> findByName(String name);

  @Query("select c from ClinicBranch c where unaccent(c.name) = unaccent(:name) and c.id <> :id")
  Optional<ClinicBranch> findByNameAndIdNot(String name, Long id);

  @Query("select c from ClinicBranch c where c.id =:id ")
  ClinicBranch findClinicBranchById(Long id);
}
