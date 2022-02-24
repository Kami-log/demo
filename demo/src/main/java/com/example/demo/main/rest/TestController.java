package com.example.demo.main.rest;

import com.example.demo.main.service.FileAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.io.File;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("run")
    public ResponseEntity<Object> getData(){
        return new ResponseEntity<>("Test", HttpStatus.OK);
    }

}
