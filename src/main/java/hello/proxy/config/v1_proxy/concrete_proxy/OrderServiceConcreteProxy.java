package hello.proxy.config.v1_proxy.concrete_proxy;

import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

// 구체 기반 프록시 구현할때의 단점 (자바 문법 특성상 super로 부모 클래스의 생성자를 어쩔 수 없이 호출해야함!)
// 이 부분을 생략하면 기본 생성자가 호출됨 (그런데 부모클래스인 OrderService2에는 기본생성자(파라미터를 안받는 생성자: default 생성자)가 없음)
// 따라서 super에 파라미터를 넣어 호출해야 한다. (+ super 호출때문에 @RequiredArgsConstructor도 못씀)
public class OrderServiceConcreteProxy extends OrderServiceV2 {

    private final OrderServiceV2 target;
    private final LogTrace logTrace;

    public OrderServiceConcreteProxy(OrderServiceV2 target, LogTrace logTrace) {
        super(null); // 단, 프록시로 부모클래스의 기능을 사용하지는 않을 것이기 때문에 null로 세팅 (인터페이스 기반 프록시는 이런 고민이 필요없음!)
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public void orderItem(String itemId) {

        TraceStatus status = null;
        try {
            status = logTrace.begin("OrderService.orderItem()");

            // target 호출
            target.orderItem(itemId);
            logTrace.end(status);
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
