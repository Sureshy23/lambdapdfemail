package com.tradefinance.lambda_pdf_email;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
			PDRectangle pdRectangle = PDRectangle.A4;
//			PDPage page = new PDPage(PDRectangle.A4);
//			document.addPage(page);
			
//			PDRectangle pdRectangle = page.getMediaBox();
	        float margin = 50;
	        float col1Width = 150;
	        float col2Width = 350;
	        float usableWidth = col1Width + col2Width;
	        float leading = 14f;
	        float rowSpacing = 6f;

			InputStream imageStream = getClass().getResourceAsStream("/logo.png");
			if (imageStream == null) {
			    throw new RuntimeException("Image not found in classpath!");
			}
			PDImageXObject imageXObject = PDImageXObject.createFromByteArray(document, imageStream.readAllBytes(), "logo");
			
			String col1Text = String.join("\n", request.getMsgColumn1());
	        String col2Text = String.join("\n", request.getMsgColumn2());
	        
	        PDPage page = new PDPage(pdRectangle);
	        document.addPage(page);
	        PDPageContentStream cs = new PDPageContentStream(document, page);
	        float yPosition = drawHeader(document, cs, pdRectangle, imageXObject);

	        
	        // Draw header row
	        drawTableRow(cs, margin, yPosition, new float[] {col1Width, col2Width}, leading + rowSpacing, "Message 1", "Message 2");
	        yPosition -= (leading + rowSpacing);

	        // Calculate number of lines and row height
	        List<String> col1Lines = List.of(col1Text.split("\n"));
	        List<String> col2Lines = List.of(col2Text.split("\n"));
	        int maxLines = Math.max(col1Lines.size(), col2Lines.size());
	        float rowHeight = maxLines * leading + rowSpacing;

	        // Now draw multiline content, with page break if needed
	        float textStartX1 = margin + 5;
	        float textStartX2 = margin + col1Width + 5;
	        float textStartY = yPosition - 12; // small offset for baseline

	        int lineIndex = 0;
	        
	        while (lineIndex < maxLines) {
	            // Check if we have enough space for at least one line
	            if (yPosition - rowHeight < margin) {
	                cs.close(); // close current content stream

	                // Add new page
	                page = new PDPage(pdRectangle);
	                document.addPage(page);
	                cs = new PDPageContentStream(document, page);

	                // Draw header & reset yPosition
	                yPosition = drawHeader(document, cs, pdRectangle, imageXObject);

	                // Redraw header row on new page
	                drawTableRow(cs, margin, yPosition, new float[] {col1Width, col2Width}, leading + rowSpacing, "Message 1", "Message 2");
	                yPosition -= (leading + rowSpacing);

	                textStartY = yPosition - 12;
	                rowHeight = leading + rowSpacing; // on subsequent pages, print line-by-line
	            }

	            // Draw one line per column (or empty if none)
	            String line1 = lineIndex < col1Lines.size() ? col1Lines.get(lineIndex) : "";
	            String line2 = lineIndex < col2Lines.size() ? col2Lines.get(lineIndex) : "";

	            // Draw row border (single line height)
	            drawTableRow(cs, margin, yPosition, new float[] {col1Width, col2Width}, leading + rowSpacing, null, null);

	            // Draw text
	            cs.beginText();
	            cs.newLineAtOffset(textStartX1, textStartY);
	            cs.showText(line1);
	            cs.endText();

	            cs.beginText();
	            cs.newLineAtOffset(textStartX2, textStartY);
	            cs.showText(line2);
	            cs.endText();

	            yPosition -= (leading + rowSpacing);
	            textStartY -= (leading + rowSpacing);
	            lineIndex++;
	        }

	        cs.close();

	        // Protect PDF with password
