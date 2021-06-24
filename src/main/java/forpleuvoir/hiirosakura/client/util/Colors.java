package forpleuvoir.hiirosakura.client.util;

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
    FORPLEUVOIR(0xFF7F00),
    DHWUIA(0x17E5E5),
    YUYUKOSAMA(0xEB57AE);

    private final int color;
    Colors(int color) {
        this.color=color;
    }

    public int getColor(){
        return this.color;
    }
}
