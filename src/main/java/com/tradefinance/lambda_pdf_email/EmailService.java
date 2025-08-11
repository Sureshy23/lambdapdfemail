package com.tradefinance.lambda_pdf_email;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.regions.Regions;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;

public class EmailService {
	
	public void sendEmailWithAttachment(String from, String to, String subject, String bodyText, byte[] attachmentData, String attachmentName) throws Exception {
		Session session = Session.getDefaultInstance(new Properties());
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setText(bodyText);
		
		MimeBodyPart attachmentPart = new MimeBodyPart();
		DataSource dataSource = new ByteArrayDataSource(attachmentData, "application/pdf");
		attachmentPart.setDataHandler(new DataHandler(dataSource));
		attachmentPart.setFileName(attachmentName);
		
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(bodyPart);
		multipart.addBodyPart(attachmentPart);
		
		message.setContent(multipart);
		
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		message.writeTo(arrayOutputStream);
		RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(arrayOutputStream.toByteArray()));
		
		
		   AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
	                .withRegion(Regions.US_EAST_1) // Update to your region
	                .build();

	        SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
	        client.sendRawEmail(rawEmailRequest);
		
	}

}
