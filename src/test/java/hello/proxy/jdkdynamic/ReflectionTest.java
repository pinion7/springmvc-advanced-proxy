package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class ReflectionTest {

    @Test
    void reflection0() {
        Hello target = new Hello();

        // 공통 로직1 시작
        log.info("start");
        String result1 = target.callA(); // 서로 호출하는 메서드만 다름
        log.info("result1={}", result1);

        // 공통 로직2 시작
        log.info("start");
        String result2 = target.callB();
        log.info("result2={}", result2);
    }

    // 위 테스트처럼 간단히 뽑을 걸 왜 아래같이하냐?
    // 아래 방식을 쓰면 클래스나 메서드 정보를 동적으로 변경할 수 있기 때문임!
    // 기존의 callA() , callB() 메서드를 직접 호출하는 부분이 Method 로 대체된것. -> 덕분에 이제 공통 로직을 만들 수 있게 됨!
    @Test
    void reflection1() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 클래스 메타 정보 획득 (아래에 static으로 정의된 Hello라는 클래스를 뽑을 거임: 내부에 있는 클래스는 아래처럼 앞에 $을 붙여서 접근해야함)
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // callA 메서드 정보 얻기
        Method methodCallA = classHello.getMethod("callA");
        Object result1 = methodCallA.invoke(target); // callA란 메서드를 호출하는데, target 인스턴스에 있는 callA를 호출
        log.info("result1={}", result1);

        // callB 메서드 정보 얻기
        Method methodCallB = classHello.getMethod("callB");
        Object result2 = methodCallB.invoke(target); // callB란 메서드를 호출하는데, target 인스턴스에 있는 callB를 호출
        log.info("result2={}", result2);
    }

    @Test
    void reflection2() throws Exception {
        // 클래스 메타 정보 획득 (아래에 static으로 정의된 Hello라는 클래스를 뽑을 거임: 내부에 있는 클래스는 아래처럼 앞에 $을 붙여서 접근해야함)
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // callA 메서드 정보 얻기
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);

        // callB 메서드 정보 얻기
        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
    }

    private void dynamicCall(Method method, Object target) throws InvocationTargetException, IllegalAccessException {
        log.info("start");
        Object result = method.invoke(target);
        log.info("result={}", result);
    }


    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }

        public String callB() {
            log.info("callB");
            return "B";
        }
    }

}
