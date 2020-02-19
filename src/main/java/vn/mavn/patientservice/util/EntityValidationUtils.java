package vn.mavn.patientservice.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.BindingResult;
import vn.mavn.patientservice.exception.BadRequestException;

/**
 * Created by TaiND on 2019-08-28.
 */
public class EntityValidationUtils {

  /**
   * Process binding results.
   *
   * @param bindingResult the binding result
   */
  public static void processBindingResults(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      List<String> errors = new ArrayList<>();
      bindingResult.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
      throw new BadRequestException(errors);
    }
  }
}
