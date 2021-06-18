package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

    protected Logger logger = LoggerFactory.getLogger(CartController.class);
}
