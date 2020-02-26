package vn.mavn.patientservice.service;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import vn.mavn.patientservice.dto.qobject.QueryMedicalRecordDto;

/**
 * Created by TaiND on 2020-02-26.
 **/
public interface ReportService {

  void exportReport(QueryMedicalRecordDto queryMedicalRecordDto,
      HttpServletResponse httpServletResponse) throws IOException;
}
