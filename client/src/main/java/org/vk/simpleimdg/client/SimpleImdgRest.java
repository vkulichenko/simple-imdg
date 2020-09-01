package org.vk.simpleimdg.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SimpleImdgRest {
    public static void main(String[] args) {
        SpringApplication.run(SimpleImdgRest.class, args);
    }

    @Bean
    public SimpleImdgClient imdgClient() throws Exception {
        SimpleImdgClient client = new SimpleImdgClient();

        client.start();

        return client;
    }
}
