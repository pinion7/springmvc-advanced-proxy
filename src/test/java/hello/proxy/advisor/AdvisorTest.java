package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

@Slf4j
public class AdvisorTest {

    /** 1. 어드바이저와 이를 구성하는 포인트컷, 어드바이스 이해해보기
     * (1) new DefaultPointcutAdvisor : Advisor 인터페이스의 가장 일반적인 구현체이다.
     *    생성자를 통해 하나의 포인트컷과 하나의 어드바이스를 넣어주면 된다. 어드바이저는 하나의 포인트컷과 하나의 어드바이스로 구성된다.
     * (2) Pointcut.TRUE : 항상 true 를 반환하는 포인트컷이다. 이후에 직접 포인트컷을 구현해볼 것이다.
     * (3) new TimeAdvice() : 앞서 개발한 TimeAdvice 어드바이스를 제공한다.
     * (4) proxyFactory.addAdvisor(advisor) : 프록시 팩토리에 적용할 어드바이저를 지정한다. 어드바이저는 내부에 포인트컷과 어드바이스를 모두 가지고 있다.
     *     따라서 어디에 어떤 부가 기능을 적용해야 할지 어드바이스 하나로 알 수 있다. 프록시 팩토리를 사용할 때 어드바이저는 필수이다.
     *     그런데 생각해보면 이전에 분명히 proxyFactory.addAdvice(new TimeAdvice()) 이렇게 어드바이저가 아니라 어드바이스를 바로 적용했다.
     *     이것은 단순히 편의 메서드이고 결과적으로 해당 메서드 내부에서 지금 코드와 마찬가지로 아래의 어드바이저가 생성된다.
     *     => DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice())
     */
    @Test
    @DisplayName("포인트 컷 + 어드바이스 = 어드바이저: 적용 과정 이해하기")
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    /** 2. 직접 커스텀한 포인트컷 사용해보기
     * (1) static class Mypointcut
     * (2) static class MyMethodMatcher
     */
    @Test
    @DisplayName("2. 직접 만든 포인트컷")
    void advisorTest2() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new Mypointcut(), new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    /** 3. 실무에서 사용하는 것들에 대한 소개 및 테스트
     * (1) NameMatchMethodPointcut : 메서드 이름을 기반으로 매칭한다. 내부에서는 PatternMatchUtils 를 사용한다. 예) *xxx* 허용
     * (2) JdkRegexpMethodPointcut : JDK 정규 표현식을 기반으로 포인트컷을 매칭한다.
     * (3) TruePointcut : 항상 참을 반환한다.
     * (4) AnnotationMatchingPointcut : 애노테이션으로 매칭한다.
     * (5) AspectJExpressionPointcut : aspectJ 표현식으로 매칭한다.
     *     가장 중요한 것은 aspectJ 표현식 (사실 다른 것은 중요하지 않다.)
     *     실무에서는 사용하기도 편리하고 기능도 가장 많은 aspectJ 표현식을 기반으로 사용하는 AspectJExpressionPointcut 을 사용하게 된다.
     *     aspectJ 표현식과 사용방법은 중요해서 이후 AOP를 설명할 때 자세히 설명하겠다.
     *     지금은 Pointcut 의 동작 방식과 전체 구조에 집중하자.
     */
    @Test
    @DisplayName("3. 스프링이 제공하는 포인트컷")
    void advisorTest3() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("save");
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }


    // 2번 테스트에서 활용할 직접 만든 포인트컷 객체
    static class Mypointcut implements Pointcut {

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }

    // 2번 테스트에서 활용할 직접 만든 포인트컷의 getMethodMatcher메서드의 반환값에 해당하는 MyMethodMatcher 객체
    static class MyMethodMatcher implements MethodMatcher {

        private String matchName = "save";

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            boolean result = method.getName().equals(matchName);
            log.info("포인트컷 호출 method={} targetClass={}", method, targetClass);
            log.info("포인트컷 결과 result={}", result);
            return result;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }

}
