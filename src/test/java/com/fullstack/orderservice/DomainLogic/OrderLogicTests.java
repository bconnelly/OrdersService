package com.fullstack.orderservice.DomainLogic;

import com.fullstack.orderservice.DBAccessEntities.Order;
import com.fullstack.orderservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
public class OrderLogicTests {

    @Autowired
    private OrderLogic orderLogic;

    @Test
    void getAllOrdersTest(){
        List<Order> orders = orderLogic.getAllOrders();

        assert(orders.size() == 8);
        assert(orders.get(0).getFirstName().equals("alice"));
        assert(orders.get(1).getFirstName().equals("bob"));
        assert(orders.get(2).getFirstName().equals("dan"));
        assert(orders.get(3).getFirstName().equals("chuck"));
        assert(orders.get(4).getFirstName().equals("ed"));
        assert(orders.get(5).getFirstName().equals("ed"));
        assert(orders.get(6).getFirstName().equals("fred"));
        assert(orders.get(7).getFirstName().equals("george"));
    }

    @Test
    void insertOrdersTest(){
        Order newOrder = Order.builder()
                .firstName("test")
                .tableNumber(1)
                .bill(12.34f)
                .dish("some food").build();
        Order submittedOrder = orderLogic.insertOrder(orderLogic.insertOrder(newOrder));
        assert(newOrder.equals(submittedOrder));
    }

    @Test
    void getOrderByFirstName() throws EntityNotFoundException {
        Order expectedOrder = Order.builder().firstName("alice").dish("coke").bill(2.01f).tableNumber(1).build();

        Order returnedOrder = orderLogic.getOrderByFirstName("alice");
        assert(expectedOrder.equals(returnedOrder));

        assertThrows(EntityNotFoundException.class, () -> orderLogic.getOrderByFirstName("zach"));
    }
}
//