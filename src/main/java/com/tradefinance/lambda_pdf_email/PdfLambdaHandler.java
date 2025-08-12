package com.tradefinance.lambda_pdf_email;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class PdfLambdaHandler implements RequestHandler<PdfEmailRequest, String> {

    @Override
    public String handleRequest(PdfEmailRequest request, Context context) {
        try {
            PdfService pdfService = new PdfService();
            EmailService emailService = new EmailService();

            byte[] pdfBytes = pdfService.createPasswordProtectedPdf(
                    request,
                    "user123",
                    "owner123"
            );
            System.out.println(pdfBytes);

            System.out.println(request.getEmail());
            String messageId = emailService.sendEmailWithAttachment(
                    "no-reply-dev@alsalambank.com",
                    request.getEmail(),
                    "Your Password Protected PDF",
                    "Please find attached.",
                    pdfBytes,
                    request.getTfNo()
            );

            return "Email sent successfully!"+messageId.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
