package com.maticz.PDF.Generator.controller;


import com.maticz.PDF.Generator.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/test")
public class PDFController {

    @Autowired
    private PDFService pdfService;

    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(String age, String date, String startTime, Integer idLocation, String endTime,
                                              String phone, String childName, Integer idProgType) {
        try {
            byte[] pdfBytes = pdfService.createPdfInMemoryInvite(age, date, startTime, idLocation, endTime, phone, childName, idProgType);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            String filename = "vabilo_" + childName + ".pdf";

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("vabilo")
    public String vabilo() {
        return "invite.html";
    }

}