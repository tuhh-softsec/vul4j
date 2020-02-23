package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.entity.ConsultingStatus;

public interface ConsultingStatusService {

  Page<ConsultingStatus> getAllConsultingStatuses(Pageable pageable);
}
