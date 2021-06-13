package forpleuvoir.hiirosakura.client.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 物品栈工具
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name ItemStackUtil
 * <p>#create_time 2021/6/13 16:12
 */
public class ItemStackUtil {

    /**
     * 获取该物品栈的所有附魔属性以及等级文本 格式如  (抢夺 III)
     * <p>等级为1的附魔则不会显示等级
     * @param stack {@link ItemStack}
     * @param formatting {@link Formatting}
     * @return 附魔文本列表
     */
    public static List<Text> getEnchantmentsWithLvl(ItemStack stack, Formatting... formatting) {
        LinkedList<Text> texts = new LinkedList<>();
        if (stack.getEnchantments().isEmpty()) {
            return texts;
        }
        stack.getEnchantments().forEach(nbtElement -> {
            if (nbtElement instanceof NbtCompound tag) {
                int lvl = tag.getInt("lvl");
                String[] originKey = tag.getString("id").split(":");
                String key = String.format("enchantment.%s.%s", originKey[0], originKey[1]);
                MutableText text = new TranslatableText(key).append(" ");
                if (lvl != 1) {
                    text.append(new TranslatableText("enchantment.level." + lvl));
                }
                text.formatted(formatting);
                texts.addLast(text);
            }
        });
        return texts;
    }
}
