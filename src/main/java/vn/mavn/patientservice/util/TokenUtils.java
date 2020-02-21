package vn.mavn.patientservice.util;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import vn.mavn.patientservice.exception.NotFoundException;

/**
 * Created by TaiND on 2019-12-12.
 */
@Log4j2
public class TokenUtils {

  private static final String ACCESS_TOKEN = "additional_token";
  private static final String USER_ID_FIELD = "user_id";
  private static final String USER_CODE_FIELD = "code";

  /**
   * Decode token json object.
   *
   * @param token the token
   * @return the json object
   */
  public static JSONObject decodeToken(String token) {
    try {
      String[] splitToken = token.split("\\.");
      Base64 base64Url = new Base64(true);
      byte[] decodedBytes = base64Url.decode(splitToken[1]);

      return new JSONObject(new String(decodedBytes, "UTF-8"));
    } catch (UnsupportedEncodingException ex) {
      log.error("Cannot decode token: " + ex.getMessage());
      return null;
    }
  }

  public static String getUserIdFromToken(HttpServletRequest httpServletRequest) {
    String token = httpServletRequest.getHeader("authorization").replace("Bearer ","");
    if (StringUtils.isBlank(token)) {
      throw new NotFoundException(
          Collections.singletonList("err.users.missing-additional-token-in-request"));
    }
    return Oauth2TokenUtils.getValueByKeyInTheToken(token, USER_ID_FIELD, String.class);
  }

  public static String getUserCodeFromToken(HttpServletRequest httpServletRequest) {
    String token = httpServletRequest.getHeader("authorization").replace("Bearer ","");
    if (StringUtils.isBlank(token)) {
      throw new NotFoundException(
          Collections.singletonList("err.users.missing-additional-token-in-request"));
    }
    return Oauth2TokenUtils.getValueByKeyInTheToken(token, USER_CODE_FIELD, String.class);
  }
}
