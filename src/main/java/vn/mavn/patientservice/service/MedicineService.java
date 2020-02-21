package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.MedicineAddDto;
import vn.mavn.patientservice.dto.MedicineDto;
import vn.mavn.patientservice.dto.MedicineEditDto;
import vn.mavn.patientservice.dto.qobject.QueryMedicineDto;
import vn.mavn.patientservice.entity.Medicine;

public interface MedicineService {

  Page<Medicine> getAllMedicines(QueryMedicineDto data, Pageable pageable);

  Medicine add(MedicineAddDto data);

  Medicine update(MedicineEditDto data);

  MedicineDto detail(Long id);

  void remove(Long id);

}
