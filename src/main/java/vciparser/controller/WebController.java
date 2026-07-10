package vciparser.controller;

import vciparser.model.Vehicle;
import vciparser.service.ParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String processReportText(@RequestParam("rawText") String rawText, Model model){
        Vehicle vehicle = parserService.parserReportText(rawText);
        model.addAttribute("vehicle", vehicle);
        return "index";
    }
}
