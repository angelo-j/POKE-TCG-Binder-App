package com.pokebinderapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.pokebinderapp"})
@SpringBootApplication
public class Application {

    // todo: allow user to input quantity during trades

  public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
  }
  
}
