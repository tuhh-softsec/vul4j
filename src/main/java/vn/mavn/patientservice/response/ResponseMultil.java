package vn.mavn.patientservice.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Response multil.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMultil {

  /**
   * The Ids.
   */
  List<Long> ids;
  /**
   * The Message responses.
   */
  List<MessageResponse> messageResponses;

}
