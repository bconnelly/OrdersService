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
    void getAllOrdersTest() throws EntityNotFoundException {
        List<Order> orders = orderLogic.getAllOrders();
        System.out.println(orders.toString());

        assert(orders.size() == 9);
        assert(orders.get(0).getFirstName().equals("alice"));
        assert(orders.get(1).getFirstName().equals("bob"));
        assert(orders.get(2).getFirstName().equals("chuck"));
        assert(orders.get(3).getFirstName().equals("dave"));
        assert(orders.get(4).getFirstName().equals("ed"));
        assert(orders.get(5).getFirstName().equals("ed"));
        assert(orders.get(6).getFirstName().equals("fred"));
        assert(orders.get(7).getFirstName().equals("george"));
        assert(orders.get(8).getFirstName().equals("harry"));
    }

    @Test
    void getPendingOrdersTest() throws EntityNotFoundException {
        List<Order> pendingOrders = orderLogic.getPendingOrders();
        assert(pendingOrders.size() == 5);
    }

    @Test
    void getOrderByFirstNameTest() throws EntityNotFoundException {
        Order expectedOrder = Order.builder().firstName("alice").dish("coke").bill(2.01f).tableNumber(1).served(false).build();

        List<Order> returnedOrder = orderLogic.getOrderByFirstName("alice");
        assert(returnedOrder.size() == 1);
        assert(expectedOrder.equals(returnedOrder.get(0)));

        assertThrows(EntityNotFoundException.class, () -> orderLogic.getOrderByFirstName("zach"));
    }

    @Test
    void getOrderByIdTest() throws EntityNotFoundException {
        Order expectedOrder = Order.builder().firstName("bob").dish("pizza").tableNumber(2).bill(5.12f).served(false).build();

        Order returnedOrder = orderLogic.getOrderById(2);
        assert(returnedOrder.equals(expectedOrder));

        assertThrows(EntityNotFoundException.class, () -> orderLogic.getOrderById(-1));
    }

    @Test
    void insertOrdersTest(){
        Order newOrder = Order.builder()
                .firstName("hank")
                .tableNumber(1)
                .bill(12.34f)
                .dish("some food")
                .served(false).build();
        Order submittedOrder = orderLogic.insertOrder(orderLogic.insertOrder(newOrder));
        assert(newOrder.equals(submittedOrder));
    }

    @Test
    void serveOrderNameTableTest() throws EntityNotFoundException {
        List<Order> orderToServe = orderLogic.getOrderByFirstName("alice");
        assert(orderToServe.size() == 1);
        assert(orderToServe.get(0).getServed().equals(false));

        orderLogic.serveOrder("alice", 1);
        List<Order> orderServed = orderLogic.getOrderByFirstName("alice");
        assert(orderServed.size() == 1);
        assert(orderServed.get(0).getServed().equals(true));
    }

    @Test
    void serveOrderNameTableTestBadOrder() throws EntityNotFoundException {
        orderLogic.serveOrder("fake", 1);
    }

    @Test
    void serveOrderIdTest() throws EntityNotFoundException {
        Order orderToServe = orderLogic.getOrderById(1);
        assert(orderToServe.getServed().equals(false));

        orderLogic.serveOrder("alice", 1);
        Order orderServed = orderLogic.getOrderById(1);
        assert(orderServed.getServed().equals(true));
    }
}