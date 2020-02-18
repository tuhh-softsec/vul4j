package vn.mavn.patientservice.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.MessageResponse;
import vn.mavn.patientservice.response.ResponseMultil;
import vn.mavn.patientservice.service.ResponseService;

/**
 * The type Response service.
 */
@Service
@Transactional
public class ResponseServiceImpl implements ResponseService {

  @Autowired
  private MessageSource messageSource;

  public HttpResponse buildUpdatedResponse(Long id, List<String> codes) {
    List<MessageResponse> messageResponses = buildMessageResponse(codes);
    return HttpResponse.builder().id(id).messages(messageResponses).build();
  }

  @Override
  public ResponseMultil buildResponseMutil(List<Long> ids, List<String> codes) {
    List<MessageResponse> messageResponses = buildMessageResponse(codes);
    return ResponseMultil.builder().ids(ids).messageResponses(messageResponses).build();
  }

  private List<MessageResponse> buildMessageResponse(List<String> codes) {
    List<MessageResponse> messageResponses = new ArrayList<>();
    for (String code : codes) {
      MessageResponse messageResponse = new MessageResponse();
      messageResponse.setCode(code);
      messageResponse
          .setMessage(messageSource.getMessage(code, null, LocaleContextHolder.getLocale()));
      messageResponses.add(messageResponse);
    }
    return messageResponses;
  }
}
