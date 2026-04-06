package com.example.my_api_server.repo;

import com.example.my_api_server.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p Where p.id IN :ids ORDER BY p.id")
    List<Product> findAllByIdsWithXLock(List<Long> ids);

}
