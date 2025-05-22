# Simple Crypto Exchange Project (Central Limit Order Book)

## 🧭Overview
This project implements an order book system that interacts with the Ethereum blockchain to manage and match buy/sell orders in a centralized manner. The system provides core order book functionality, using matching engine to execute trades.

## 🛠️ Technologies Used
| Area                  | Tech                                                   |
|-----------------------|------------------------------------------              |
| Backend Exchange Services         | [Java Spring Boot](https://spring.io/)     |
| Ethereum Deployment Environment   | [Hardhat](https://hardhat.org/)            |
| Real-time communication           | [WebSocket](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)                                                               |
| Frontend Exchange Platform        | [NextJs](https://nextjs.org/)              |


## 🚀 Features

- Blockchain Integration: Connects to Ethereum mainnet/testnet (configurable). Hardhat for Local Development Environment
- Order Management:
  - Submit buy/sell orders
  - Cancel open orders
  - View order histories
- Order Matching: Implements price-time priority matching engine
- Web3 Integration: Java Web3j library for Ethereum interaction
- Real-time Data: Allow clients to connect with WebSocket API for persistent real-time data

## Prerequisites

- Java JDK 17+
- Maven 3.6+
- Access to Ethereum node (Infura/Alchemy/self-hosted)
- Environment variables for sensitive configuration

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
```bash
   export ETH_NETWORK_URL=YOUR_ETHEREUM_NODE_URL
   export WALLET_PRIVATE_KEY=YOUR_PRIVATE_KEY
   export CONTRACT_ADDRESS=ORDERBOOK_CONTRACT_ADDRESS
```
## Usage

### Running the Application
```bash
java -jar target/orderbook-1.0.0.jar
```

### API Endpoints
The application provides REST endpoints for order management:
```http
POST /order: Submit new order

GET /order/{userId} - Get orders for specific user

GET /book/{market} - Get Orderbook for specific market

GET /book/{market}/bids - Get all current bid orders

GET /book/{market}/bestBid - Get best bid

GET /book/{market}/asks - Get all current ask orders

GET /book/{market}/bestAsk - Get best ask order

GET /trades/{market} - Get all executed trades

DELETE /book/{market}/{orderId} - Cancel order

GET /orders - Get open orders

GET /trades - Get recent trades
```

## Configuration

Edit src/main/resources/application.properties:
properties
# Ethereum network configuration
web3j.network=ropsten
web3j.node-url=${ETH_NETWORK_URL}

# Contract settings
contract.address=${CONTRACT_ADDRESS}
gas.price=20
gas.limit=500000


## Development

### Building Smart Contracts
Solidity contracts are in contracts/. To compile:
bash
solc --abi --bin contracts/OrderBook.sol -o build/


### Testing
Run unit tests:
bash
mvn test


For integration tests with Ganache:
bash
mvn verify -Pintegration-test


## Security Considerations

- Never commit private keys to source control
- Use environment variables for sensitive data
- Test thoroughly on testnets before mainnet deployment
- Implement proper gas estimation to avoid failed transactions

## License
MIT License

## Contact
For questions or contributions, please open an issue or contact project maintainers.