//	        AccessPermission accessPermission = new AccessPermission();
//	        accessPermission.setCanPrint(true);
//
//	        StandardProtectionPolicy policy = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
//	        policy.setEncryptionKeyLength(128);
//	        document.protect(policy);

	        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
	            document.save(arrayOutputStream);
	            return arrayOutputStream.toByteArray();
	        }
	    }
		
//			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)){
//				
//                contentStream.drawImage(imageXObject, x, y, imageWidth, imageHeight);
//
//				contentStream.beginText();
//				contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
//				contentStream.newLineAtOffset(50, 700);
//				contentStream.showText("ttt");
//				contentStream.endText();
//				
////				float currentY = pdRectangle.getHeight() - 100;
//                float[] colWidths = {150, 350};
////                float rowHeight = 20;
//
////                    drawTableRow(contentStream, margin, currentY, colWidths, rowHeight);
////                    currentY -= rowHeight;
//                    
//                    List<String> col1 = request.getMsgColumn1();
//                    List<String> col2 = request.getMsgColumn2();
//                    
//                    String col1Text = String.join("\n", col1);
//                    String col2Text = String.join("\n", col2);
//
//                    float currentY = pdRectangle.getHeight() - 100;
//                    float rowHeight; 
//                    float col1Width = 150;
//                    float col2Width = 350;
//                    float maxWidth1 = col1Width - 10; // padding inside cell
//                    float maxWidth2 = col2Width - 10;
//                    float yStart = pdRectangle.getHeight() - 150;
//                    float leading = 14f; // line spacing
//                    
//                 // Draw header row
//                    drawTableRow(contentStream, margin, currentY, new float[] {col1Width, col2Width}, leading + 6, "Message 1", "Message 2");
//                    currentY -= (leading + 6);
//
//                    // Draw content row border box height depends on number of lines
//                    // First calculate height for col1 and col2 multiline text
//                    int col1Lines = col1.size();
//                    int col2Lines = col2.size();
//                    int maxLines = Math.max(col1Lines, col2Lines);
//                    rowHeight = leading * maxLines + 6;
//
//                    drawTableRow(contentStream, margin, currentY, new float[] {col1Width, col2Width}, rowHeight, null, null);
//
//                    // Draw multiline texts inside cells
//                    float textStartX1 = margin + 5;
//                    float textStartX2 = margin + col1Width + 5;
//                    float textStartY = currentY - 14; // baseline offset
//
////                    drawMultilineText(contentStream, col1Text, textStartX1, textStartY, maxWidth1, leading);
////                    drawMultilineText(contentStream, col2Text, textStartX2, textStartY, maxWidth2, leading);
//                    drawMultilineTextWithPagination(document, contentStream, col1Text, textStartX1, yStart, col1Width - 10, leading, margin, bottomMargin);
//                    drawMultilineTextWithPagination(document, contentStream, col2Text, textStartX2, yStart, col2Width - 10, leading, margin, bottomMargin);
//
////                    int maxRows = Math.max(col1.size(), col2.size());
////                    
////                    drawTableRow(contentStream, margin, currentY, colWidths, rowHeight, "Message 1", "Message 2");
////                    currentY -= rowHeight;
////                    
////                    for (int i = 0; i < maxRows; i++) {
////                        String val1 = i < col1.size() ? col1.get(i) : "";
////                        String val2 = i < col2.size() ? col2.get(i) : "";
////                        drawTableRow(contentStream, margin, currentY, colWidths, rowHeight, val1, val2);
////                        currentY -= rowHeight;
////                    }         
//                    
//			}
//			AccessPermission accessPermission = new AccessPermission();
//			accessPermission.setCanPrint(true);
////			
////			StandardProtectionPolicy policy = new StandardProtectionPolicy(ownerPassword, userPassword, accessPermission);
////			policy.setEncryptionKeyLength(128);
////			document.protect(policy);
//			
//			try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
//				document.save(arrayOutputStream);
//				return arrayOutputStream.toByteArray();
//			}
//		}
	}
	
	
	private float drawHeader(PDDocument document, PDPageContentStream cs, PDRectangle pdRectangle, PDImageXObject imageXObject) throws IOException {
	    float imageWidth = 100;
	    float imageHeight = 50;
	    float x = pdRectangle.getWidth() - imageWidth - 50;
	    float y = pdRectangle.getHeight() - imageHeight - 30;
	    
	    cs.drawImage(imageXObject, x, y, imageWidth, imageHeight);

	    cs.beginText();
	    cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
	    cs.newLineAtOffset(50, pdRectangle.getHeight() - 80);
//	    cs.showText("ttt");
	    cs.endText();

	    return pdRectangle.getHeight() - 100; // starting Y for content
	}
	
	private void drawMultilineTextWithPagination(PDDocument document,
            PDPageContentStream cs,
            String text,
            float x,
            float yStart,
            float maxWidth,
            float leading,
            float margin,
            float bottomMargin) throws IOException {
		
			String[] lines = text.split("\n");
			PDRectangle pageSize = PDRectangle.A4;
			float yPosition = yStart;
			
			cs.setLeading(leading);
			cs.beginText();
			cs.newLineAtOffset(x, yPosition);
			
			for (String line : lines) {
			// Check if there is space for next line, else create new page
			if (yPosition <= bottomMargin) {
			cs.endText();
			cs.close();
			
			// Add new page and open new content stream
			PDPage newPage = new PDPage(pageSize);
			document.addPage(newPage);
			cs = new PDPageContentStream(document, newPage);
			
			// Reset yPosition and begin text again
			yPosition = pageSize.getHeight() - margin;
			cs.setLeading(leading);
			cs.beginText();
			cs.newLineAtOffset(x, yPosition);
			}
			
			cs.showText(line);
			cs.newLine();
			yPosition -= leading;
			}
			cs.endText();
}


	private float drawMultilineText(PDPageContentStream cs, String text, float x, float y, float maxWidth, float leading) throws IOException {
	    String[] lines = text.split("\n");
	    cs.setLeading(leading);
	    cs.beginText();
	    cs.newLineAtOffset(x, y);
	    for (String line : lines) {
	        cs.showText(line);
	        cs.newLine();
	    }
	    cs.endText();
	    return lines.length * leading; // total height used
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

	    if (col1 != null) {
	        cs.beginText();
	        cs.newLineAtOffset(x + 5, y - 15);
	        cs.showText(col1);
	        cs.endText();
	    }

	    if (col2 != null) {
	        cs.beginText();
	        cs.newLineAtOffset(xPositions[1] + 5, y - 15);
	        cs.showText(col2);
	        cs.endText();
	    }
	}

	
//	private void drawTableRow(PDPageContentStream cs, float x, float y, float[] colWidths, float rowHeight,
//			String col1, String col2) throws IOException {
//		float cellHeight = rowHeight;
//		float[] xPositions = {x, x + colWidths[0], x + colWidths[0] + colWidths[1]};
//		
//		cs.setLineWidth(0.5f);
//		
//		// Borders
//		cs.moveTo(x, y);
//		cs.lineTo(xPositions[2], y);
//		cs.lineTo(xPositions[2], y - cellHeight);
//		cs.lineTo(x, y - cellHeight);
//		cs.lineTo(x, y);
//		cs.stroke();
//		
//		// Vertical line between columns
//		cs.moveTo(xPositions[1], y);
//		cs.lineTo(xPositions[1], y - cellHeight);
//		cs.stroke();
//		
//		// Text
//		cs.beginText();
//		cs.newLineAtOffset(x + 5, y - 15);
//		cs.showText(col1 != null ? col1 : "");
//		cs.endText();
//		
//		cs.beginText();
//		cs.newLineAtOffset(xPositions[1] + 5, y - 15);
//		cs.showText(col2 != null ? col2 : "");
//		cs.endText();	
//	}
}
