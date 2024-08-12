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
        return orderRepository.save(order);
    }

    public void serveOrder(String firstName, int tableNumber) throws EntityNotFoundException {
        int recordsChanged = orderRepository.serveOrder(firstName, tableNumber);
        if(recordsChanged == 0) throw new EntityNotFoundException();
        else if (recordsChanged > 1) throw new NonUniqueResultException(recordsChanged);
    }

    public void serveOrder(int id) throws EntityNotFoundException {
        int recordsChanged = orderRepository.serveOrder(id);
        if(recordsChanged == 0) throw new EntityNotFoundException();
        else if (recordsChanged > 1) throw new NonUniqueResultException(recordsChanged);
    }

    public List<Order> getOrderByFirstName(String firstName) throws EntityNotFoundException {
        List<Order> returnedOrder = orderRepository.findByFirstName(firstName);
        if(returnedOrder == null || returnedOrder.isEmpty()) throw new EntityNotFoundException("order for customer " + firstName + " not found");
        else if(returnedOrder.size() > 1) throw new NonUniqueResultException(returnedOrder.size());
        return returnedOrder;
    }

    public Order getOrderById(int id) throws EntityNotFoundException {
        Order returnedOrder = orderRepository.findOrderById(id);
        if(returnedOrder == null) throw new EntityNotFoundException("order for id " + id + " not found");
        return returnedOrder;
    }
}