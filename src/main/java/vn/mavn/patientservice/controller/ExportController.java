package vn.mavn.patientservice.controller;

import io.swagger.annotations.Api;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mavn.patientservice.dto.qobject.QueryMedicalRecordDto;
import vn.mavn.patientservice.service.ReportService;
import vn.mavn.patientservice.util.EntityValidationUtils;

/**
 * Created by TaiND on 2020-02-25.
 **/
@RestController
@RequestMapping("/api/v1/export-report")
@Api(tags = "Export Report Controller")
public class ExportController {

  @Autowired
  private ReportService reportService;

  @PostMapping("/medical-records")
  public void exportMedicalRecords(HttpServletResponse httpServletResponse,
      @Valid @ModelAttribute QueryMedicalRecordDto queryMedicalRecordDto,
      BindingResult bindingResult) throws IOException {
    EntityValidationUtils.processBindingResults(bindingResult);
    // Load file as Resource
    reportService.exportReport(queryMedicalRecordDto, httpServletResponse);
  }

}
