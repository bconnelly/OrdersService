package com.fullstack.orderservice.Repositories;

import com.fullstack.orderservice.DBAccessEntities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @NonNull
    List<Order> findAll();
    List<Order> findByFirstName(String firstName);
    Order findOrderById(Integer id);
    List<Order> findByServed(boolean ifServed);

    @Transactional
    @Modifying
    @Query("update Order o set o.served = true where o.firstName = ?1 and o.tableNumber = ?2")
    int serveOrder(String firstName, int tableNumber);

    @Transactional
    @Modifying
    @Query("update Order o set o.served = true where o.id = ?1")
    int serveOrder(int id);
}
