package com.homeware.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.homeware.pos.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}