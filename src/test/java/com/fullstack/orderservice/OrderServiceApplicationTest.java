package com.fullstack.orderservice;

import com.fullstack.orderservice.DBAccessEntities.Order;
import com.fullstack.orderservice.DomainLogic.OrderLogic;
import com.fullstack.orderservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceApplicationTest {

    @InjectMocks
    private OrderServiceApplication application;

    @Mock
    private OrderLogic orderLogicMock;

    @Test
    void testGetAllOrders() throws EntityNotFoundException {
        List<Order> expected = new ArrayList<>();
        expected.add(Order.builder().firstName("alice").tableNumber(1).dish("burg").bill(5.99f).build());
        expected.add(Order.builder().firstName("bob").tableNumber(1).dish("cheeseburg").bill(6.99f).build());
        when(orderLogicMock.getAllOrders()).thenReturn(expected);

        List<Order> response = application.getAllOrders();

        assertEquals(expected, response);
        verify(orderLogicMock, times(1)).getAllOrders();
    }

    @Test
    void testGetOrdersByFirstName() throws EntityNotFoundException {
        List<Order> expected = new ArrayList<>();
        expected.add(Order.builder().firstName("dave").tableNumber(1).dish("burg").bill(5.99f).build());
        expected.add(Order.builder().firstName("dave").tableNumber(1).dish("cheeseburg").bill(6.99f).build());
        when(orderLogicMock.getOrderByFirstName("dave")).thenReturn(expected);

        List<Order> response = application.getOrdersByFirstName("dave");

        assertEquals(expected, response);
        verify(orderLogicMock, times(1)).getOrderByFirstName("dave");


    }

    @Test
    void testGetPendingOrders() throws EntityNotFoundException {
        List<Order> expected = new ArrayList<>();
        expected.add(Order.builder().firstName("alice").tableNumber(1).dish("burg").bill(5.99f).build());
        expected.add(Order.builder().firstName("bob").tableNumber(1).dish("cheeseburg").bill(6.99f).build());
        when(orderLogicMock.getPendingOrders()).thenReturn(expected);

        List<Order> response = application.getPendingOrders();

        assertEquals(expected, response);
        verify(orderLogicMock, times(1)).getPendingOrders();
    }

    @Test
    void testInsertOrder() {
        Order toInsert = Order.builder().firstName("charlie").tableNumber(1).dish("burg").bill(5.99f).build();
        when(orderLogicMock.insertOrder(toInsert)).thenReturn(toInsert);

        Order response = application.insertOrder(toInsert);

        assertEquals(toInsert, response);
        verify(orderLogicMock, times(1)).insertOrder(toInsert);
    }

    @Test
    void testServeOrder() throws EntityNotFoundException {
        doNothing().when(orderLogicMock).serveOrder("dave", 1);

        application.serveOrder("dave", 1);

        verify(orderLogicMock, times(1)).serveOrder("dave", 1);
    }
}