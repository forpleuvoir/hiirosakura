package forpleuvoir.hiirosakura.client.feature.tooltip;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import forpleuvoir.hiirosakura.client.config.Configs;
import forpleuvoir.hiirosakura.client.config.base.AbstractHiiroSakuraData;
import forpleuvoir.hiirosakura.client.util.HSLogger;
import forpleuvoir.hiirosakura.client.util.JsonUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.*;

import static forpleuvoir.hiirosakura.client.util.PlayerHeadUtil.getSkullOwner;

/**
 * 工具提示
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.tooltip
 * <p>#class_name Tooltip
 * <p>#create_time 2021/6/17 0:55
 */
public class Tooltip extends AbstractHiiroSakuraData {

    private transient static final HSLogger log = HSLogger.getLogger(Tooltip.class);
    public Map<String, List<String>> datas = new HashMap<>();

    public Tooltip() {
        super("tooltip");
    }

    private void add(String key, String... tooltip) {
        if (tooltip == null || tooltip.length == 0) return;
        List<String> tooltips = Lists.newArrayList();
        Arrays.stream(tooltip).forEach(s -> tooltips.add(s.replace("&", "§")));
        if (datas.containsKey(key)) {
            datas.get(key).addAll(tooltips);
        } else {
            datas.put(key, tooltips);
        }
        this.onValueChanged();
    }

    public void add(ItemStack item, String... tooltip) {
        add(getKey(item), tooltip);
    }

    public String remove(ItemStack item, int index) {
        return remove(getKey(item), index);
    }

    private String remove(String key, int index) {
        if (datas.containsKey(key)) {
            StringBuilder s = new StringBuilder();
            if (index < 0) {
                for (String s1 : datas.get(key)) {
                    s.append(s1);
                    s.append(",");
                }
                s.deleteCharAt(s.length()-1);
                datas.remove(key);
            } else {
                try {
                    s = new StringBuilder(datas.get(key).get(index));
                    datas.get(key).remove(index);
                    if(datas.get(key).size()==0){
                        datas.remove(key);
                    }
                } catch (IndexOutOfBoundsException e) {
                    return null;
                }
            }
            this.onValueChanged();
            return s.toString();
        }
        return null;
    }

    public List<Text> getTooltip(ItemStack stack) {
        var list = new ArrayList<Text>();
        if (Configs.Toggles.SHOW_TOOLTIP.getBooleanValue()) {
            String key = getKey(stack);
            if (datas.containsKey(key)) {
                var tips = datas.get(key);
                tips.forEach(tip -> list.add(new LiteralText(tip)));
            }
        }
        return list;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        try {
            if (element.isJsonObject()) {
                JsonObject object = element.getAsJsonObject();
                Map<String, List<String>> data = JsonUtil.gson
                        .fromJson(object, new TypeToken<Map<String, List<String>>>() {
                        }.getType());
                datas.clear();
                datas.putAll(data);
            } else {
                log.warn("{}无法从JsonElement{}中读取数据", this.getName(), element);
            }
        } catch (Exception e) {
            log.warn("{}无法从JsonElement{}中读取数据", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement() {
        return JsonUtil.gson.toJsonTree(datas);
    }

    public static String getKey(ItemStack stack) {
        String value = "";
        Item item = stack.getItem();
        String name = item.getName(stack).getString();
        String cName = stack.getName().getString();
        if (stack.getItem().equals(Items.PLAYER_HEAD)) {
            value = ":" + getSkullOwner(stack);
        } else if (!name.equals(cName)) {
            value = "#" + cName;
        }
        return item.getTranslationKey(stack) + value;
    }


}
