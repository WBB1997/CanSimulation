package eventbus;

public class MessageWrap {

    private final String message;

    public static MessageWrap getBean(String message) {
        return new MessageWrap(message);
    }

    private MessageWrap(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}