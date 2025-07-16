package com.github.taxbeans.forms.nz;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/ir3")
@Tag(name = "IR3 API", description = "API for generating IR3 2025 forms")
public class IR3ApiController {

    @GetMapping("/2025")
    @Operation(summary = "Get a filled IR3 2025 form", description = "Returns a sample filled IR3 2025 form as JSON.")
    public IR3Form2025 getFilledIR3Form2025() {
        IR3Form2025 form = new IR3Form2025();
        // Example: fill in some fields for demonstration
        form.setFirstname("Jane");
        form.setSurname("Doe");
        form.setDateOfBirth(java.time.LocalDate.of(1980, 1, 1));
        form.setIrdNumber("123-456-789");
        form.setPostalAddressLine1("123 Main St");
        form.setPostalAddressLine2("Apt 4B");
        form.setPhonePrefix("021");
        form.setPhoneNumberExcludingPrefix("1234567");
        // ... add more fields as needed ...
        return form;
    }

    @Operation(
        summary = "Download filled IR3 2025 form as PDF",
        description = "Returns a filled IR3 2025 form as a PDF file.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "PDF file",
                content = @Content(
                    mediaType = "application/pdf",
                    schema = @Schema(type = "string", format = "binary")
                )
            )
        }
    )
    @GetMapping("/2025/pdf")
    public ResponseEntity<byte[]> getFilledIR3Form2025Pdf() throws Exception {
        // TODO: Replace with real PDF generation logic
        byte[] pdfBytes = generateFilledIr3Pdf();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "IR3-2025.pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    private byte[] generateFilledIr3Pdf() {
        // Placeholder: return an empty PDF for now
        return new byte[0];
    }
} 