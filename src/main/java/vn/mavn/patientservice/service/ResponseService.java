package vn.mavn.patientservice.service;

import java.util.List;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.ResponseMultil;

/**
 * The interface Response service.
 */
public interface ResponseService {

  /**
   * Build updated response http response.
   *
   * @param id the id
   * @param codes the codes
   * @return the http response
   */
  HttpResponse buildUpdatedResponse(Long id, List<String> codes);

  /**
   * Build response mutil response multil.
   *
   * @param ids the ids
   * @param codes the codes
   * @return the response multil
   */
  ResponseMultil buildResponseMutil(List<Long> ids, List<String> codes);

}
