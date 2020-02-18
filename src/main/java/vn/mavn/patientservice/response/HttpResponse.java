package vn.mavn.patientservice.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Http response.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpResponse {

  private Long id;
  private List<MessageResponse> messages;
}

