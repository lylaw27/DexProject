package com.example.springdemo.model;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import java.io.IOException;
import java.math.BigDecimal;

public class ETHTransaction {
    public static TransactionReceipt TransferETH(Web3j web3, Credentials fromAddress, Credentials toAddress, BigDecimal amount){
        try {
            return Transfer.sendFunds(
                web3, fromAddress, toAddress.getAddress(),
                amount, Convert.Unit.ETHER).send();
        }
        catch(IOException | InterruptedException | TransactionException ex) {
            throw new RuntimeException("Error whilst sending json-rpc requests");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
