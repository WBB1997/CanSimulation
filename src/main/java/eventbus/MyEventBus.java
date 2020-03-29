package eventbus;

import com.google.common.eventbus.EventBus;

public class MyEventBus {
    private static EventBus instance = new EventBus();

    private MyEventBus(){
        System.out.println("EventBus has loaded");
    }

    // 发送事件
    public static void post(Object event) {
        instance.post(event);
    }

    // 为类注册监听
    public static void register(Object object) {
        System.out.println("EventBus: " + object.getClass().getName() + " register");
        instance.register(object);
    }

    // 为类注销监听
    public static void unregister(Object object) {
        System.out.println("EventBus: " + object.getClass().getName() + " unregister");
        instance.unregister(object);
    }
}
