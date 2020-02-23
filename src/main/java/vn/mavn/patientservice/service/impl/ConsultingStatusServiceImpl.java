package vn.mavn.patientservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mavn.patientservice.entity.ConsultingStatus;
import vn.mavn.patientservice.repository.ConsultingStatusRepository;
import vn.mavn.patientservice.service.ConsultingStatusService;

@Service
public class ConsultingStatusServiceImpl implements ConsultingStatusService {

  @Autowired
  private ConsultingStatusRepository consultingStatusRepository;

  @Override
  public Page<ConsultingStatus> getAllConsultingStatuses(Pageable pageable) {
    return consultingStatusRepository.findAll(pageable);
  }
}
