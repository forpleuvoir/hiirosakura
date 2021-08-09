package forpleuvoir.hiirosakura.client.feature.input;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static forpleuvoir.hiirosakura.client.feature.input.AnalogInput.Key.values;


/**
 * 模拟输入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.input
 * <p>#class_name AnalogInput
 * <p>#create_time 2021-07-28 11:50
 */
public class AnalogInput {
    private static final AnalogInput INSTANCE = new AnalogInput();

    private final List<Node> data = new LinkedList<>();

    public AnalogInput() {
        for (Key key : values()) {
            data.add(Node.key(key));
        }
    }

    public void tick() {
        for (Node node : data) {
            node.tick();
        }
    }

    public boolean isPress(Key key) {
        AtomicBoolean isPress = new AtomicBoolean(false);
        data.stream().filter(node -> node.key == key).findFirst().ifPresent(node -> isPress.set(node.isPress()));
        return isPress.get();
    }

    public void set(Key key, Integer value) {
        data.stream().filter(node -> node.key == key).findFirst().ifPresent(node -> node.set(value));
    }

    public void setOnReleasedCallBack(Key key, Consumer<Key> onReleasedCallBack) {
        data.stream().filter(node -> node.key == key).findFirst().ifPresent(node -> node.onReleased = onReleasedCallBack);
    }

    private static class Node {
        public final Key key;
        public Integer value = 0;
        public Consumer<Key> onReleased;

        private static Node key(Key key) {
            return new Node(key);
        }

        private Node(Key key) {
            this.key = key;
        }

        private void set(Integer value) {
            if (value > 0)
                this.value = value;
        }

        private boolean isPress() {
            return value > 0;
        }

        private void tick() {
            if (value > 0) {
                value--;
                if (value == 0) {
                    if (onReleased != null)
                        onReleased.accept(key);
                }
            }

        }
    }

    public enum Key {
        FORWARD, BACK, LEFT, RIGHT, JUMP, SNEAK, ATTACK, USE, PICK_ITEM
    }

    public static AnalogInput getInstance() {
        return INSTANCE;
    }
}
