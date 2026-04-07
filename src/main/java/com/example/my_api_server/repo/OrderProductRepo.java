package com.example.my_api_server.repo;

import com.example.my_api_server.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepo extends JpaRepository<OrderProduct, Long> {
    public List<OrderProduct> findAllByOrderId(Long orderId);
}
