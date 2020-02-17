package vn.mavn.patientservice.gracefulshd;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * Created by TaiND on 2019-11-10.
 **/
@Log4j2
public class GracefulShutdown implements TomcatConnectorCustomizer,
    ApplicationListener<ContextClosedEvent> {

  private volatile Connector connector;

  private Long waitingTime;

  public GracefulShutdown(Long waitingTime) {
    this.waitingTime = waitingTime;
  }

  @Override
  public void customize(Connector connector) {
    this.connector = connector;
  }

  @Override
  public void onApplicationEvent(ContextClosedEvent event) {
    this.connector.pause();
    log.info("Graceful shutdown is working now...");
    Executor executor = this.connector.getProtocolHandler().getExecutor();
    if (executor instanceof ThreadPoolExecutor) {
      try {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
        threadPoolExecutor.shutdown();
        if (!threadPoolExecutor.awaitTermination(waitingTime, TimeUnit.SECONDS)) {
          log.warn("Application was forced to shutdown!");
          threadPoolExecutor.shutdownNow();
        }
      } catch (InterruptedException ex) {
        log.error("Graceful shutdown has exception: " + Arrays.toString(ex.getStackTrace()));
        Thread.currentThread().interrupt();
      }
    }
  }
}