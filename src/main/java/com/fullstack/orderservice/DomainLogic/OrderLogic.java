package com.fullstack.orderservice.DomainLogic;

import com.fullstack.orderservice.DBAccessEntities.Order;
import com.fullstack.orderservice.Repositories.OrderRepository;
import com.fullstack.orderservice.Utilities.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderLogic {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders() throws EntityNotFoundException {
        List<Order> orders = orderRepository.findAll();
        if(orders.isEmpty()) throw new EntityNotFoundException("no orders");
        return orders;
    }

    public List<Order> getPendingOrders() throws EntityNotFoundException {
        List<Order> orders = orderRepository.findByServedFalse();
        if(orders.isEmpty()) throw new EntityNotFoundException("no pending orders");
        return orders;
    }

    public Order insertOrder(Order order){
        return orderRepository.save(order);
    }

    public Order getOrderByFirstName(String firstName) throws EntityNotFoundException {
        Order returnedOrder = orderRepository.findByFirstName(firstName);
        if(returnedOrder == null) throw new EntityNotFoundException("order for customer " + firstName + " not found");
        return returnedOrder;
    }

}