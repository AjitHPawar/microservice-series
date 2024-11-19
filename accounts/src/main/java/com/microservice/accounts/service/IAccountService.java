package com.microservice.accounts.service;

import com.microservice.accounts.dto.CustomerDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IAccountService {

    /**
     * Creates a new account.
     *
     * @param dto The account info
     * @return The created account
     */
    public void createAccount(CustomerDto dto);

    /**
     * Retrieves an account by its mobile number.
     *
     * @param mobileNumber the mobile number to search for
     * @return the account if found, null otherwise
     */
    public CustomerDto fetchAccount(String mobileNumber);
   /**
     * Updates an existing account.
     *
     * @param customerDto The account info to be updated
     * @return true if the account was updated, false otherwise
     */
   public CustomerDto updateAccount(CustomerDto customerDto);
   /**
     * Deletes an account.
     *
     * @param mobileNumber The mobile number of the account
     * @return true if the account was deleted, false otherwise
     */
   public boolean deleteAccount(String mobileNumber);

    List<CustomerDto> fetchAllAccounts();
}
