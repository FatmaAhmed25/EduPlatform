package com.edu.eduplatform.utils.quiz.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfContentByte;

import java.io.IOException;

public class FooterPageEvent extends PdfPageEventHelper {
    private Image logo;

    public FooterPageEvent(String logoPath) throws IOException, DocumentException {
        this.logo = Image.getInstance(logoPath);
        this.logo.scaleToFit(150, 150); // Adjust the size of the logo as needed
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContent();
        float x = (document.left() + document.right()) / 2;
        float y = document.bottom() - 80; // Adjust the position of the logo as needed
        logo.setAbsolutePosition(x - (logo.getScaledWidth() / 2), y);
        try {
            canvas.addImage(logo);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}