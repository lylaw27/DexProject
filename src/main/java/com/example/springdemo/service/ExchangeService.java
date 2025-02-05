package com.example.springdemo.service;
import com.example.springdemo.model.*;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.math.BigDecimal;
import java.util.UUID;


@RestController
public class ExchangeService {
    Credentials exchangeCredentials = Credentials.create("0x23e23de15849bddade0d2986e1c77fe988c19451d3b7f77929bf68899810f610");
    Web3j web3 = Web3j.build(new HttpService("http://localhost:7545"));
    Exchange exchange = new Exchange(exchangeCredentials,web3);
    public UUID HandlePlaceOrder(PlaceOrderRequest payload) {
        return exchange.HandlePlaceOrder(payload);
    }
    public GetOrderData HandleGetBook(Market market) {
        return exchange.HandleGetOrderbook(market);
    }
    public User HandleGetOrders(String userId){ String realUserId = userId.toLowerCase(); return exchange.HandleGetOrders(realUserId);}
    public BigDecimal HandleGetBestBid(Market market) {
        return exchange.HandleGetBestBid(market);
    }
    public BigDecimal HandleGetBestAsk(Market market) {
        return exchange.HandleGetBestAsk(market);
    }
    public void HandleCancelOrder(Market market,UUID orderId) {
        exchange.HandleCancelOrder(market,orderId);
    }
}
