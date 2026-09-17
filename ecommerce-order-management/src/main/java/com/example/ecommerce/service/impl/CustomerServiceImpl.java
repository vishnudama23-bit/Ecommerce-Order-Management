package com.example.ecommerce.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.dto.request.CustomerRequestDto;
import com.example.ecommerce.dto.response.CustomerResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.DuplicateEmailException;
import com.example.ecommerce.exception.DuplicatePhoneException;
import com.example.ecommerce.exception.InvalidOrderException;
import com.example.ecommerce.mapper.CustomerMapper;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper,
            UserRepository userRepository,
            OrderRepository orderRepository) {

        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public CustomerResponseDto createOrUpdateCustomer(
            Long id,
            CustomerRequestDto request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN"));

        Customer customer;

        if (id == null) {

            if (!isAdmin &&
                    customerRepository.existsByUserEmail(email)) {

                throw new DuplicateEmailException(
                        "Customer profile already exists for this user");
            }

            if (customerRepository.existsByEmail(
                    request.getEmail())) {

                throw new DuplicateEmailException(
                        "Email already registered");
            }

            if (customerRepository.existsByPhone(
                    request.getPhone())) {

                throw new DuplicatePhoneException(
                        "Phone number already registered");
            }

            customer =
                    customerMapper.toEntity(request);

            if (!isAdmin) {

                User user =
                        userRepository.findByEmail(email)
                                .orElseThrow(() ->
                                        new CustomerNotFoundException(
                                                "User not found"));

                customer.setUser(user);
            }

        } else {

            customer =
                    customerRepository.findById(id)
                            .orElseThrow(() ->
                                    new CustomerNotFoundException(
                                            "Customer not found with id: "
                                                    + id));

            if (!isAdmin) {

                if (customer.getUser() == null ||
                        !customer.getUser()
                                .getEmail()
                                .equalsIgnoreCase(email)) {

                    throw new CustomerNotFoundException(
                            "Customer not found with id: "
                                    + id);
                }
            }

            if (customerRepository.existsByEmailAndIdNot(
                    request.getEmail(),
                    id)) {

                throw new DuplicateEmailException(
                        "Email already registered");
            }

            if (customerRepository.existsByPhoneAndIdNot(
                    request.getPhone(),
                    id)) {

                throw new DuplicatePhoneException(
                        "Phone number already registered");
            }

            customer.setName(request.getName());
            customer.setEmail(request.getEmail());
            customer.setPhone(request.getPhone());

            if (!isAdmin && customer.getUser() == null) {

                User user =
                        userRepository.findByEmail(email)
                                .orElseThrow(() ->
                                        new CustomerNotFoundException(
                                                "User not found"));

                customer.setUser(user);
            }
        }

        Customer savedCustomer =
                customerRepository.save(customer);

        return customerMapper.toResponseDto(
                savedCustomer);
    }

    @Override
    public List<CustomerResponseDto> getAllCustomers() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN"));

        if (isAdmin) {

            List<Customer> customers =
                    customerRepository.findAll();

            return customers.stream()
                    .map(customerMapper::toResponseDto)
                    .toList();
        }

        String email =
                authentication.getName();

        Customer customer =
                customerRepository.findByUserEmail(email)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer profile not found"));

        return List.of(
                customerMapper.toResponseDto(customer));
    }

    @Override
    public CustomerResponseDto getCustomerById(
            Long id) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN"));

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: "
                                                + id));

        if (!isAdmin) {

            String email =
                    authentication.getName();

            if (customer.getUser() == null ||
                    !customer.getUser()
                            .getEmail()
                            .equalsIgnoreCase(email)) {

                throw new CustomerNotFoundException(
                        "Customer not found with id: "
                                + id);
            }
        }

        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        boolean isAdmin =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN"));

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new CustomerNotFoundException(
                                        "Customer not found with id: "
                                                + id));

        if (!isAdmin) {

            String email =
                    authentication.getName();

            if (customer.getUser() == null ||
                    !customer.getUser()
                            .getEmail()
                            .equalsIgnoreCase(email)) {

                throw new CustomerNotFoundException(
                        "Customer not found with id: "
                                + id);
            }
        }

        if (orderRepository.existsByCustomerId(id)) {

            throw new InvalidOrderException(
                    "Customer cannot be deleted because orders exist for this customer");
        }

        customerRepository.delete(customer);
    }
}