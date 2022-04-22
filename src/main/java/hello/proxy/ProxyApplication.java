package hello.proxy;

import hello.proxy.config.v3_proxyfactory.ProxyFactoryConfigV1;
import hello.proxy.config.v3_proxyfactory.ProxyFactoryConfigV2;
import hello.proxy.config.v4_postprocessor.BeanPostProcessorConfig;
import hello.proxy.config.v5_autoproxy.AutoProxyConfig;
import hello.proxy.config.v6_aop.AopConfig;
import hello.proxy.trace.logtrace.LogTrace;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

// 1. @Import 설명
// @Import를 사용하면 해당 클래스를 스프링 빈으로 등록할 수 있음
// 여기서는 AppV1Config.class를 스프링 빈으로 등록 -> AppV1Config에 빈에 등록이되어야 그안에 등록된 것들도 빈에 올라갈 수 있기 때문
// @Import는 일반적으로 @Configuraion같이 여러가지 빈 설정 파일을 등록할 때 사용하지만, 그냥 단순하게 하나의 클래스를 스프링 빈에 등록할 때도 사용할 수 있음

// 2. @Configuration 설명 (config 파일에 있는 애노테이션)
// 이 애노테이션은 내부에 @Component 애노테이션을 포함하고 있어서 자동으로 컴포넌트 스캔의 대상이 된다.
// 따라서 본래라면 컴포넌트 스캔에 의해 hello.proxy.config 위치의 설정 파일들도 전부 스프링 빈으로 자동 등록 됨
// 하지만 여기에서는 config파일의 자동 등록을 방지하기 위해, 컴포넌스 스캔의 시작 위치를 scanBasePackages=hello.proxy.app 로 설정해준 것!

// 3. @SpringBootApplication 이해
// 원래 현재 패키지를 포함한 하위 패키지 전부를 컴포넌트 스캔하는 애토테이션임 (허나, 2번에서 말했듯 여기선 옵션으로 세부경로를 지정해줌)
// 다시말해 "hello.proxy.app" 이하만 컴포넌트 스캔! (역시 2번에서 말했듯 안그러면 config 패키지 내에 있는 모든(V1~V3) AppConfig설정들이 다 자동 스캔되므로!)
// 즉, @Import를 사용해 V1, V2, V3 Config 파일을 각각을 필요할때마다 수동 등록하는 방식으로 실습해보려는 것! (즉 이러한 이유로 @Import도 사용하는 거임!: 본래 전체스캔할거면 안써도됨!)
//@Import({AppV1Config.class, AppV2Config.class}) // V3는 그냥 바로 자동스캔 되도록, controller, service, repository에 애노테이션을 붙여둠(그리고 전부 app패키지 이하에 있어서 자동스캔됨!)
//@Import({InterfaceProxyConfig.class, ConcreteProxyConfig.class}) // 프록시 적용을 위해 기존 임포트 주석처리하고 새로운 config 클래스 임포트!
//@Import(DynamicProxyBasicConfig.class)
//@Import(DynamicProxyFilterConfig.class)
//@Import({ProxyFactoryConfigV1.class, ProxyFactoryConfigV2.class})
//@Import(BeanPostProcessorConfig.class) // 빈후처리기를 통해 V1~V3까지 다적용 성공!!
//@Import(AutoProxyConfig.class) //  자동으로 프록시를 생성해주는 빈 후처리기를 통해 V1~V3까지 다적용 성공!!
@Import(AopConfig.class) // @Aspect를 통해 더 편하게 V1~V3 다 적용!
@SpringBootApplication(scanBasePackages = "hello.proxy.app")
public class ProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }

    // logTrace도 자연스레 DI 될 수 있도록 빈에 등록
    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace(); // 동시성 문제 해결하는 logTrace!
    }
}
