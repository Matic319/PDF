package com.maticz.PDF.Generator.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PDFService {

    private static final float POINTS_PER_INCH = 72f;

    public byte[] createPdfInMemoryInvite(String age, String date, String startTime, Integer idLocation, String endTime,
                                          String phone, String childName, Integer idProgType) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(setTemplate(idProgType));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDDocument document = Loader.loadPDF(inputStream.readAllBytes());
            PDPage page = document.getPage(0);
            PDRectangle pageSize = page.getMediaBox();
            float pageHeightInPoints = pageSize.getHeight();

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                insertTextIntoInviteTemplate(age,date,startTime,idLocation,endTime,phone,childName, idProgType, contentStream,pageHeightInPoints,document);
            }

            document.save(outputStream);
            document.close();

            return outputStream.toByteArray();
        }
    }


    public void insertTextIntoInviteTemplate(String age, String date, String startTime, Integer idLocation, String endTime,
                                             String phone, String childName, Integer idProgType , PDPageContentStream contentStream, float pageHeightInPoints, PDDocument document) throws IOException {

        contentStream.setNonStrokingColor(1f, 1f, 1f);

        if(Integer.parseInt(age) > 9){
            insertTextFontZing(
                    contentStream, age +".", 2.66f, 1.9f, pageHeightInPoints, 60,document);
        }else {
            insertTextFontZing(
                    contentStream, age +".", 2.74f, 1.9f, pageHeightInPoints, 60,document);
        }

        insertTextFontMontserrat(
                contentStream, date, 2.62f, 4.2f, pageHeightInPoints, 18,document);
        insertTextFontMontserrat(
                contentStream, startTime, 2.62f, 4.99f, pageHeightInPoints, 18,document);
        if(idLocation == 2){
           insertTextFontMontserrat(
                    contentStream, locationName(idLocation), 2.62f, 5.75f, pageHeightInPoints, 15,document);
        }else {
            insertTextFontMontserrat(
                    contentStream, locationName(idLocation), 2.62f, 5.75f, pageHeightInPoints, 18,document);
        }

        insertTextFontMontserrat(
                contentStream, locationAddress(idLocation), 2.62f, 6.55f, pageHeightInPoints, 18,document);
        insertTextFontMontserrat(
                contentStream, endTime, 2.62f, 7.3f, pageHeightInPoints, 18,document);
        insertTextFontMontserrat(
                contentStream, convertPhoneNumber(phone), 3.48f, 8.38f, pageHeightInPoints, 18,document);

        if(childName.length() <= 6) {
            insertTextFontMontserrat(
                    contentStream, childName.toUpperCase(), 5.85f, 11.1f, pageHeightInPoints, 32,document);
        }else if (childName.length() < 10 && !childName.contains(" ")){
            insertTextFontMontserrat(
                    contentStream, childName.toUpperCase(), 5.8f, 11.1f, pageHeightInPoints, 27,document);
        } else if(childName.contains(" ")){
            insertTextFontMontserrat(
                    contentStream, childName.split(" ")[0].toUpperCase(), 5.78f, 11.02f, pageHeightInPoints, 23,document);

            insertTextFontMontserrat(
                    contentStream, childName.split(" ")[1].toUpperCase(), 5.78f, 11.38f, pageHeightInPoints, 23,document);
        } else if (childName.length() > 10 && childName.length() < 16) {
            insertTextFontMontserrat(
                    contentStream, childName.toUpperCase(), 5.70f, 11.1f, pageHeightInPoints, 16,document);
        } else {
            insertTextFontMontserrat(
                    contentStream, childName.toUpperCase(), 5.70f, 11.1f, pageHeightInPoints, 13,document);
        }



    }

    public void insertTextFontMontserrat(PDPageContentStream contentStream, String text, float xInches, float yInches,
                                         float pageHeightInPoints, float fontSize, PDDocument document) throws IOException {
        contentStream.beginText();
        PDType0Font font = PDType0Font.load(document, PDType0Font.class.getResourceAsStream("/montserrat.ttf"), true);

        contentStream.setFont(font, fontSize);
        float x = inchesToPoints(xInches);
        float y = pageHeightInPoints - inchesToPoints(yInches);
        contentStream.newLineAtOffset(x, y);

        String sanitizedText = sanitizeText(text, font);
        contentStream.showText(sanitizedText);
        contentStream.endText();
    }

    private String sanitizeText(String text, PDFont font) {
        if (text == null) {
            return "";
        }

        StringBuilder cleanedText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (Character.isISOControl(c) ) {
                cleanedText.append(' ');
                continue;
            }

            try {
                // Try to encode the character
                font.encode(String.valueOf(c));
                cleanedText.append(c);
            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                cleanedText.append(' ');
            }
        }

        return cleanedText.toString();
    }



    public void insertTextFontZing(PDPageContentStream contentStream, String text, float xInches, float yInches,
                                   float pageHeightInPoints, float fontSize, PDDocument document) throws IOException {
        contentStream.beginText();
        PDType0Font font = PDType0Font.load(document, PDType0Font.class.getResourceAsStream("/zingrust.ttf"), true);

        contentStream.setFont(font, fontSize);
        float x = inchesToPoints(xInches);
        float y = pageHeightInPoints - inchesToPoints(yInches);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    public float inchesToPoints(float inches) {
        return inches * POINTS_PER_INCH;
    }

    public String convertPhoneNumber(String phone) {
        String phoneNumber = phone.trim().replaceAll("\\s", "");
        if (phoneNumber.startsWith("+386") || phoneNumber.startsWith("00386") || phoneNumber.startsWith("386") || phoneNumber.startsWith("+")) {
            phoneNumber = "0" + phoneNumber.substring(phoneNumber.length() - 8);
            phoneNumber = phoneNumber.substring(0, 3) + " " + phoneNumber.substring(3, 6) + " " + phoneNumber.substring(6);
        } else if (phoneNumber.length() == 8) {
            phoneNumber = "0" + phoneNumber.substring(0, 2) + " " + phoneNumber.substring(2, 5) + " " + phoneNumber.substring(5);
        }else if (phoneNumber.length() < 6) {
            return phoneNumber;
        }
        else {
            phoneNumber = phoneNumber.substring(0, 3) + " " + phoneNumber.substring(3, 6) + " " + phoneNumber.substring(6);
        }
        return phoneNumber;
    }

    public String locationAddress(Integer idLocation) {
        return switch (idLocation) {
            case 1 -> "Leskoškova cesta 3";
            case 2, 3 -> "Moskovska ulica 10";
            case 5 -> "Jurčkova cesta 223";
            case 6 -> "Tržaška cesta 7";
            case 100 -> "Testni naslov 5";
            default -> throw new IllegalStateException("wrong idLocation: " + idLocation);
        };
    }

    public String locationName(Integer idLocation) {
        return switch (idLocation) {
            case 1 -> "WOOP! Fun parku";
            case 2 -> "WOOP! Karting & Glow golf";
            case 3 -> "WOOP! Arena";
            case 5 -> "WOOP! Izzivi";
            case 6 -> "WOOP! Maribor";
            case 100 -> "WOOP! Test";
            default -> throw new IllegalStateException("wrong idLocation: " + idLocation);
        };
    }

    private String setTemplate(Integer idProgType){
        return switch (idProgType){
            case 38 -> "/inviteTP.pdf";
            case 9 -> "/inviteFW.pdf";
            case 39 -> "/inviteKarting.pdf";
            case 41 -> "/inviteCubes.pdf";
            case 42 -> "/inviteER.pdf";
            case 43 -> "/inviteGG.pdf";
            case 44 -> "/inviteBowling.pdf";
            case 45 -> "/inviteLT.pdf";


            default -> throw new IllegalArgumentException("idProgType napačn");
        };
    }
}
