package com.example.demo.main.rest;

import com.example.demo.main.service.FileAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class FileAnalysisController {

    @Autowired
    private FileAnalysisService fileAnalysisService;

    @GetMapping("/gs/holyRelic")
    public ResponseEntity<Object> getGSHolyRelic(
            @PathVariable(required = false,name = "fileFolder") String fileFolder){
        return new ResponseEntity<>(fileAnalysisService.getFileMsg(fileFolder), HttpStatus.OK);
    }

}
