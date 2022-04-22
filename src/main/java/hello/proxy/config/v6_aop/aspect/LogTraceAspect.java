package hello.proxy.config.v6_aop.aspect;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.Setter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 자동 프록시 생성기 ( AnnotationAwareAspectJAutoProxyCreator )는 Advisor 를 자동으로 찾아와서 필요한 곳에 프록시를 생성하고 적용해준다.
 * 자동 프록시 생성기는 여기에 추가로 하나의 역할을 더 하는데, 바로 @Aspect 를 찾아서 이것을 Advisor 로 만들어준다.
 * 쉽게 이야기해서 지금까지 학습한 기능에 @Aspect 를 Advisor 로 변환해서 저장하는 기능도 한다.
 * 그래서 이름 앞에 AnnotationAware (애노테이션을 인식하는)가 붙어 있는 것이다.
 *
 * 즉, 자동 프록시 생성기는 2가지 일을 한다.
 * 1. @Aspect 를 보고 어드바이저( Advisor )로 변환해서 저장한다.
 * 2. 어드바이저를 기반으로 프록시를 생성한다.
 *
 * @Aspect를 어드바이저로 변환해서 저장하는 과정을 알아보자
 * 1. 실행: 스프링 애플리케이션 로딩 시점에 자동 프록시 생성기를 호출한다.
 * 2. 모든 @Aspect 빈 조회: 자동 프록시 생성기는 스프링 컨테이너에서 @Aspect 애노테이션이 붙은 스프링 빈을 모두 조회한다.
 * 3. 어드바이저 생성: @Aspect 어드바이저 빌더를 통해 @Aspect 애노테이션 정보를 기반으로 어드바이저를 생성한다.
 * 4. @Aspect 기반 어드바이저 저장: 생성한 어드바이저(포인트컷 + 어드바이스)를 @Aspect 어드바이저 빌더 내부에 저장한다.
 */
@Setter
@Aspect // 이게 중요. 스프링은 @Aspect 애노테이션으로 매우 편리하게 포인트컷과 어드바이스로 구성되어 있는 어드바이저 생성 기능을 지원
public class LogTraceAspect {

    private final LogTrace logTrace;

    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    // 1. @Around
    // @Around 의 값에 포인트컷 표현식을 넣는다. 표현식은 AspectJ 표현식을 사용한다.
    // @Around 의 메서드, 즉 execute 로직이 어드바이스( Advice )가 된다.
    // 2. ProceedingJoinPoint
    // 어드바이스에서 살펴본 MethodInvocation invocation 과 유사한 기능이다.
    // 내부에 실제 호출 대상, 전달 인자, 그리고 어떤 객체와 어떤 메서드가 호출되었는지 정보가 포함되어 있다.
    // 3. joinPoint.proceed()
    // 실제 호출 대상( target )을 호출한다.
    @Around("execution(* hello.proxy.app..*(..))") // 포인트컷 부분
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable { // 어드바이스 부분
        TraceStatus status = null;
        try {
//            Method method = invocation.getMethod();
//            String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
            // 위 로직을 한줄의 코드로 작성. 같은 형태의 message를 뽑아낼 수 있음!
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            // target 호출
            Object result = joinPoint.proceed();
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }

    // 만약 아래 어노테이션을 붙여서 메서드를 만들면 어드바이저가 하나 더 만들어지는 거임
//   @Around()
}
