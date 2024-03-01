package com.fullstack.orderservice;

import com.fullstack.orderservice.DBAccessEntities.Order;
import com.fullstack.orderservice.DomainLogic.OrderLogic;
import com.fullstack.orderservice.Utilities.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping(path = "/getAllOrders")
	public List<Order> getAllOrders(){
		return orderLogic.getAllOrders();
	}

	@PostMapping(path = "/insertOrder")
	public Order insertOrder(@RequestParam(value = "firstName") String firstName,
											 @RequestParam(value = "tableNumber")Integer tableNumber,
											 @RequestParam(value = "dish") String dish,
											 @RequestParam(value = "bill") Float bill){

		Order order = Order.builder()
				.firstName(firstName)
				.tableNumber(tableNumber)
				.dish(dish)
				.bill(bill)
				.build();

		return orderLogic.insertOrder(order);
	}

	@GetMapping("/getOrdersByFirstName")
	public Order getOrdersByFirstName(@RequestParam(value = "firstName") String firstName) throws EntityNotFoundException {
		return orderLogic.getOrderByFirstName(firstName);
	}
}