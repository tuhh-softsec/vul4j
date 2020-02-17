package vn.mavn.patientservice.config;

import java.util.Arrays;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created by TaiND on 2019-11-10.
 **/
@Aspect
@Component
@Log4j2
public class LogAopConfig {

  private String patternLog(JoinPoint joinPoint) {
    return String.format("%s%s%s%s%s%s%s",
        joinPoint.getSignature().getName(),
        "() with params: ",
        Arrays.toString(joinPoint.getArgs()),
        " in class: ",
        joinPoint.getTarget().getClass(),
        " base class: ",
        joinPoint.getSignature().getDeclaringType());
  }

  @Pointcut(value = "within(*vn.mavn.*.controller..*)")
  public void controllerPointcut() {
  }

  @Before(value = "controllerPointcut()")
  public void logBefore(JoinPoint joinPoint) {
    if (log.isInfoEnabled()) {
      log.info(String.format("%s%s", "Enter method: ", patternLog(joinPoint)));
    }
  }

  @After(value = "controllerPointcut()")
  public void logAfter(JoinPoint joinPoint) {
    if (log.isInfoEnabled()) {
      log.info(String.format("%s%s", "Finished method: ", patternLog(joinPoint)));
    }
  }

}
