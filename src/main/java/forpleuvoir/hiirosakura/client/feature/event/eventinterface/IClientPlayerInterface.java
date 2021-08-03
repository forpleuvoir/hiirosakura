package forpleuvoir.hiirosakura.client.feature.event.eventinterface;

/**
 * 客户端玩家接口
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.feature.event.eventinterface
 * <p>#class_name IClientPlayerInterface
 * <p>#create_time 2021/8/1 14:08
 */
public interface IClientPlayerInterface {

    /**
     * 获取玩家 UUID
     *
     * @return UUID
     */
    String getUUID();

    /**
     * 获取玩家名
     *
     * @return 玩家名
     */
    String getName();

    /**
     * 获取生命值
     *
     * @return 生命值
     */
    float getHealth();

    /**
     * 获取主手物品注册id
     * @return 注册id 例 minecraft:melon
     */
    String getMainHandItemRegisterId();

    /**
     * 获取主手物品翻译key
     * @return 翻译key
     */
    String getMainHandItemTranslationKey();

    /**
     * 获取助手物品显示名
     * @return 现实的名字
     */
    String getMainHandItemDisplayName();

    /**
     * 获取主手物品的耐久值
     * @return 耐久值
     */
    int getMainHandItemDurable();

    /**
     * 获取主手物品的最大耐久值
     * @return 耐久值
     */
    int getMainHandItemMaxDurable();

    /**
     * 获取主手物品剩余耐久百分比
     * @return 耐久百分比
     */
    double getMainHandItemDurableProgress();

    /**
     * 是否免疫火焰
     *
     * @return 免疫火焰状态
     */
    boolean isFireImmune();

    /**
     * 是否处于着火状态
     *
     * @return 着火状态
     */
    boolean isOnFire();

    /**
     * 是否接触水或在淋雨
     *
     * @return 接触水或在淋雨状态
     */
    boolean isTouchingWaterOrRain();

    /**
     * 是否处于潜行状态
     *
     * @return 潜行状态
     */
    boolean isSneaking();

    /**
     * 是否正在游泳
     *
     * @return 游泳状态
     */
    boolean isSwimming();

    /**
     * 是否隐形
     *
     * @return 隐形状态
     */
    boolean isInvisible();

    /**
     * 获取玩家位置
     *
     * @return 数组 0：X坐标 ，1：Y坐标，2：坐标
     */
    double[] getPosition();

    /**
     * 获取玩家 X 坐标
     *
     * @return X 坐标
     */
    double getPosX();

    /**
     * 获取玩家 Y 坐标
     *
     * @return Y 坐标
     */
    double getPosY();

    /**
     * 获取玩家 Z 坐标
     *
     * @return Z 坐标
     */
    double getPosZ();



    /**
     * 获取X轴旋转
     *
     * @return Pitch
     */
    float getPitch();

    /**
     * 获取Y轴旋转
     *
     * @return Yaw
     */
    float getYaw();

    /**
     * 获取玩家速度
     *
     * @return 速度
     */
    float getSpeed();

    /**
     * 获取玩家经验等级
     *
     * @return 经验等级
     */
    int getExperienceLevel();

    /**
     * 获取玩家经验总量
     *
     * @return 经验总量
     */
    int getTotalExperience();

    /**
     * 获取玩家经验 升级进度百分比
     *
     * @return 升级进度百分比
     */
    float getExperienceProgress();

}
