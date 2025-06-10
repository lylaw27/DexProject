package com.example.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ExchangeGG {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ExchangeGG.class, args);
        OrderGenerator.GenerateOrders();
}}
