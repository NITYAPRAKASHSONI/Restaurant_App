package com.restaurant.jwt;

import com.restaurant.dao.UserDao;
import com.restaurant.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Objects;
@Slf4j
@Service
public class CustomerUsersDetailsService implements UserDetailsService {

    @Autowired
   private UserDao userDao;


    private User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("I am inside loadUserByUsername {}",username);
        userDetail = userDao.findByEmailId(username);
        if (!Objects.isNull(userDetail)){
            return new org.springframework.security.core.userdetails.User(userDetail.getEmail(), userDetail.getPassword(),new ArrayList<>());
        }else{
            throw new UsernameNotFoundException("User Not Found");
        }
    }


    public User getUserDetail(){
        return userDetail;
    }


}
