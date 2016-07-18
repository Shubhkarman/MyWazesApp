package pack;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Email {
   public void emailsend(String to, String key) {
      // Recipient's email ID needs to be mentioned.
      //String to = "shubkarmanjosan123@gmail.com";//change accordingly

      // Sender's email ID needs to be mentioned
      String from = "mywavesapp@gmail.com";//change accordingly
      final String username = "MyWavesApp";//change accordingly
      final String password = "MyWavesApp123";//change accordingly
      
      // Assuming you are sending email through relay.jangosmtp.net
      String host = "smtp.gmail.com";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", "587");

      // Get the Session object.
      Session session = Session.getInstance(props,
      new javax.mail.Authenticator() {
         protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
         }
      });

      try {
         // Create a default MimeMessage object.
         Message message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.setRecipients(Message.RecipientType.TO,
         InternetAddress.parse(to));

         // Set Subject: header field
         message.setSubject("Email Verification");

         // Now set the actual message
         message.setText("Thank you for signing in to MyWaves. To Complete the verification process please go to this link: http://localhost:8080/sample_web_project/activate.jsp"
         		+ " and enter your email id and unique key : "+key );

         // Send message
         Transport.send(message);

         System.out.println("Sent message successfully....");

      } catch (MessagingException e) {
            throw new RuntimeException(e);
      }
   }
}