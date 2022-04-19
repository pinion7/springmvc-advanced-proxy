package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// 사실 컨트롤러는 인터페이스로 잘 안만들지만 가능은 하다는 것 보여주는 예시
// @Controller 또는 @RequestMapping의 차이: @Controller는 @Component가 있고, @RequestMapping에는 없음 (@Component가 있으면 스프링 빈에 자동 등록 됨)
@RequestMapping // 스프링은 @Controller 또는 @RequestMapping이 있으면 스프링 컨트롤러 인식 (둘중하나만 있어도 인식 가능!)
@ResponseBody // HTTP 메시지 컨버터를 사용해서 응답한다. 이 애노테이션은 인터페이스에 사용해도 됨
public interface OrderControllerV1 {

    /**
     * 구현체에서는 사실 괄호안의 "itemId"를 빼거나, @RequestParam을 생략해도 있는 것처럼 인식 해주지만,
     * 인터페이스에는 애노테이션 및 키워드를 모두 명확히 명시해줘야 함. -> 안그러면 컴파일 시점에 잘 인식이 안됨
     */
    @GetMapping("/v1/request") // LogTrace를 적용할 대상
    String request(@RequestParam("itemId") String itemId);

    @GetMapping("/v1/no-log") // LogTrace를 적용하지 않을 대상
    String noLog();
}
