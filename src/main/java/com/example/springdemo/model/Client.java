package com.example.springdemo.model;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.UUID;
import java.math.BigDecimal;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class Client {
    RestClient restClient;
    public Client() {
        restClient = RestClient.create();
    }
    public UUID PlaceOrder(PlaceOrderRequest payload) {
        ResponseEntity<UUID> response = restClient.post()
                .uri("http://localhost:8080/order")
                .contentType(APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toEntity(UUID.class);
        return response.getBody();
    }
    public BigDecimal GetBestBid() {
            return restClient.get()
                .uri("http://localhost:8080/book/{market}/bestBid",Market.ETH)
                .retrieve()
                .body(BigDecimal.class);
    }
    public BigDecimal GetBestAsk() {
        return restClient.get()
                .uri("http://localhost:8080/book/{market}/bestAsk",Market.ETH)
                .retrieve()
                .body(BigDecimal.class);
    }
    public void CancelOrder(UUID orderId) {
        ResponseEntity<Void> response = restClient.delete()
                .uri("http://localhost:8080/book/{market}/{orderId}",Market.ETH,orderId)
                .retrieve()
                .toBodilessEntity();
    }
    public ArrayList<Trade> GetTrades() {
        return restClient.get()
                .uri("http://localhost:8080/trades/{market}",Market.ETH)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ArrayList<Trade>>() {})
                .getBody();
    }
    public UserInfo GetOrders(String userId){
        return restClient.get()
                .uri("http://localhost:8080/order/{userId}",userId)
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(UserInfo.class)
                .getBody();
    }
}
