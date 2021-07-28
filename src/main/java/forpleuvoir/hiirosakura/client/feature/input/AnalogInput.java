package forpleuvoir.hiirosakura.client.feature.input;


/**
 * 模拟输入
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.input
 * <p>#class_name AnalogInput
 * <p>#create_time 2021-07-28 11:50
 */
public class AnalogInput {
    private static final AnalogInput INSTANCE = new AnalogInput();
    private int pressingForward = 0;
    private int pressingBack = 0;
    private int pressingLeft = 0;
    private int pressingRight = 0;
    private int jumping = 0;
    private int sneaking = 0;

    public void tick() {
        if (pressingForward > 0)
            pressingForward--;
        if (pressingBack > 0)
            pressingBack--;
        if (pressingLeft > 0)
            pressingLeft--;
        if (pressingRight > 0)
            pressingRight--;
        if (jumping > 0)
            jumping--;
        if (sneaking > 0)
            sneaking--;
    }

    public boolean getPressingForward() {
        return pressingForward > 0;
    }

    public boolean getPressingBack() {
        return pressingBack > 0;
    }

    public boolean getPressingLeft() {
        return pressingLeft > 0;
    }

    public boolean getPressingRight() {
        return pressingRight > 0;
    }

    public boolean getJumping() {
        return jumping > 0;
    }

    public boolean getSneaking() {
        return sneaking > 0;
    }

    public void setPressingForward(int pressingForward) {
        if (pressingForward > 0)
            this.pressingForward = pressingForward;
    }

    public void setPressingBack(int pressingBack) {
        if (pressingBack > 0)
            this.pressingBack = pressingBack;
    }

    public void setPressingLeft(int pressingLeft) {
        if (pressingLeft > 0)
            this.pressingLeft = pressingLeft;
    }

    public void setPressingRight(int pressingRight) {
        if (pressingRight > 0)
            this.pressingRight = pressingRight;
    }

    public void setJumping(int jumping) {
        if (jumping > 0)
            this.jumping = jumping;
    }

    public void setSneaking(int sneaking) {
        if (sneaking > 0)
            this.sneaking = sneaking;
    }

    public static AnalogInput getInstance() {
        return INSTANCE;
    }
}
