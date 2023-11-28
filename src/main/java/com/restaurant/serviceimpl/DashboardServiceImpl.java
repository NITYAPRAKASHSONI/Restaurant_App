package com.restaurant.serviceimpl;

import com.restaurant.dao.BillDao;
import com.restaurant.dao.CategoryDao;
import com.restaurant.dao.ProductDao;
import com.restaurant.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private BillDao billDao;
    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
            Map<String,Object> map=new HashMap<>();
            map.put("category",categoryDao.count());
            map.put("product",productDao.count());
            map.put("bill",billDao.count());
            return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
