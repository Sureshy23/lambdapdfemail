package com.tradefinance.lambda_pdf_email;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


public class PdfService {

	public byte[] createPasswordProtectedPdf(PdfEmailRequest request, String userPassword, String ownerPassword) throws IOException {
		try (PDDocument document = new PDDocument()){
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);
			
			PDRectangle pdRectangle = page.getMediaBox();
			float margin = 50;
			
			File imageFile = new File ("src/main/resources/logo.png");
			PDImageXObject imageXObject = PDImageXObject.createFromFileByContent(imageFile, document);
			
			float imageWidth = 100;
			float imageHeight = 50;
			
			float x = pdRectangle.getWidth() - imageWidth - 50;
			float y = pdRectangle.getHeight() - imageHeight - 30;
			
			
			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)){
				
                contentStream.drawImage(imageXObject, x, y, imageWidth, imageHeight);

				contentStream.beginText();
				contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
				contentStream.newLineAtOffset(50, 700);
				contentStream.showText("ttt");
				contentStream.endText();
				
				float currentY = pdRectangle.getHeight() - 100;
                float[] colWidths = {150, 350};
                float rowHeight = 20;

//                    drawTableRow(contentStream, margin, currentY, colWidths, rowHeight);
//                    currentY -= rowHeight;
                    
                    List<String> col1 = request.getMsgColumn1();
                    List<String> col2 = request.getMsgColumn2();
                    int maxRows = Math.max(col1.size(), col2.size());
                    
                    drawTableRow(contentStream, margin, currentY, colWidths, rowHeight, "Message 1", "Message 2");
                    currentY -= rowHeight;
                    
                    for (int i = 0; i < maxRows; i++) {
                        String val1 = i < col1.size() ? col1.get(i) : "";
                        String val2 = i < col2.size() ? col2.get(i) : "";
                        drawTableRow(contentStream, margin, currentY, colWidths, rowHeight, val1, val2);
                        currentY -= rowHeight;
                    }
			}
			AccessPermission accessPermission = new AccessPermission();
			accessPermission.setCanPrint(true);
			
			StandardProtectionPolicy policy = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
			policy.setEncryptionKeyLength(128);
			document.protect(policy);
			
			try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
				document.save(arrayOutputStream);
				return arrayOutputStream.toByteArray();
			}
		}
	}

	private void drawTableRow(PDPageContentStream cs, float x, float y, float[] colWidths, float rowHeight,
            String col1, String col2) throws IOException {
float cellHeight = rowHeight;
float[] xPositions = {x, x + colWidths[0], x + colWidths[0] + colWidths[1]};

cs.setLineWidth(0.5f);

// Borders
cs.moveTo(x, y);
cs.lineTo(xPositions[2], y);
cs.lineTo(xPositions[2], y - cellHeight);
cs.lineTo(x, y - cellHeight);
cs.lineTo(x, y);
cs.stroke();

// Vertical line between columns
cs.moveTo(xPositions[1], y);
cs.lineTo(xPositions[1], y - cellHeight);
cs.stroke();

// Text
cs.beginText();
cs.newLineAtOffset(x + 5, y - 15);
cs.showText(col1 != null ? col1 : "");
cs.endText();

cs.beginText();
cs.newLineAtOffset(xPositions[1] + 5, y - 15);
cs.showText(col2 != null ? col2 : "");
cs.endText();	
	}
}
