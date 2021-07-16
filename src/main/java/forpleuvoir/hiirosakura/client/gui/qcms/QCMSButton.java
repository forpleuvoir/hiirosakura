package forpleuvoir.hiirosakura.client.gui.qcms;

import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.gui.qcms
 * <p>#class_name QCMSButton
 * <p>#create_time 2021/7/17 1:00
 */
public class QCMSButton extends ButtonGeneric {
    private final List<Text> hoverText = new LinkedList<>();

    public QCMSButton(int x, int y, int width, int height, String text, Text... hoverText) {
        super(x, y, width, height, text, "");
        if (hoverText != null && hoverText.length > 0)
            this.hoverText.addAll(Arrays.asList(hoverText));
    }

    public List<Text> getHoverText() {
        return hoverText;
    }

    @Override
    public boolean hasHoverText() {
        return !hoverText.isEmpty();
    }
}