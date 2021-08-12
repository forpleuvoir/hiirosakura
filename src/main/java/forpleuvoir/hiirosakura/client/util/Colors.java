package forpleuvoir.hiirosakura.client.util;


import java.awt.*;

/**
 * 颜色
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name Colors
 * <p>#create_time 2021/6/24 20:17
 */
public enum Colors {
    FORPLEUVOIR(0xFFFF7F00),
    DHWUIA(0xFF17E5E5),
    YUYUKOSAMA(0xFFEB57AE);

    private final int color;

    Colors(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    /**
     * 这段代码属实烂
     * @param alpha 透明度 0-255
     * @return 颜色
     */
    public int getColor(int alpha) {
        alpha = Math.max(0, alpha);
        alpha = Math.min(255, alpha);
        var c = new Color(this.color);
        var color = new Color(c.getRed(), c.getBlue(), c.getGreen(), alpha);
        return color.getRGB();
    }
}
