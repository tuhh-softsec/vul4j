package vn.mavn.patientservice.config;

import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by TaiND on 2019-10-24.
 **/
@Configuration
public class LiquibaseConfig {
  /**
   * The Application context.
   */
  @Autowired
  ApplicationContext applicationContext;

  /**
   * Liquibase spring liquibase.
   *
   * @return the spring liquibase
   */
  @Bean
  public SpringLiquibase liquibase() {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setChangeLog("classpath:liquibase/db.changelog-master.xml");
    liquibase.setDataSource((DataSource) applicationContext.getBean("dataSource"));
    return liquibase;
  }
}
