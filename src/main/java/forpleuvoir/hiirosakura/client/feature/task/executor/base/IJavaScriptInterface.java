package forpleuvoir.hiirosakura.client.feature.task.executor.base;

/**
 * js接口
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor.base
 * <p>#class_name IJavaScriptInterface
 * <p>#create_time 2021/7/27 23:16
 */
public interface IJavaScriptInterface {

    /**
     * 模拟按下方向前进按键
     * @param tick 持续时间
     */
    void forward(int tick);

    /**
     * 模拟按下方向后退按键
     * @param tick 持续时间
     */
    void back(int tick);

    /**
     * 模拟按下方向左按键
     * @param tick 持续时间
     */
    void left(int tick);

    /**
     * 模拟按下方向右按键
     * @param tick 持续时间
     */
    void right(int tick);

    /**
     * 模拟按下跳跃按键
     * @param tick 持续时间
     */
    void jump(int tick);

    /**
     * 模拟下潜行键
     * @param tick 持续时间
     */
    void sneak(int tick);

    /**
     * 客户端玩家攻击一次
     */
    void doAttack();

    /**
     * 客户端玩家使用一次物品
     */
    void doItemUse();

    /**
     * 模拟按下鼠标中键
     */
    void doItemPick();

    /**
     * 加入服务器
     * @param address 服务器ip地址
     */
    void joinServer(String address);

    /**
     * 加入服务器
     * @param address 服务器ip地址
     * @param maxConnect 最大连接次数
     */
    void joinServer(String address,int maxConnect);

    /**
     * 发送聊天消息
     * @param message 消息文本
     */
    void sendChatMessage(String message);
}
