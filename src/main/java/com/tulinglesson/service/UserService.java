package com.tulinglesson.service;

import com.tulinglesson.annotation.Autowired;
import com.tulinglesson.annotation.Componet;
import com.tulinglesson.annotation.Scope;

/**
 * @author bahsk
 * @createTime 2022-02-19 22:27
 * @description
 * @program: lightframework
 */
@Componet("userService")
@Scope("prototype")
//@Scope("singleton")
public class UserService {

    @Autowired
    private OrderService orderService;

    public void test() {
        System.out.println(orderService);
    }
}
