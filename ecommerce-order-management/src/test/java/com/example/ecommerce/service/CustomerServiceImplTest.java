package com.example.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.ecommerce.dto.request.CustomerRequestDto;
import com.example.ecommerce.dto.response.CustomerResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.DuplicateEmailException;
import com.example.ecommerce.exception.DuplicatePhoneException;
import com.example.ecommerce.mapper.CustomerMapper;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.service.impl.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;

    private CustomerRequestDto request;

    private CustomerResponseDto response;

    @BeforeEach
    void setUp() {

        customer = new Customer(
                1L,
                "Dama",
                "dama@gmail.com",
                "9876543210"
        );

        request = new CustomerRequestDto(
                "Dama",
                "dama@gmail.com",
                "9876543210"
        );

        response = new CustomerResponseDto(
                1L,
                "Dama",
                "dama@gmail.com",
                "9876543210"
        );
    }

    @Test
    void shouldCreateCustomer() {

        when(customerRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(false);

        when(customerRepository.existsByPhone(
                request.getPhone()))
                .thenReturn(false);

        when(customerMapper.toEntity(request))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponseDto(customer))
                .thenReturn(response);

        CustomerResponseDto result =
                customerService.createOrUpdateCustomer(
                        null,
                        request);

        assertEquals(
                response,
                result);

        verify(customerRepository)
                .existsByEmail(request.getEmail());

        verify(customerRepository)
                .existsByPhone(request.getPhone());

        verify(customerRepository)
                .save(customer);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        when(customerRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(true);

        DuplicateEmailException exception =
                assertThrows(
                        DuplicateEmailException.class,
                        () -> customerService
                                .createOrUpdateCustomer(
                                        null,
                                        request));

        assertEquals(
                "Email already registered",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneAlreadyExists() {

        when(customerRepository.existsByEmail(
                request.getEmail()))
                .thenReturn(false);

        when(customerRepository.existsByPhone(
                request.getPhone()))
                .thenReturn(true);

        DuplicatePhoneException exception =
                assertThrows(
                        DuplicatePhoneException.class,
                        () -> customerService
                                .createOrUpdateCustomer(
                                        null,
                                        request));

        assertEquals(
                "Phone number already registered",
                exception.getMessage());
    }

    @Test
    void shouldUpdateCustomer() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.existsByEmailAndIdNot(
                request.getEmail(),
                1L))
                .thenReturn(false);

        when(customerRepository.existsByPhoneAndIdNot(
                request.getPhone(),
                1L))
                .thenReturn(false);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponseDto(customer))
                .thenReturn(response);

        CustomerResponseDto result =
                customerService.createOrUpdateCustomer(
                        1L,
                        request);

        assertEquals(
                response,
                result);

        verify(customerRepository)
                .findById(1L);

        verify(customerRepository)
                .existsByEmailAndIdNot(
                        request.getEmail(),
                        1L);

        verify(customerRepository)
                .existsByPhoneAndIdNot(
                        request.getPhone(),
                        1L);

        verify(customerRepository)
                .save(customer);
    }

    @Test
    void shouldAllowCustomerToKeepOwnEmail() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.existsByEmailAndIdNot(
                request.getEmail(),
                1L))
                .thenReturn(false);

        when(customerRepository.existsByPhoneAndIdNot(
                request.getPhone(),
                1L))
                .thenReturn(false);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        when(customerMapper.toResponseDto(customer))
                .thenReturn(response);

        CustomerResponseDto result =
                customerService.createOrUpdateCustomer(
                        1L,
                        request);

        assertEquals(
                response,
                result);
    }

    @Test
    void shouldThrowExceptionWhenEmailBelongsToAnotherCustomer() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.existsByEmailAndIdNot(
                request.getEmail(),
                1L))
                .thenReturn(true);

        DuplicateEmailException exception =
                assertThrows(
                        DuplicateEmailException.class,
                        () -> customerService
                                .createOrUpdateCustomer(
                                        1L,
                                        request));

        assertEquals(
                "Email already registered",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneBelongsToAnotherCustomer() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.existsByEmailAndIdNot(
                request.getEmail(),
                1L))
                .thenReturn(false);

        when(customerRepository.existsByPhoneAndIdNot(
                request.getPhone(),
                1L))
                .thenReturn(true);

        DuplicatePhoneException exception =
                assertThrows(
                        DuplicatePhoneException.class,
                        () -> customerService
                                .createOrUpdateCustomer(
                                        1L,
                                        request));

        assertEquals(
                "Phone number already registered",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFoundDuringUpdate() {

        when(customerRepository.findById(99L))
                .thenReturn(Optional.empty());

        CustomerNotFoundException exception =
                assertThrows(
                        CustomerNotFoundException.class,
                        () -> customerService
                                .createOrUpdateCustomer(
                                        99L,
                                        request));

        assertEquals(
                "Customer not found with id: 99",
                exception.getMessage());
    }

    @Test
    void shouldGetAllCustomers() {

        Customer customer2 =
                new Customer(
                        2L,
                        "Arun",
                        "arun@gmail.com",
                        "9999999999"
                );

        CustomerResponseDto response2 =
                new CustomerResponseDto(
                        2L,
                        "Arun",
                        "arun@gmail.com",
                        "9999999999"
                );

        when(customerRepository.findAll())
                .thenReturn(List.of(
                        customer,
                        customer2));

        when(customerMapper.toResponseDto(customer))
                .thenReturn(response);

        when(customerMapper.toResponseDto(customer2))
                .thenReturn(response2);

        List<CustomerResponseDto> result =
                customerService.getAllCustomers();

        assertEquals(
                2,
                result.size());

        assertEquals(
                response,
                result.get(0));

        assertEquals(
                response2,
                result.get(1));
    }

    @Test
    void shouldGetCustomerById() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerMapper.toResponseDto(customer))
                .thenReturn(response);

        CustomerResponseDto result =
                customerService.getCustomerById(1L);

        assertEquals(
                response,
                result);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findById(99L))
                .thenReturn(Optional.empty());

        CustomerNotFoundException exception =
                assertThrows(
                        CustomerNotFoundException.class,
                        () -> customerService
                                .getCustomerById(99L));

        assertEquals(
                "Customer not found with id: 99",
                exception.getMessage());
    }

    @Test
    void shouldDeleteCustomer() {

        when(customerRepository.existsById(1L))
                .thenReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository)
                .existsById(1L);

        verify(customerRepository)
                .deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingCustomerNotFound() {

        when(customerRepository.existsById(99L))
                .thenReturn(false);

        CustomerNotFoundException exception =
                assertThrows(
                        CustomerNotFoundException.class,
                        () -> customerService
                                .deleteCustomer(99L));

        assertEquals(
                "Customer not found with id: 99",
                exception.getMessage());
    }
}