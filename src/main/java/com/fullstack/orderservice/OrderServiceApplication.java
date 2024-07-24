package com.fullstack.orderservice;

import com.fullstack.orderservice.DBAccessEntities.Order;
import com.fullstack.orderservice.DomainLogic.OrderLogic;
import com.fullstack.orderservice.Utilities.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@EnableTransactionManagement
@RestController
@SpringBootApplication(scanBasePackages = "com.fullstack.orderservice")
public class OrderServiceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Autowired
	private OrderLogic orderLogic;

	@GetMapping("/getAllOrders")
	public List<Order> getAllOrders() throws EntityNotFoundException {
		return orderLogic.getAllOrders();
	}

	@GetMapping("/getPendingOrders")
	public List<Order> getPendingOrders() throws EntityNotFoundException{
		return orderLogic.getPendingOrders();
	}

	@PostMapping("/insertOrder")
	public Order insertOrder(@RequestBody Order order){
		return orderLogic.insertOrder(order);
	}

	@PostMapping(value = "/serveOrder")
	public void serveOrder(String firstName, int tableNumber) throws EntityNotFoundException {
		orderLogic.serveOrder(firstName, tableNumber);
	}

	@PostMapping(value = "/serveOrder/{id}")
	public void serveOrder(@PathVariable("id") int id) throws EntityNotFoundException {
		orderLogic.serveOrder(id);
	}

	@GetMapping("/getOrdersByFirstName")
	public List<Order> getOrdersByFirstName(String firstName) throws EntityNotFoundException {
		return orderLogic.getOrderByFirstName(firstName);
	}

	@GetMapping("/getOrdersById")
	public Order getOrdersById(int id) throws EntityNotFoundException {
		return orderLogic.getOrderById(id);
	}
}