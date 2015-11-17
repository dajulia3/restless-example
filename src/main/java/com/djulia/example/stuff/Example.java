package com.djulia.example.stuff;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

public class Example {

}

@RestController
class AccountActionsController {
    public static final String WITHDRAWAL_PATH = "/accounts/{accountNumber}/withdrawal";
    public static final String DEPOSIT_PATH = "/accounts/{accountNumber}/deposit";
    public static final String TRANSFER_PATH = "/accounts/{accountNumber}/transfer";
    public static final String CLOSE_ACCOUNT_PATH = "/accounts/{accountNumber}/transfer";
    private AccountActionsService service;

    public AccountActionsController(AccountActionsService service) {
        this.service = service;
    }

    @RequestMapping(value = WITHDRAWAL_PATH, method = RequestMethod.POST)
    public AccountActionsApiResponse createWithdrawalRequest(WithdrawalRequest withdrawal) {
        return new AccountActionsApiResponse(service.performWithdrawal(withdrawal), WITHDRAWAL_PATH, DEPOSIT_PATH, TRANSFER_PATH, CLOSE_ACCOUNT_PATH);
    }

    @RequestMapping(value = DEPOSIT_PATH, method = RequestMethod.POST)
    public AccountActionsApiResponse createDepositRequest(DepositRequest deposit) {
        return new AccountActionsApiResponse(service.performDeposit(deposit), WITHDRAWAL_PATH, DEPOSIT_PATH, TRANSFER_PATH, CLOSE_ACCOUNT_PATH);
    }

    @RequestMapping(value = TRANSFER_PATH, method = RequestMethod.POST)
    public AccountActionsApiResponse createTransferRequest(TransferRequest transfer) {
        return new AccountActionsApiResponse(service.performTransfer(transfer), WITHDRAWAL_PATH, DEPOSIT_PATH, TRANSFER_PATH, CLOSE_ACCOUNT_PATH);
    }

    @RequestMapping(value = CLOSE_ACCOUNT_PATH, method = RequestMethod.POST)
    public AccountActionsApiResponse createCloseAccountRequest(AccountCloseRequest close) {
        return new AccountActionsApiResponse(service.closeAccount(close));
    }

}

class AccountActionsService {
    private LegacyAccountsSystemsClient client;

    public AccountActionsService(LegacyAccountsSystemsClient client) {
        this.client = client;
    }

    public AccountActionResult performWithdrawal(WithdrawalRequest withdrawal) {
        return client.performRequest(new LegacyAccountActionRequest("WDR", withdrawal.getAccountNumber(), withdrawal.getAmount()));
    }

    public AccountActionResult performDeposit(DepositRequest deposit) {
        return client.performRequest(new LegacyAccountActionRequest("WDR", deposit.getAccountNumber(), deposit.getAmount()));
    }

    public AccountActionResult performTransfer(TransferRequest transfer) {
        return client.performRequest(new LegacyAccountActionRequest("XFR", transfer.getSourceAccountNumber(), transfer.getAmount(), transfer.getDestinationAccountNumber()));
    }

    public AccountActionResult closeAccount(AccountCloseRequest close) {
        return client.performRequest(new LegacyAccountActionRequest("CLS", close.getAccountNumber()));
    }

}

class WithdrawalRequest {
    private final Integer accountNumber;
    private final Integer amount;

    public WithdrawalRequest(Integer accountNumber, Integer amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public Integer getAmount() {
        return amount;
    }
}

class DepositRequest {
    private final Integer amount;
    private final Integer accountNumber;

    DepositRequest(Integer amount, Integer accountNumber) {
        this.amount = amount;
        this.accountNumber = accountNumber;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public Integer getAmount() {
        return amount;
    }
}

class AccountCloseRequest {
    private Integer accountNumber;

    public AccountCloseRequest(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }
}

class TransferRequest {
    private final Integer amount;
    private final Integer sourceAccountNumber;
    private final Integer destinationAccountNumber;

    private TransferRequest(Integer amount, Integer sourceAccountNumber, Integer destinationAccountNumber) {
        this.amount = amount;
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public Integer getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getDestinationAccountNumber() {
        return destinationAccountNumber;
    }
}

//We can't change this DTO, it's the format that the Legacy Service expects of our requests and
//was generated from a wsdl.
class LegacyAccountActionRequest {
    @JacksonXmlProperty(localName = "withdrawal_amt")
    private Integer withdrawalAmount;

    @JacksonXmlProperty(localName = "cmd_cd")
    private String commandCode;
    private final Integer accountNumber;

    @JacksonXmlProperty(localName = "dst_act_nbr")
    private Integer destinationAccountNumber;

    @JacksonXmlProperty(localName = "trxn_amt")
    private Integer amount;

    public LegacyAccountActionRequest(String commandCode, Integer accountNumber, Integer amount) {
        this.commandCode = commandCode;
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public LegacyAccountActionRequest(String commandCode, Integer accountNumber, Integer amount, Integer destinationAccountNumber) {

        this.commandCode = commandCode;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public LegacyAccountActionRequest(String commandCode, Integer accountNumber) {
        this.commandCode = commandCode;
        this.accountNumber = accountNumber;
    }
}
