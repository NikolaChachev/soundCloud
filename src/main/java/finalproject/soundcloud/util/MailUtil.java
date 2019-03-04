package finalproject.soundcloud.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class MailUtil {

    public static void sendMail(String messageText,String email) throws Exception{
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("finalprojectsoundcloud@gmail.com", "SoundCloud33!");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("no-reply@gmail.com"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Confirm your registration");
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(messageText, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
