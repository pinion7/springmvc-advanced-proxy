package hello.proxy.postprocessor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class BasicTest {

    @Test
    void basicConfig() {
        // A를 빈으로 등록하기
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BasicConfig.class);

        // 등록된 빈 A 조회하기
        A a = applicationContext.getBean("beanA", A.class);
        a.helloA();

        // 등록되지 않은 빈 B 조회하기 (B가 등록되지 않았다는 사실 검증하기 -> 등록되지 않았으므로 예외터짐!)
        assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(B.class));
    }

    static class BasicConfig {
        @Bean(name = "beanA")
        public A a() {
            return new A();
        }
    }

    static class A {
        public void helloA() {
            log.info("hello A");
        }
    }

    static class B {
        public void helloB() {
            log.info("hello B");
        }
    }


}
