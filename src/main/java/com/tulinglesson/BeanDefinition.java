package com.tulinglesson;

import lombok.Data;

/**
 * @author bahsk
 * @createTime 2022-02-19 23:17
 * @description Bean的描述
 * @program: lightframework
 */
@Data
public class BeanDefinition {

    private Class type;
    private String scope;
    private boolean isLazy;
}
