package vn.mavn.patientservice.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Created by TaiND on 2020-02-16.
 **/
@RestControllerAdvice
public class BizExceptionHandler {

  @Autowired
  private MessageSource messageSource;

  /**
   * Handler exception when data not found.
   *
   * @param ex the ex
   * @param request the request
   * @return the response entity
   */
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public RequestResponse handlerNotFoundException(NotFoundException ex,
      HttpServletRequest request) {

    return buildRequestResponse(ex.getErrCodes(), request.getRequestURL().toString());
  }

  /**
   * Handler exception when have any validation error.
   *
   * @param ex the ex
   * @param request the request
   * @return the response entity
   */
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public RequestResponse handlerBadRequestException(BadRequestException ex,
      HttpServletRequest request) {

    return buildRequestResponse(ex.getErrCodes(), request.getRequestURL().toString());
  }

  /**
   * Handler exception when having any data conflict.
   *
   * @param ex the ex
   * @param request the request
   * @return the response entity
   */
  @ExceptionHandler(ConflictException.class)
  @ResponseStatus(value = HttpStatus.CONFLICT)
  public RequestResponse handlerConflictException(ConflictException ex,
      HttpServletRequest request) {

    return buildRequestResponse(ex.getErrCodes(), request.getRequestURL().toString());
  }

  private RequestResponse buildRequestResponse(List<String> errCodes, String requestUrl) {
    List<Message> errorMessages = new ArrayList<>();
    for (String errCode : errCodes) {
      String errMessage = messageSource.getMessage(errCode, null, LocaleContextHolder.getLocale());
      errorMessages.add(new Message(errCode, errMessage));
    }

    RequestResponse response = new RequestResponse();
    response.setErrors(errorMessages);
    response.setUrl(requestUrl);
    response.setTimestamp(LocalDateTime.now());

    return response;
  }

}
