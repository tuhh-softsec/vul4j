package vn.mavn.patientservice.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Message response.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("message")
@Builder
public class MessageResponse {

  private String code;
  private String message;
}

