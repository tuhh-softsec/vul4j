package vn.mavn.patientservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by TaiND on 2020-02-17.
 **/
@RestController
public class TestController {

  @GetMapping
  public String hello() {
    return "Hello World!";
  }
}
