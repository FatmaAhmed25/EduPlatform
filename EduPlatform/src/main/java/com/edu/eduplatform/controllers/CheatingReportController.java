package com.edu.eduplatform.controllers;

import com.edu.eduplatform.services.CheatingReportService;
import com.edu.eduplatform.utils.quiz.pdf.PdfService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/cheating-report")
public class CheatingReportController {

    @Autowired
    private CheatingReportService cheatingReportService;

    @Autowired
    private PdfService pdfService;

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping(value = "/save-photo", consumes = {"multipart/form-data"})
    public ResponseEntity<String> savePhoto(@RequestParam("studentId") Long studentId,
                                            @RequestParam("quizId") Long quizId,
                                            @RequestParam("file") MultipartFile file) {
        try {
            cheatingReportService.savePhoto(studentId, quizId, file);
            return ResponseEntity.ok("Photo saved successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save photo");
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @Operation(summary = "Generate Cheating Report PDF of a student quiz submission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF generated successfully", content = {
                    @Content(mediaType = "application/pdf")
            }),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/cheating-report/{id}")
    public ResponseEntity<byte[]> getCheatingReport(@PathVariable("id") Long id) {
        try {
            byte[] pdfBytes = pdfService.generateCheatingReportPdf(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "cheating-report.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
