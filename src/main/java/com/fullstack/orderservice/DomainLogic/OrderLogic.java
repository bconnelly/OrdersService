package com.fullstack.orderservice.DomainLogic;

import com.fullstack.orderservice.DBAccessEntities.Order;
import com.fullstack.orderservice.Repositories.OrderRepository;
import com.fullstack.orderservice.Utilities.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderLogic {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    public Order insertOrder(Order order){
        return orderRepository.save(order);
    }

    public Order getOrderByFirstName(String firstName) throws EntityNotFoundException {
        Optional<Order> returnedOrder = orderRepository.findByFirstName(firstName);
        if(returnedOrder.isEmpty()) throw new EntityNotFoundException("order for customer " + firstName + " not found");
        return returnedOrder.get();
    }
}