# Simple Crypto Exchange Project (Central Limit Order Book)

## 🧭 Overview
This project implements an order book system that interacts with the Ethereum blockchain to manage and match buy/sell orders in a centralized manner. The system provides core order book functionality, using matching engine to execute trades.

## 🛠️ Technologies Used
| Area                  | Tech                                                   |
|-----------------------|------------------------------------------              |
| Backend Exchange Services         | [Java Spring Boot](https://spring.io/)     |
| Ethereum Deployment Environment   | [Hardhat](https://hardhat.org/)            |
| Real-time communication           | [WebSocket](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)                                                               |
| Frontend Exchange Platform        | [Next](https://nextjs.org/)              |
| Container Application Development        | [Docker](https://www.docker.com/)              |
| Cloud Deployment        | [AWS ECS](https://aws.amazon.com/tw/ecs/)              |

## 🔗 Links

- [Demo](https://cex-frontend-fzro.vercel.app/)
- [Frontend Repository](https://github.com/lylaw27/cex-frontend)

## 🚀 Features

- Blockchain Integration: Connects to Ethereum mainnet/testnet (configurable). Hardhat for Local Development Environment
- Order Management:
  - Submit buy/sell orders
  - Cancel open orders
  - View order histories
- Order Matching: Implements price-time priority matching engine
- Web3 Integration: Java Web3j library for Ethereum interaction
- Real-time Data: Allow clients to connect with WebSocket API for persistent real-time data

## 🧮 Algorithms

### Matching Engine
```java
//Make sure market orders are matched synchronously
public synchronized ArrayList<Match> PlaceMarketOrder(Order order) {
        ArrayList<Match> matches = new ArrayList<>();
        ArrayList<Limit> emptyLimits = new ArrayList<>();
        if (order.bid) {
            //Check if bid order size exceeds current market volume
            if (order.size.compareTo(this.AskTotalVolume()) > 0) {
                System.err.printf("not enough total volume [size: %.2f] for market order [size: %.2f]", this.BidTotalVolume(), order.size);

            }
            //Loop through limit prices
            for (Limit limit : this.asks) {
                //Store all the matches
                matches.addAll(limit.Fill(order));

                //Store limit prices that have orders emptied
                if (limit.totalVolume.compareTo(BigDecimal.ZERO) == 0) {
                    emptyLimits.add(limit);
                }

                //Break forloop once market order is filled
                if(order.isFilled()){
                    break;
                }
            }
        } else {
           //Check if ask order size exceeds current market volume
            if (order.size.compareTo(this.BidTotalVolume()) > 0) {
                System.err.printf("not enough total volume [size: %.2f] for market order [size: %.2f]", this.AskTotalVolume(), order.size);
            }
            for (Limit limit : this.bids) {
               //Store all the matches
                matches.addAll(limit.Fill(order));

                //Store limit prices that have orders emptied
                if (limit.totalVolume.compareTo(BigDecimal.ZERO) == 0) {
                    emptyLimits.add(limit);
                }

                //Break forloop once market order is filled
                if(order.isFilled()){
                    break;
                }
            }
        }
        for (Limit limit : emptyLimits) {
            this.ClearLimit(!order.bid, limit);
        }
        return matches;
    }

//Filling orders within a limit price
ArrayList<Match> Fill(Order marketOrder) {
        ArrayList<Match> matches = new ArrayList<>();
         //Loop through the orders
        for (Order limitOrder : this.orderList) {
            Match match = this.FillOrder(marketOrder, limitOrder);
            matches.add(match);
            //Update total volume of that limit price
            totalVolume = totalVolume.subtract(match.sizeFilled);
            //Remove limit order from orderbook if it is filled
            if (limitOrder.isFilled()) {
                this.DeleteOrder(limitOrder);
            }
            //Break loop once market order is filled
            if (marketOrder.isFilled()) {
                break;
            }
        }
        return matches;
    }

//Matching single Market Order with single Limit Order
Match FillOrder(Order marketOrder, Order limitOrder) {
   Order ask;
   Order bid;
   BigDecimal sizeFilled;
   //Update order sizes when matching
   if (marketOrder.size.compareTo(limitOrder.size)>0) {
      marketOrder.size = marketOrder.size.subtract(limitOrder.size);
      sizeFilled = limitOrder.size;
      limitOrder.size = new BigDecimal(0);
   } else {
      limitOrder.size = limitOrder.size.subtract(marketOrder.size);
      sizeFilled = marketOrder.size;
      marketOrder.size = new BigDecimal(0);
   }
   if (marketOrder.bid) {
      bid = marketOrder;
      ask = limitOrder;
   } else {
      ask = marketOrder;
      bid = limitOrder;
   }
   return new Match(limitOrder.ID,ask, bid, sizeFilled, this.price);
}
```

## Prerequisites

- Java JDK 17+
- Maven 3.6+
- Access to Ethereum node (Infura/Alchemy/self-hosted)

## Installation

##### 1. Clone the repository:
```bash
   git clone https://github.com/yourusername/orderbook-blockchain.git
   cd orderbook-blockchain
```

##### 2. Build the project:
```bash
   mvn clean install
```

##### 3. Configure environment variables:
Edit src/main/resources/application.properties:
```java
//Ethereum network configuration
eth.serverip=${ETH_NETWORK_URL}
```

## Usage

### Running the Application
```bash
java -jar target/ExchangeGG-1.0.0.jar
```

### REST API Endpoints
The application provides REST endpoints for order management:
```http

POST /order: Submit new order

GET /order/{userId}: Get orders for specific user

GET /book/{market}: Get Orderbook for specific market

GET /book/{market}/bids: Get all current bid orders

GET /book/{market}/bestBid: Get best bid

GET /book/{market}/asks: Get all current ask orders

GET /book/{market}/bestAsk: Get best ask order

GET /trades/{market}: Get all executed trades

DELETE /book/{market}/{orderId}: Cancel order

```

### WebSocket Endpoints
The application provides STOMP endpoints for clients to subscribe for real-time data:
```http
//orderbook/asks: Sends all current ask orders

/orderbook/bids: Sends all current bid orders

/orderbook/trades: Sends all recent executed trades

/orderbook/candles: Sends the most recent 100 candles

/orderbook/user/{userId} : Sends user data and orders
```

## Deployment

This application is containerized with Docker and deployed to AWS ECS.
For the ethereum network, it is a test network created with hardhat, which is installed via
the npm package, then containerized with Docker and deployed to AWS ECS.

## License
MIT License

## Contact
For questions or contributions, please open an issue or contact me.
