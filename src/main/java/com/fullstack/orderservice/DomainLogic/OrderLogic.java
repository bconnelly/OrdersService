package com.fullstack.orderservice.DomainLogic;

import com.fullstack.orderservice.DBAccessEntities.Order;
import com.fullstack.orderservice.Repositories.OrderRepository;
import com.fullstack.orderservice.Utilities.EntityNotFoundException;
import org.hibernate.NonUniqueResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        List<Order> orders = orderRepository.findByServed(true);
        if(orders.isEmpty()) throw new EntityNotFoundException("no pending orders");
        return orders;
    }

    public Order insertOrder(Order order) throws DataIntegrityViolationException {
        if(order == null) throw new IllegalArgumentException("order is null");
        if(order.getTableNumber() < 1) throw new IllegalArgumentException("order table number is invalid");

        return orderRepository.save(order);


    }

    public void serveOrder(String firstName, int tableNumber) throws EntityNotFoundException {
        if(firstName == null || firstName.isEmpty()) throw new IllegalArgumentException("firstName is null or empty");
        if(tableNumber < 1) throw new IllegalArgumentException("tableNumber is null");

        int recordsChanged = orderRepository.serveOrder(firstName, tableNumber);
        if(recordsChanged == 0) throw new EntityNotFoundException();
    }

    public void serveOrder(int id) throws EntityNotFoundException {
        if(id < 1) throw new IllegalArgumentException("id is invalid");

        int recordsChanged = orderRepository.serveOrder(id);
        if(recordsChanged == 0) throw new EntityNotFoundException();
        else if (recordsChanged > 1) throw new NonUniqueResultException(recordsChanged);
    }

    public List<Order> getOrderByFirstName(String firstName) throws EntityNotFoundException {
        if(firstName == null || firstName.isEmpty()) throw new IllegalArgumentException("firstName is null or empty");

        List<Order> returnedOrder = orderRepository.findByFirstName(firstName);
        if(returnedOrder == null || returnedOrder.isEmpty()) throw new EntityNotFoundException("order for customer " + firstName + " not found");
        else if(returnedOrder.size() > 1) throw new NonUniqueResultException(returnedOrder.size());
        return returnedOrder;
    }

    public Order getOrderById(int id) throws EntityNotFoundException {
        if(id < 1) throw new IllegalArgumentException("id is invalid");

        Order returnedOrder = orderRepository.findOrderById(id);
        if(returnedOrder == null) throw new EntityNotFoundException("order for id " + id + " not found");
        return returnedOrder;
    }
}