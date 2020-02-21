package vn.mavn.patientservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.ClinicAddDto;
import vn.mavn.patientservice.dto.ClinicDto;
import vn.mavn.patientservice.dto.ClinicDto.DoctorDto;
import vn.mavn.patientservice.dto.ClinicEditDto;
import vn.mavn.patientservice.dto.DiseaseDto;
import vn.mavn.patientservice.entity.Clinic;
import vn.mavn.patientservice.entity.ClinicDisease;
import vn.mavn.patientservice.entity.Disease;
import vn.mavn.patientservice.entity.Doctor;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.ClinicDiseaseRepository;
import vn.mavn.patientservice.repository.ClinicRepository;
import vn.mavn.patientservice.repository.ClinicUserRepository;
import vn.mavn.patientservice.repository.DiseaseRepository;
import vn.mavn.patientservice.repository.DoctorRepository;
import vn.mavn.patientservice.repository.spec.ClinicSpec;
import vn.mavn.patientservice.service.ClinicService;

@Service
@Transactional
public class ClinicServiceImpl implements ClinicService {

  @Autowired
  private ClinicRepository clinicRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @Autowired
  private DiseaseRepository diseaseRepository;

  @Autowired
  private ClinicDiseaseRepository clinicDiseaseRepository;

  @Autowired
  private ClinicUserRepository clinicUserRepository;

  @Override
  public Clinic save(ClinicAddDto data) {

    Clinic clinic = new Clinic();
    //valid name and phone
    validationNameOrPhoneWhenAddClinic(data);

    //valid doctor
    validDoctor(data.getDoctorId());

    BeanUtils.copyProperties(data, clinic);
    clinic.setName(data.getName().trim());
    clinicRepository.save(clinic);

    //valid disease
    validDisease(clinic, data.getDiseaseIds());

    return clinic;
  }

  @Override
  public Clinic update(ClinicEditDto data) {

    Clinic clinic = clinicRepository.findById(data.getId())
        .orElseThrow(
            () -> new NotFoundException(
                Collections.singletonList("err.clinic.clinic-does-not-exist")));
    //valid name and phone
    validationNameOrPhoneWhenEditClinic(data);
    //valid doctor
    validDoctor(data.getDoctorId());

    BeanUtils.copyProperties(data, clinic);
    clinic.setName(data.getName().trim());
    clinicRepository.save(clinic);

    //delete mapping clinic disease
    clinicDiseaseRepository.deleteAllByClinicId(clinic.getId());
    //valid disease
    validDisease(clinic, data.getDiseaseIds());
    return clinic;
  }

  @Override
  public ClinicDto findById(Long id) {

    Clinic clinic = clinicRepository.findById(id).orElseThrow(
        () -> new NotFoundException(Collections.singletonList("err.clinic.clinic-does-not-exist")));

    //get doctor
    Doctor doctor = doctorRepository.findDoctorById(clinic.getDoctorId());
    DoctorDto doctorDto;
    if (doctor == null) {
      doctorDto = null;
    } else {
      doctorDto = DoctorDto.builder().id(doctor.getId()).name(doctor.getName()).build();
    }
    //get disease
    List<DiseaseDto> diseases = new ArrayList<>();
    List<Long> diseasesIds = clinicDiseaseRepository.findAllDiseaseById(clinic.getId());
    diseasesIds.forEach(diseasesId -> {
      Disease disease = diseaseRepository.findDiseaseById(diseasesId);
      if (disease != null) {
        diseases.add(DiseaseDto.builder().id(diseasesId).name(disease.getName()).build());
      }

    });
    return ClinicDto.builder()
        .id(clinic.getId())
        .name(clinic.getName())
        .phone(clinic.getPhone())
        .address(clinic.getAddress())
        .description(clinic.getDescription())
        .doctor(doctorDto)
        .diseases(diseases)
        .isActive(clinic.getIsActive())
        .build();
  }

  @Override
  public Page<Clinic> findAllClinics(String name, String phone, Boolean isActive,
      Pageable pageable) {
    return (Page<Clinic>) clinicRepository.findAll(
        ClinicSpec.findAllClinic(name, phone, isActive), pageable);
  }

  @Override
  public void delete(Long id) {
    Clinic clinic = clinicRepository.findById(id).orElseThrow(
        () -> new NotFoundException(Collections.singletonList("err.clinic.clinic-does-not-exist")));

    List<Long> clinicIds = clinicDiseaseRepository.findAllClinicById(clinic.getId());
    List<Long> clinicIdForUser = clinicUserRepository.findAllClinicById(clinic.getId());
    if (!CollectionUtils.isEmpty(clinicIds) || !CollectionUtils.isEmpty(clinicIdForUser)) {
      clinic.setIsActive(false);
      clinicRepository.save(clinic);
    } else {
      clinicRepository.delete(clinic);
    }
  }

  private void validDoctor(Long doctorId) {
    doctorRepository.findById(doctorId).orElseThrow(() ->
        new NotFoundException(
            Collections.singletonList("err.doctor.doctor-does-not-exist")));
  }

  private void validDisease(Clinic clinic, List<Long> diseases) {
    Set<Long> setDisease = new HashSet<>(diseases);
    setDisease.forEach(disease -> {
      diseaseRepository.findById(disease).orElseThrow(() -> new NotFoundException(
          Collections.singletonList("err.diseases.disease-not-found")));
    });
    //mapping clinic disease
    List<ClinicDisease> clinicDiseases = new ArrayList<>();
    setDisease.forEach(disease -> {
          clinicDiseases
              .add(ClinicDisease.builder().clinicId(clinic.getId()).diseaseId(disease).build());
        }
    );
    clinicDiseaseRepository.saveAll(clinicDiseases);
  }

  private void validationNameOrPhoneWhenAddClinic(ClinicAddDto data) {
    List<String> failReasons = new ArrayList<>();
    clinicRepository.findByName(data.getName().trim()).ifPresent(doctor -> {
      failReasons.add("err.clinic.name-is-duplicate");
    });

    clinicRepository.findByPhone(data.getPhone()).ifPresent(doctor -> {
      failReasons.add("err.clinic.phone-is-duplicate");
    });
    if (!CollectionUtils.isEmpty(failReasons)) {
      throw new ConflictException(failReasons);
    }
  }

  private void validationNameOrPhoneWhenEditClinic(ClinicEditDto data) {
    List<String> failReasons = new ArrayList<>();
    clinicRepository.findByNameAndIdNot(data.getName().trim(), data.getId()).ifPresent(doctor -> {
      failReasons.add("err.clinic.name-is-duplicate");
    });

    clinicRepository.findByPhoneAndIdNot(data.getPhone(), data.getId()).ifPresent(doctor -> {
      failReasons.add("err.clinic.phone-is-duplicate");
    });
    if (!CollectionUtils.isEmpty(failReasons)) {
      throw new ConflictException(failReasons);
    }
  }
}
