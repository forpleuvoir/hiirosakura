package forpleuvoir.hiirosakura.client.feature.chatshow;

import net.minecraft.util.math.Vec3f;

/**
 * 聊天显示配置
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.chatshow
 * <p>#class_name ChatShowConfig
 * <p>#create_time 2021/6/13 0:22
 */
public class ChatShowConfig {
    public float height = 0.8f;
    public int backgroundColor = 0XFFFFFFFF;
    public int textColor = 0XF8FFFFFF;
    public Vec3f scale = new Vec3f(0.05f, 0.05f, 0.05f);
    public double lineSpacing = scale.getX() * 10;
    public int time = 200;
    public int width = 10;
    public String playerNameRegex = "<(\\S*)> ";
}
