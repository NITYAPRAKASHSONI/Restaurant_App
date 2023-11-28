package com.restaurant.dao;

import com.restaurant.pojo.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BillDao extends JpaRepository<Bill,Integer> {
    List<Bill> getAllBills();

    List<Bill> getBillByUserName(@Param("username") String username);


}