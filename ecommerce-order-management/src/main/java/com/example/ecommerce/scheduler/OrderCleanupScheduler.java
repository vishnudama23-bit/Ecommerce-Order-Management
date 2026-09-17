package com.example.ecommerce.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;

@Component
public class OrderCleanupScheduler {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public OrderCleanupScheduler(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteOldCancelledOrders() {

        LocalDateTime cutoffTime =
        		LocalDateTime.now().minusDays(30);

        List<Order> orders =
                orderRepository
                        .findByStatusAndOrderDateBefore(
                                OrderStatus.CANCELLED,
                                cutoffTime);

        for (Order order : orders) {

            List<OrderItem> orderItems =
                    orderItemRepository
                            .findByOrderId(order.getId());

            orderItemRepository.deleteAll(orderItems);

            paymentRepository.deleteByOrderId(
                    order.getId());

            orderRepository.delete(order);

            System.out.println(
                    "Old cancelled order deleted: "
                    + order.getId());
        }
    }
}