package com.tradefinance.lambda_pdf_email;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.stream.Collectors;

public class EmailService {
	
	public String sendEmailWithAttachment(String from, String to, String subject, String bodyText, byte[] attachmentData, String attachmentName) throws Exception {
		Session session = Session.getDefaultInstance(new Properties());
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		
        String htmlContent = loadHtmlTemplate("/emailBody.html");
        
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setContent(htmlContent,"text/html; charset=UTF-8");
//		bodyPart.setText(bodyText);
		
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
	                .withRegion(Regions.ME_SOUTH_1) // Update to your region
	                .build();

	        SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
	        
	        SendRawEmailResult emailResult = client.sendRawEmail(rawEmailRequest);
	        String messageId = emailResult.getMessageId();
	        System.out.println(messageId);
	        
	        
	        return messageId;
	}

	
	  private String loadHtmlTemplate(String path) throws IOException {
	        try (InputStream in = getClass().getResourceAsStream(path)) {
	            if (in == null) throw new FileNotFoundException("HTML template not found: " + path);
	            return new BufferedReader(new InputStreamReader(in))
	                    .lines()
	                    .collect(Collectors.joining("\n"));
	        }
	    }
}
