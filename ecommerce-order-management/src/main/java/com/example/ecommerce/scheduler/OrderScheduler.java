package com.example.ecommerce.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.OrderService;

@Component
public class OrderScheduler {

    private final OrderRepository orderRepository;

    private final OrderService orderService;

    public OrderScheduler(
            OrderRepository orderRepository,
            OrderService orderService) {

        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 100000)
    public void checkPendingOrders() {

        LocalDateTime cutoffTime =
                LocalDateTime.now().minusMinutes(30);

        List<Order> orders =
                orderRepository
                        .findByStatusAndOrderDateBefore(
                                OrderStatus.PLACED,
                                cutoffTime);

        for (Order order : orders) {

            orderService.cancelOrderAutomatically(
                    order.getId());

            System.out.println(
                    "Order automatically cancelled: "
                            + order.getId());
        }
    }
}