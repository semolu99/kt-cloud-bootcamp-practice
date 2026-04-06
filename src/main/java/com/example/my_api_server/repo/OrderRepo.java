package com.example.my_api_server.repo;

import com.example.my_api_server.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
