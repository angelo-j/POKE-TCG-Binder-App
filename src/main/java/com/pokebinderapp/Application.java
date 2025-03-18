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

    // todo: fix tests

//   public static void main(String[] args) {
//       SpringApplication.run(Application.class, args);
//   }

    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Generated JWT Secret Key: " + base64Key);
    }

}
