package fr.martiben.mail.greenmail.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * @author martinelli-b
 */
@Configuration
public class TestConfig
{
  // Thoses properties can be set in a props file w/ @PropertySource(value = "classpath:application.properties")
  /** SMTP Host. */
  private String smtpHost = "smtp.domain.net";

  /** SMTP Port. */
  private int smtpPort = 465;

  /** SMTP Username. */
  private String smtpUsername = "login";

  /** SMTP Password. */
  private String smtpPassword = "password";

  @Bean
  public JavaMailSenderImpl javaMailSenderImpl()
  {
    Properties properties = new Properties();
    properties.setProperty("mail.transport.protocol", "smtp");
    properties.setProperty("mail.smtp.auth", "true");
    properties.setProperty("mail.debug", "false");

    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost(smtpHost);
    javaMailSender.setPort(smtpPort);
    javaMailSender.setUsername(smtpUsername);
    javaMailSender.setPassword(smtpPassword);
    javaMailSender.setJavaMailProperties(properties);

    return javaMailSender;
  }
}
