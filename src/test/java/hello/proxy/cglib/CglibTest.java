package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

@Slf4j
public class CglibTest {

    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();

        Enhancer enhancer = new Enhancer(); // Enhancer가 cglib를 만드는 객체임!
        enhancer.setSuperclass(ConcreteService.class); // 프록시의 부모타입을 지정해주는 작업
        enhancer.setCallback(new TimeMethodInterceptor(target));
        ConcreteService proxy = (ConcreteService) enhancer.create(); // 부모타입을 ConcreteService로 지정했기 때문에 다운캐스팅해도 괜찮음
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        // TimeMethodInterceptor의 intercept가 인터셉트 기능을 하는 거임 그래서 그거먼저 호출되고,
        // 그다음에 intercept 메서드 안에 서 target이 invoke로 호출되서 실행되고
        // 마지막으로 TimeMethodInterceptor 종료하면서 시간 체크!
        proxy.call();
    }

}
