package com.tulinglesson;

import com.tulinglesson.annotation.Autowired;
import com.tulinglesson.annotation.Componet;
import com.tulinglesson.annotation.ComponetScan;
import com.tulinglesson.annotation.Scope;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bahsk
 * @createTime 2022-02-10 0:22
 * @description
 * @program: lightframework
 */
public class YqyAnnotationConfigApplicationContext {

    private Class configClass;

    private Map<String ,BeanDefinition> beanDefinitionMap = new HashMap<>();

    //单例池
    private Map<String ,Object> singletonObjects = new HashMap<>();


    // effective java 中推荐使用依赖注入而不是final static 来修饰成员变量
    public YqyAnnotationConfigApplicationContext(Class clazz)  {

        this.configClass = clazz;
        //扫描
        scan(configClass);

        //遍历map
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getScope().equals("singleton")) {
                // TODO 创建单例对象
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName,bean);
            }

        }
    }

     /**
      * @author: bahsk
      * @date: 2022/2/19 23:39
      * @description: 创建bean,TODO 依赖注入
      * @params:
      * @return:
      */
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        //TODO 创建bean
        Class clazz = beanDefinition.getType();
        //简化处理，直接获取无参构造来进行创建
        Object instance = null;
        try {
            instance = clazz.getConstructor().newInstance();

            //获取成员变量,是否存在依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {

                //判断是否有Autowired修饰
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    //拿到类，去看它的component注解有没有定义好的beanName,没有就用默认的
                    declaredField.setAccessible(true);


                    //去bendefinitionMap中拿beandefinition,看是单例还是原型，如果是单例就从单例池拿，否则就new
                    Class<?> fieldType = declaredField.getType();
                    if (fieldType.isAnnotationPresent(Componet.class)) {
                        String fieldBeanName = fieldType.getAnnotation(Componet.class).value();
                        if("".equals(fieldBeanName)) {
                            fieldBeanName = Introspector.decapitalize(fieldType.getSimpleName());
                        }
                        BeanDefinition fieldBeanDefinition = beanDefinitionMap.get(fieldBeanName);
                        if ("prototype".equals(fieldBeanDefinition.getScope())) {
                            //多例
                            Object fieldInstance = fieldType.getConstructor().newInstance();
                            declaredField.set(instance,fieldInstance);

                        } else {
                            declaredField.set(instance,singletonObjects.get(fieldBeanName));

                        }

                    }


                }
            }


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return instance;


    }

     /**
      * @author: bahsk
      * @date: 2022/2/20 19:39
      * @description: 扫描配置类，为beanDefinitionMap添加beanDefinition
      * @params:
      * @return:
      */
    private void scan(Class clazz) {
        if(configClass.isAnnotationPresent(ComponetScan.class)) {
            ComponetScan annotation = (ComponetScan) configClass.getAnnotation(ComponetScan.class);
            //1.读取配置类中要扫描的包
            //获取需要扫描包的源码路径
            String value = annotation.value();
            System.out.println(value);

            // .格式无法找到目录得进行格式转换
            String path = value.replace(".","/");

            //2.***利用类加载器.获取target底下的class对应的目录
            ClassLoader classLoader = YqyAnnotationConfigApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);

            //遍历service底下的文件和文件夹
            File file = new File(resource.getFile());

            if(file.isDirectory()) {
                for (File listFile : file.listFiles()) {
                    File absoluteFile = listFile.getAbsoluteFile();
//                    System.out.println(absoluteFile);
                    //判断扫描出的类是否有component注解,需要用类加载器装载我们找到的目录
                    //3. 目录装载只装载com.tulinglesson.service.UserService这种
                    //lastIndexOf 含头不含尾
                    //String filePath = absolutePath.substring(absolutePath.lastIndexOf("com"), absolutePath.lastIndexOf(".class"));
                    // com\tulinglesson\service\OrderService
                    String absolutePath = absoluteFile.getAbsolutePath();
                    String filePath = absolutePath.substring(absolutePath.lastIndexOf("com"), absolutePath.lastIndexOf(".class")).replace("\\",".");
//                    System.out.println(filePath);
                    Class<?> aClass = null;
                    try {
                        aClass = classLoader.loadClass(filePath);
                        //判断类是否含有Component注解
                        if (aClass.isAnnotationPresent(Componet.class)) {

                            //4. aClass  ： 获取到的bean
                            // 需要根据单例还是原型进行一个判断处理，原型bean不是在初始化的时候创建，
                            // 而是每次getBean都去创建一个新的bean
                            String beanName = aClass.getAnnotation(Componet.class).value();

                            //如果beanName为空就生成一个默认的
                            if("".equals(beanName)) {
                                beanName = Introspector.decapitalize(aClass.getSimpleName());
                            }

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(aClass);


                            //5.判断是单例还是多例?
                            if (aClass.isAnnotationPresent(Scope.class)) {
                                Scope scope = aClass.getAnnotation(Scope.class);
                                String beanType = scope.value();
                                //TODO 有注解可以是单例也可以是多例
                                beanDefinition.setScope(beanType);
                            } else {
                                //TODO 没有注解就是单例
                                beanDefinition.setScope("singleton");
                            }
                            //6.添加到beanDefinitionMap
                            beanDefinitionMap.put(beanName,beanDefinition);




                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }



        }
    }

    public YqyAnnotationConfigApplicationContext() {

    }


    public Object getBean(String beanName) {
        //TODO beanName --> Class
        //判断beanName是否存在于map中
        if (!beanDefinitionMap.containsKey(beanName)) {
            throw new NullPointerException();
        }
        //获取beanDefinition
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        //判断是单例还是原型
        if (beanDefinition.getScope().equals("singleton")) {
            return singletonObjects.get(beanName);
        } else {
            //原型 --> 每次都要创建一个bean
            Object prototypeBean = createBean(beanName, beanDefinition);
            return prototypeBean;

        }


    }
}
