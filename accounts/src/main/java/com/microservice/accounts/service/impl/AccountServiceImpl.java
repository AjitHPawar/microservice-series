package com.microservice.accounts.service.impl;

import com.microservice.accounts.constants.AccountsConstants;
import com.microservice.accounts.dto.AccountsDto;
import com.microservice.accounts.dto.CustomerDto;
import com.microservice.accounts.entity.Accounts;
import com.microservice.accounts.entity.Customer;
import com.microservice.accounts.exception.RecordAllreadyExistException;
import com.microservice.accounts.exception.ResourceNotFoundException;
import com.microservice.accounts.repository.AccountsRepository;
import com.microservice.accounts.repository.CustomerRepository;
import com.microservice.accounts.service.IAccountService;
import com.microservice.accounts.utils.AccountsUtility;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements IAccountService {
    @Autowired
    private AccountsUtility accountsUtility;

    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private CustomerRepository customerRepository;


    /**
     * Creates a new account.
     *
     * @param dto The account info
     * @return The created account
     */
    @Override
    public void createAccount(CustomerDto dto) {
        ModelMapper modelMapper = new ModelMapper();
        Customer customer = modelMapper.map(dto, Customer.class);
        Optional<Customer> byMobileNumber = customerRepository.findByMobileNumberOrEmail(dto.getMobileNumber(), dto.getEmail());
        if (byMobileNumber.isPresent()) {
            throw new RecordAllreadyExistException("Customer with this mobile number and email already exist");
        }
        Customer savedCustomer = customerRepository.save(customer);
        createNewAccount(savedCustomer);
    }

    private Accounts createNewAccount(Customer customer) {

        Accounts accounts = new Accounts();
        accounts.setAccountNumber(accountsUtility.getAccountNumber());
        accounts.setCustomer(customer);
        accounts.setAccountType(AccountsConstants.SAVINGS);
        accounts.setBranchAddress(AccountsConstants.ADDRESS);

        return accountsRepository.save(accounts);
    }

    /**
     * Retrieves an account by its mobile number.
     *
     * @param mobileNumber the mobile number to search for
     * @return the account if found, null otherwise
     */
    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer =
                customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                        () -> new ResourceNotFoundException("Customer", "Mobile Number", mobileNumber));
        ModelMapper modelMapper = new ModelMapper();
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        Accounts accounts = accountsRepository.findByCustomerId(customer.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "Customer Id", customer.getId())
        );
        customerDto.setAccountsDto(modelMapper.map(accounts, AccountsDto.class));

        return customerDto;
    }


    @Override
    public List<CustomerDto> fetchAllAccounts() {
        List<Accounts> all = accountsRepository.findAll();
        List<CustomerDto> customerDtos = new CopyOnWriteArrayList<>();
        if (all.size() > 0) {
            all.forEach(accounts -> {
                Customer customer = customerRepository.findById(accounts.getCustomer().getId()).orElseThrow(
                        () -> new ResourceNotFoundException("Customer", "Customer Id", accounts.getCustomer().getId())
                );
                CustomerDto customerDto = new ModelMapper().map(customer, CustomerDto.class);
                customerDto.setAccountsDto(new ModelMapper().map(accounts, AccountsDto.class));
                customerDtos.add(customerDto);
            });
            return customerDtos;
        } else throw new ResourceNotFoundException("Accounts", "Accounts", "Empty");

    }


    /**
     * Updates an existing account.
     *
     * @param customerDto The account info to be updated
     * @return true if the account was updated, false otherwise
     */
    @Override
    public CustomerDto updateAccount(CustomerDto customerDto) {
        if (customerDto != null) {
            Accounts accounts = accountsRepository.findByAccountNumber(customerDto.getAccountsDto().getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Accounts", "Account Number", customerDto.getAccountsDto().getAccountNumber())
            );
            new ModelMapper().map(customerDto.getAccountsDto(), accounts);

            accountsRepository.save(accounts);

            Customer updatedCustomer = customerRepository.findById(accounts.getCustomer().getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "Customer Id", accounts.getCustomer().getId())
            );
            new ModelMapper().map(customerDto, updatedCustomer);
            customerRepository.save(updatedCustomer);
            CustomerDto updatedCustomerDto = new CustomerDto();
            new ModelMapper().map(updatedCustomer, updatedCustomerDto);
            updatedCustomerDto.setAccountsDto(new ModelMapper().map(accounts, AccountsDto.class));
            return updatedCustomerDto;
        }
        throw new ResourceNotFoundException("Accounts Or Customer", "Accounts Or Customer", "Failed to update");

    }

    /**
     * Deletes an account.
     *
     * @param mobileNumber The mobile number of the account
     * @return true if the account was deleted, false otherwise
     */
    @Override
    public boolean deleteAccount(String mobileNumber) {
        Optional<Customer> customer = customerRepository.findByMobileNumber(mobileNumber);
        if (customer.isPresent()) {

            Accounts accounts = accountsRepository.findByCustomerId(customer.get().getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Accounts", "Customer Id", customer.get().getId())
            );
            accountsRepository.delete(accounts);
            customerRepository.delete(customer.get());
            return true;
        } else return false;
    }

}
