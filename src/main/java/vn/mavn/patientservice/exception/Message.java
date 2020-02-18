package vn.mavn.patientservice.exception;

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
public class Message {

  private String code;
  private String message;
}
