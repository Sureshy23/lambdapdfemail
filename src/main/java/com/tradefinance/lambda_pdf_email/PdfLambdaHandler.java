package com.tradefinance.lambda_pdf_email;

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

            String messageId = emailService.sendEmailWithAttachment(
                    "no-reply-dev@alsalambank.com",
                    request.getEmail(),
                    "Your Password Protected PDF",
                    "Please find attached.",
                    pdfBytes,
                    request.getTfNo()
            );

            return "Email sent successfully!"+messageId;
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
