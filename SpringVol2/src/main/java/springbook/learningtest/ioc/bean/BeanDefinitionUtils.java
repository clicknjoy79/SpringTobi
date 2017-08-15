package springbook.learningtest.ioc.bean;

import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinitionUtils {
    public static void printBeanDefinitions(GenericApplicationContext gac) {
        List<List<String>> roleBeansInfo = new ArrayList<>();
        roleBeansInfo.add(new ArrayList<>());
        roleBeansInfo.add(new ArrayList<>());
        roleBeansInfo.add(new ArrayList<>());

        for(String name : gac.getBeanDefinitionNames()) {
            int role = gac.getBeanDefinition(name).getRole();
            List<String> beanInfos = roleBeansInfo.get(role);
            beanInfos.add(role + "\t" + name + "\t" + gac.getBean(name).getClass().getName());
        }

        for(List<String> beanInfos : roleBeansInfo) {
            for(String beanInfo : beanInfos) {
                System.out.println(beanInfo);
            }
        }
    }
}
