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
     * 客户端玩家攻击一次
     */
    void doAttack();

    /**
     * 客户端玩家使用一次物品
     */
    void itemUse();

    /**
     * 加入服务器
     * @param address 服务器ip地址
     */
    void joinServer(String address);

    /**
     * 发送聊天消息
     * @param message 消息文本
     */
    void sendChatMessage(String message);
}
