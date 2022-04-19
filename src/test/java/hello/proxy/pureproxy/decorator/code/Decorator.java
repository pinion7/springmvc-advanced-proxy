package hello.proxy.pureproxy.decorator.code;

public abstract class Decorator implements Component {

    protected Component component;
    protected Decorator(Component component) {
        this.component = component;
    }
}
