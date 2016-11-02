package fr.martiben.mail.greenmail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import fr.martiben.mail.greenmail.util.TestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class GreenmailTest
{
  /** Spring Email Sender. */
  @Autowired
  private JavaMailSenderImpl javaMailSender;

  /** Green mail testing SMTP. */
  private GreenMail greenmailSmtp;

  /** Before test method. */
  @Before
  public void before()
  {
    greenmailSmtp = new GreenMail(ServerSetupTest.SMTP);
    greenmailSmtp.start();
    javaMailSender.setPort(3025);
    javaMailSender.setHost("localhost");
  }

  /** After test method. */
  @After
  public void after()
  {
    greenmailSmtp.stop();
  }

  @Test
  public void testGreenmail_OK() throws Exception
  {
    final String expectedFrom    = "test@sender.com";
    final String expectedTo      = "test@receiver.com";
    final String expectedSubject = "test subject";
    final String expectedBody    = "test message";

    javaMailSender.send(mimeMessage->{
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      message.setFrom(expectedFrom);
      message.setTo(expectedTo);
      message.setSubject(expectedSubject);
      message.setText(expectedBody);
    });

    boolean result = greenmailSmtp.waitForIncomingEmail(3000, 1);
    assertThat(result).isTrue();

    Message[] recievedMails = greenmailSmtp.getReceivedMessages();
    assertThat(recievedMails).hasSize(1);

    Message one = recievedMails[0];
    assertThat(one.getFrom()).isEqualTo(new InternetAddress[] { new InternetAddress(expectedFrom) });
    assertThat(one.getAllRecipients()).isEqualTo(new InternetAddress[] { new InternetAddress(expectedTo) });
    assertThat(one.getSubject()).isEqualTo(expectedSubject);

    MimeMultipart parts = (MimeMultipart) one.getContent();
    assertThat(parts.getCount()).isEqualTo(1);
    assertThat(GreenMailUtil.getBody(parts.getBodyPart(0))).contains(expectedBody);
  }
}

