package vn.mavn.patientservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by TaiND on 2020-02-16.
 **/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponse {

  private List<Message> errors;
  private String url;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
  private LocalDateTime timestamp;
}
