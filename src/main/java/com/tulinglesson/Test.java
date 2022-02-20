package com.tulinglesson;

import com.tulinglesson.service.OrderService;
import com.tulinglesson.service.UserService;

/**
 * @author bahsk
 * @createTime 2022-02-10 0:24
 * @description
 * @program: lightframework
 */
public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        YqyAnnotationConfigApplicationContext yqyAnnotationConfigApplicationContext = new YqyAnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = (UserService) yqyAnnotationConfigApplicationContext.getBean("userService");

        //反复输出验证单例
        System.out.println((UserService) yqyAnnotationConfigApplicationContext.getBean("userService"));
        System.out.println((UserService) yqyAnnotationConfigApplicationContext.getBean("userService"));
        System.out.println((OrderService) yqyAnnotationConfigApplicationContext.getBean("orderService"));
        userService.test();
//
//        userService.test();
    }
}
