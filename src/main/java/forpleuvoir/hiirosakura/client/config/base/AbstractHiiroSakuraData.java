package forpleuvoir.hiirosakura.client.config.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.util.JsonUtils;
import forpleuvoir.hiirosakura.client.config.HiiroSakuraDatas;

import java.util.List;

/**
 * 数据抽象类
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.config.base
 * <p>#class_name AbstractHiiroSakuraData
 * <p>#create_time 2021/6/16 22:25
 */
public abstract class AbstractHiiroSakuraData {
    private final String name;
    private final IConfigHandler configHandler;

    public AbstractHiiroSakuraData(String name) {
        this.name = name;
        configHandler = HiiroSakuraDatas.getConfigHandler();
    }

    public static void writeData(JsonObject root, String category, List<? extends AbstractHiiroSakuraData> datas) {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);
        if (obj != null) {
            for (AbstractHiiroSakuraData data : datas) {
                obj.add(data.getName(), data.getAsJsonElement());
            }
        }

    }

    public static void readData(JsonObject root, String category, List<? extends AbstractHiiroSakuraData> datas) {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);
        if (obj != null) {
            for (AbstractHiiroSakuraData data : datas) {
                data.setValueFromJsonElement(obj.get(data.getName()));
            }
        }

    }


    /**
     * 获取数据的名称
     *
     * @return {@link String}
     */
    public String getName() {
        return this.name;
    }

    /**
     * 从JsonElement中获取数据
     *
     * @param element {@link JsonElement}
     */
    public abstract void setValueFromJsonElement(JsonElement element);

    /**
     * 将数据转换为JsonElement类型
     *
     * @return {@link JsonElement}
     */
    public abstract JsonElement getAsJsonElement();

    public void onValueChanged() {
        configHandler.onConfigsChanged();
    }
}
