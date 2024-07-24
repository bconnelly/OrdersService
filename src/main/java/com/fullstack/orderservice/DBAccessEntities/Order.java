package com.fullstack.orderservice.DBAccessEntities;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.persistence.Table;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "native")
    private Integer id;
    private String firstName;
    private String dish;
    private Integer tableNumber;
    private Float bill;
    private Boolean served = false;

    public String toString(){
        return "[first_name: " + firstName +
                ", dish: " + dish +
                ", table_number: " + tableNumber +
                ", bill: " + bill +
                ", served: " + served + "]";
    }

    public boolean equals(Order order){
        return (order.firstName.equals(this.firstName) &&
                order.dish.equals(this.dish) &&
                order.tableNumber.equals(this.tableNumber) &&
                order.bill.equals(this.bill) &&
                order.served.equals(this.served));
    }
}
