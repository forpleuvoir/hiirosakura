package forpleuvoir.hiirosakura.client.feature.qcms;

import java.util.Objects;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.qcms
 * <p>#class_name QuickChatMessage
 * <p>#create_time 2021/7/16 23:59
 */
public record QuickChatMessage(String remark, String message,Integer level) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuickChatMessage that = (QuickChatMessage) o;
        return Objects.equals(remark, that.remark) && Objects
                .equals(message, that.message) && Objects.equals(level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remark, message, level);
    }
}