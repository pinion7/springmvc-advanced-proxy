package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

@Slf4j
public class JdkDynamicProxyTest {

    @Test
    void dynamicA() {
        AInterface target = new AImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        // 첫번째 인자: 어떤 클래스를 로딩할지, 두번째 인자: 어떤 인터페이스를 기반으로 만들어지는 프록시인지, 세번째 인자: 그 프록시가 사용해야될 로직은 무엇인지
        AInterface proxy = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);

        // 먼저 프록시를 호출하고, 그 프록시가 내부에 잇는 타겟을 호출하는 원리인 것!
        // 즉, 프록시의 handler안에 있는 invoke 메서드가 우선 실행이 되고, invoke 메서드는 하나의 실행용 method를 파라미터로 받는데,
        // 그 타깃 실행용 method를 활용하여 target 클래스를 호출하게 됨 (-> method.invoke(target, args)가 해당 로직)
        proxy.call();
        log.info("targetClass={}", target.getClass()); // targetClass=class hello.proxy.jdkdynamic.code.AImpl -> 우리가 만든 AImpl임
        log.info("proxyClass={}", proxy.getClass()); // proxyClass=class com.sun.proxy.$Proxy9 -> AInterface를 impl해서 만들어진 프록시
    }

    @Test
    void dynamicB() {
        BInterface target = new BImpl();
        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        BInterface proxy = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(), new Class[]{BInterface.class}, handler);

        proxy.call(); // 다시 말하지만 해당 로직을 통해 먼저 프록시를 호출하고, 그 프록시가 내부에 잇는 타겟을 호출하는 원리인 것!
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }

}
