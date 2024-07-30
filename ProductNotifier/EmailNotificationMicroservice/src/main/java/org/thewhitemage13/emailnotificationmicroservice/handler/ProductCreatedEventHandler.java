package org.thewhitemage13.emailnotificationmicroservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.thewhitemage13.core.ProductCreatedEvent;
import org.thewhitemage13.emailnotificationmicroservice.exception.NonRetryableException;
import org.thewhitemage13.emailnotificationmicroservice.exception.RetryableException;

@Component
//@KafkaListener(topics = "product-created-events-topic", groupId = "product-created-events")
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private RestTemplate restTemplate;

    public ProductCreatedEventHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

//    @KafkaHandler
//    public void handle(ProductCreatedEvent productCreatedEvent) {
//        if (true)
//            throw new NonRetryableException("Non Retryable Exception");
//        LOGGER.info("Received event: {}", productCreatedEvent.getTitle());
//    }

    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent) {
        LOGGER.info("Received event: {}", productCreatedEvent.getTitle());
        String url = "http://localhost:8090/response/200";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if(response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response: {}", response.getBody());
            }
        }catch (ResourceAccessException e){
            LOGGER.error(e.getMessage());
            throw new RetryableException(e);
        }catch (HttpServerErrorException e){
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }

    }

}

















































