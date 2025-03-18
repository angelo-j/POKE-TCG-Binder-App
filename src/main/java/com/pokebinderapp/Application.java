package com.pokebinderapp;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Base64;

@ComponentScan(basePackages = {"com.pokebinderapp"})
@SpringBootApplication
public class Application {

    // todo: allow user to input quantity during trades

  public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
  }



}
