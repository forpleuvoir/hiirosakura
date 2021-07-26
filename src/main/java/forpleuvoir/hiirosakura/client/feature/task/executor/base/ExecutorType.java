package forpleuvoir.hiirosakura.client.feature.task.executor.base;

/**
 * 执行器类型
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.task.executor.base
 * <p>#class_name ExecutorType
 * <p>#create_time 2021-07-23 16:04
 */
public enum ExecutorType {
    sendChatMessage("sendChatMessage"),
    joinServer("joinServer"),
    doAttack("doAttack"),
    doItemUse("doItemUse")
    ;


    private final String type;

    ExecutorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
