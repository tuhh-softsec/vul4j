package vn.mavn.patientservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

/**
 * Created by TaiND on 2019-12-11.
 */
@Log4j2
public class Oauth2TokenUtils {

  /**
   * Gets value by key in the token.
   *
   * @param <T> the type parameter
   * @param token the token
   * @param fieldName the field name
   * @param klazz the klazz
   * @return the value by key in the token
   */
  public static <T> T getValueByKeyInTheToken(String token, String fieldName,
      Class<T> klazz) {

    JSONObject tokenObj = TokenUtils.decodeToken(token);
    if (tokenObj == null) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    return mapper.convertValue(tokenObj.get(fieldName), klazz);
  }
}
