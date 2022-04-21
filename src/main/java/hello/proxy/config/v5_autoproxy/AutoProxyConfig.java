package hello.proxy.config.v5_autoproxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 중요: 포인트컷은 2가지에 사용된다.
 *
 * 1. 프록시 적용 여부 판단 - 생성 단계
 * 자동 프록시 생성기는 포인트컷을 사용해서 해당 빈이 프록시를 생성할 필요가 있는지 없는지 체크한다.
 * 클래스 + 메서드 조건을 모두 비교한다. 이때 모든 메서드를 체크하는데, 포인트컷 조건에 하나하나 매칭해본다. 만약 조건에 맞는 것이 하나라도 있으면 프록시를 생성한다.
 * 예) orderControllerV1 은 request() , noLog() 가 있다. 여기에서 request() 가 조건에 만족하므로 프록시를 생성한다.
 * 만약 조건에 맞는 것이 하나도 없으면 프록시를 생성할 필요가 없으므로 프록시를 생성하지 않는다.
 *
 * 2. 어드바이스 적용 여부 판단 - 사용 단계
 * 프록시가 호출되었을 때 부가 기능인 어드바이스를 적용할지 말지 포인트컷을 보고 판단한다. 앞서 설명한 예에서 orderControllerV1 은 이미 프록시가 걸려있다.
 * orderControllerV1 의 request() 는 현재 포인트컷 조건에 만족하므로 프록시는 어드바이스를 먼저 호출하고, target 을 호출한다.
 * orderControllerV1 의 noLog() 는 현재 포인트컷 조건에 만족하지 않으므로 어드바이스를 호출하지 않고 바로 target 만 호출한다.
 *
 * 참고: 프록시를 모든 곳에 생성하는 것은 비용 낭비이다. 꼭 필요한 곳에 최소한의 프록시를 적용해야 한다.
 * 그래서 자동 프록시 생성기는 모든 스프링 빈에 프록시를 적용하는 것이 아니라 포인트컷으로 한번 필터링해서 어드바이스가 사용될 가능성이 있는 곳에만 프록시를 생성한다.
 */
@Configuration
@Import({AppV1Config.class, AppV2Config.class})
public class AutoProxyConfig {

    // BeanPostProcessor라는 자동 프록시 생성기를 이미 스프링이 빈에 자동으로 등록을 해두기 때문에, 여기에 적용할 advisor(pointcut + advice)만 등록해주면 됨!
//    @Bean
    public Advisor advisor1(LogTrace logTrace) {
        // pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");

        // advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

//    @Bean
    public Advisor advisor2(LogTrace logTrace) {
        // pointcut
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* hello.proxy.app..*(..))"); // 여기 위치에 있어야 프록시 적용대상이 되는 것

        // advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }

    // 3번으로 갈수록 발전된 예시
    @Bean
    public Advisor advisor3(LogTrace logTrace) {
        // pointcut
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // hello.proxy.app 패키지와 하위 패키지의 모든 메서드는 포인트컷의 매칭하되, noLog() 메서드는 제외하라는 뜻이다.
        pointcut.setExpression("execution(* hello.proxy.app..*(..)) && !execution(* hello.proxy.app..noLog(..))");

        // advice
        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);
    }
}


/**
 * 하나의 프록시, 여러 Advisor 적용
 * 예를 들어서 어떤 스프링 빈이 advisor1 , advisor2 가 제공하는 포인트컷의 조건을 모두 만족하면 프록시 자동 생성기는 프록시를 몇 개 생성할까?
 * 프록시 자동 생성기는 프록시를 하나만 생성한다. 왜냐하면 프록시 팩토리가 생성하는 프록시는 내부에 여러 advisor 들을 포함할 수 있기 때문이다.
 * 따라서 프록시를 여러 개 생성해서 비용을 낭비할 이유가 없다.
 *
 * 프록시 자동 생성기 상황별 정리
 * advisor1 의 포인트컷만 만족 프록시1개 생성, 프록시에 advisor1 만 포함
 * advisor1 , advisor2 의 포인트컷을 모두 만족 프록시1개 생성, 프록시에 advisor1 , advisor2 모두 포함
 * advisor1 , advisor2 의 포인트컷을 모두 만족하지 않음 프록시가 생성되지 않음 이후에 설명할 스프링 AOP도 동일한 방식으로 동작한다.
 *
 * 최종 정리
 * => 자동 프록시 생성기인 AnnotationAwareAspectJAutoProxyCreator 덕분에 개발자는 매우 편리하게 프록시를 적용할 수 있다.
 * 이제 Advisor 만 스프링 빈으로 등록하면 된다. (Advisor = Pointcut + Advice)
 * 추후 @Aspect 애노테이션을 사용해서 더 편리하게 포인트컷과 어드바이스를 만들고 프록시를 적용하는 방법에 대해서도 다뤄보자.
 */