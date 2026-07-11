package vciparser.controller;

import vciparser.model.Vehicle;
import vciparser.service.ParserService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class WebController {
    private final ParserService parserService;

    @Autowired
    public WebController(ParserService parserService){
        this.parserService = parserService;
    }
    @GetMapping("/")
    public String showIndexPage(){
        return "index";
    }
    @PostMapping("/")
    public String processReportText(@RequestParam("pdfFile") MultipartFile pdfFile, Model model){
        if(pdfFile.isEmpty()){
            model.addAttribute("error", "Por favor, selecciona un archivo valido.");
            return "index";
        }
        try {
            //Load PDF file into memory using PDFBox.
            byte[] bytes = pdfFile.getBytes();
            try (PDDocument document = Loader.loadPDF(bytes)){
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String pdfText  = pdfStripper.getText(document);

                Vehicle vehicle =  parserService.parserReportText(pdfText);
                model.addAttribute("vehicle", vehicle);
            }
        } catch(IOException e){
            e.printStackTrace();
            model.addAttribute("error",  "Ocurrió un erroral procesar  el archivo PDF:" + e.getMessage());
        }
        return "index";
    }
}
