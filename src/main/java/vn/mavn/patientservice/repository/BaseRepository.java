package vn.mavn.patientservice.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>,
    JpaSpecificationExecutor<T> {

}
