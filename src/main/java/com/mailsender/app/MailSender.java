package com.mailsender.app;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public class MailSender {

	private String smtpUsername = "SMTPUserName";
	private String smtpPassword = "SMTPPassword";
	private String smtpHost = "email-smtp.eu-west-1.amazonaws.com";
	private String smtpPort = "587";
	private String sendEmailFrom = "FromEmailAddress";

	@Autowired
	private Configuration freemarkerConfig;

	public void sendEmail() throws Exception {
		Session session = getTransportSession();
		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(sendEmailFrom));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress("TOEmailAddress"));
		message.setSubject("Test Subject");
		message.setContent("Hello Vipin Kumar. this is the sample email for testing only", "text/html; charset=UTF-8");

		// Create a transport.
		//Transport transport = session.getTransport();

		//for (int i = 1; i < 4; i++) {



		try {
			// Connect to Amazon SES using the SMTP username and password you specified above.

			//transport.connect(smtpHost, smtpUsername, smtpPassword);

			// Send the email.

			//transport.sendMessage(message, message.getAllRecipients());

			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("email-smtp.eu-west-1.amazonaws.com");
			mailSender.setPort(587);

			mailSender.setUsername("SMTP-UserName");
			mailSender.setPassword("SMTP-Password");

			Properties props = mailSender.getJavaMailProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.debug", "true");

			String attachment = "/Users/vipinkumar/Downloads/Data Mapping.xlsx";

			MimeBodyPart attach = new MimeBodyPart();
			//DataSource fds = new FileDataSource(attachment);
			DataSource source = new ByteArrayDataSource(getFileFromS3(attachment), "text/csv");
			attach.setDataHandler(new DataHandler(source));
			//attach.setDataHandler(new DataHandler(fds));
			//Extract file name to be send
			//attach.setFileName(fds.getName());
			attach.setFileName("Data Mapping.xlsx");
			MimeMultipart msg = new MimeMultipart();
			msg.addBodyPart(attach);
			message.setContent(msg);

			// Add the attachment to the message.
			//msg.addBodyPart(attach);

			System.out.println("Sending Email.....");
			mailSender.send(message);
			System.out.println("Email Sent");

		} catch (Exception ex) {


		} finally {
			//transport.close();

		} //end of finally block

		//} //for loop ends here


	}


	public void sendEmailClean() throws Exception {

		try {
			//Mail Sender
			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost("email-smtp.eu-west-1.amazonaws.com");
			mailSender.setPort(587);
			mailSender.setUsername("SMTP-UserName");
			mailSender.setPassword("SMTP-Password");

			//Mail Sender properties
			Properties props = mailSender.getJavaMailProperties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.debug", "true");

			//Local attachment
			String localAttachment = "/Users/vipinkumar/Downloads/Data Mapping.xlsx";
			String body = "This is an e-mail text.";
			String bodyMime = "text/plain";



			Session session = getTransportSession();

			//Mime Message [Main message]
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sendEmailFrom));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress("ToEmailAddress"));
			message.setSubject("Test Subject");
			//message.setContent("Hello Vipin Kumar. this is the sample email for testing only", "text/html; charset=UTF-8");


			//Body of email
			MimeMultipart msg = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			//messageBodyPart.setContent(body, bodyMime);
			messageBodyPart.setContent(processTemplate(), "text/html; charset=UTF-8");
			msg.addBodyPart(messageBodyPart);

			//Attachement
			MimeBodyPart attach = new MimeBodyPart();
			DataSource source = new ByteArrayDataSource(getFileFromS3("Data Mapping.xlsx"), "text/csv");
			attach.setDataHandler(new DataHandler(source));
			attach.setFileName("Data Mapping.xlsx");
			msg.addBodyPart(attach);


			//Adding body and attachment in message
			message.setContent(msg);


			//Sending email
			System.out.println("Sending Email.....");
			mailSender.send(message);
			System.out.println("Email Sent");

		} catch (Exception ex) {

			ex.printStackTrace();

		} 

	}

	public byte[] getFileFromS3(String file){

		AWSCredentials credentials = new BasicAWSCredentials("AccessKey", "SecretKey");

		byte[] byteArray = null;

		try {
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.EU_WEST_2).build();
			//AmazonS3 s3Client = new AmazonS3Client(credentials);  // S3 credentials

			//Bucket Listing
			/*List<Bucket> buckets = s3Client.listBuckets();
        	for(Bucket bucket : buckets) {
        	    System.out.println(bucket.getName());
        	}*/

			//Object Listing
			/*ObjectListing objectListing = s3Client.listObjects("quest-motor-reports-dev");
        	for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
        		System.out.println(os.getKey());
        	}*/
			S3Object object = s3Client.getObject("BucketName", "FileName");
			//System.out.println(object.getKey());
			byteArray = IOUtils.toByteArray(object.getObjectContent());
		}catch (Exception ex){
			ex.printStackTrace();
		}

		return byteArray;
	}

	private Session getTransportSession() {

		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtp.port", smtpPort);

		// Set properties indicating that we want to use STARTTLS to encrypt the connection.
		// The SMTP session will begin on an unencrypted connection, and then the client
		// will issue a STARTTLS command to upgrade to an encrypted connection.
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.starttls.required", true);

		return Session.getDefaultInstance(props);
	}

	private String processTemplate() {
		Map<String, Object> model = new HashMap<>();
		Date date = new Date();
    	String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateStr =  simpleDateFormat.format(date);
        model.put("reportDate", dateStr);

		String templateName = "test-report.ftl";
		try {
			Template template = freemarkerConfig.getTemplate(templateName);
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		} catch (IOException | TemplateException e) {
			throw new RuntimeException("Error while processing template: " + templateName,  e);
		}
	}

}
