package com.fullstack.orderservice.Repositories;

import com.fullstack.orderservice.DBAccessEntities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @NonNull
    List<Order> findAll();
    Order findByFirstName(String firstName);
    Order findOrderById(Integer id);
    List<Order> findByServedFalse();

}
