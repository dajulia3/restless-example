package com.djulia.example.stuff;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

public class Example {

}

//AccountActionsController.java
@RestController
class AccountActionsController {
    private AccountActionsService service;

    public AccountActionsController(AccountActionsService service) {
        this.service = service;
    }

    @RequestMapping(value = "/accounts/{accountNumber}/", method = RequestMethod.POST)
    public AccountActionsApiResponse createAccountAction(AccountAction action) {
        return new AccountActionsApiResponse(service.performAccountAction(action));
    }

}

//AccountActionsService.java
class AccountActionsService {
    private LegacyAccountsSystemsClient client;
    private AccountActionRequestFactory legacySystemRequestFactory;

    public AccountActionsService(LegacyAccountsSystemsClient client, AccountActionRequestFactory legacySystemRequestFactory) {
        this.client = client;
        this.legacySystemRequestFactory = legacySystemRequestFactory;
    }

    public AccountActionResult performAccountAction(AccountAction action) {
        return client.performRequest(legacySystemRequestFactory.make(action));
    }
}

//AccountActionRequestFactory.java
class AccountActionRequestFactory {
    public AccountActionRequest make(AccountAction action) {
        switch (action.getType()) {
            case WITHDRAWAL:
                return new AccountActionRequest("WDR", action.getAccountNumber(), action.getAmount());
            case DEPOSIT:
                return new AccountActionRequest("DPT", action.getAccountNumber(), action.getAmount());
            case TRANSFER:
                return new AccountActionRequest("XFR", action.getAccountNumber(), action.getAmount(), action.getDestinationAccountNumber());
            case CLOSE:
                return new AccountActionRequest("CLS", action.getAccountNumber());
        }
        throw new RuntimeException("Unsupported Operation");
    }
}


//AccountAction.java
class AccountAction {
    private final Integer accountNumber;
    private final Integer amount;
    private final Integer destinationAccountNumber;
    private ActionType type;

    public AccountAction(Integer accountNumber, Integer amount, Integer destinationAccountNumber, ActionType type) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.destinationAccountNumber = destinationAccountNumber;
        this.type = type;
    }

    public ActionType getType() {
        return type;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getDestinationAccountNumber() {
        return destinationAccountNumber;
    }
}

//AccountActionRequest.java We can't change this DTO, it's the format that the Legacy Service expects of our requests and
//was generated from a wsdl.
class AccountActionRequest {
    @JacksonXmlProperty(localName = "withdrawal_amt")
    private Integer withdrawalAmount;

    @JacksonXmlProperty(localName = "cmd_cd")
    private String commandCode;
    private final Integer accountNumber;

    @JacksonXmlProperty(localName = "dst_act_nbr")
    private Integer destinationAccountNumber;

    @JacksonXmlProperty(localName = "trxn_amt")
    private Integer amount;

    public AccountActionRequest(String commandCode, Integer accountNumber, Integer amount) {
        this.commandCode = commandCode;
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public AccountActionRequest(String commandCode, Integer accountNumber, Integer amount, Integer destinationAccountNumber) {

        this.commandCode = commandCode;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public AccountActionRequest(String commandCode, Integer accountNumber) {
        this.commandCode = commandCode;
        this.accountNumber = accountNumber;
    }
}
