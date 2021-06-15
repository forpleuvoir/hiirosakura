package forpleuvoir.hiirosakura.client;

import fi.dy.masa.malilib.event.InitializationHandler;
import forpleuvoir.hiirosakura.client.feature.chatshow.HiiroSakuraChatShow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client
 * <p>#class_name HiiroSakuraClient
 * <p>#create_time 2021/6/10 21:38
 */
@Environment(EnvType.CLIENT)
public class HiiroSakuraClient implements ClientModInitializer{
    public static final Logger log = LoggerFactory.getLogger(HiiroSakuraClient.class);
    //客户端tick处理器
    private static final Queue<Consumer<MinecraftClient>> tickers = new ConcurrentLinkedQueue<>();
    //客户端任务队列
    private static final Queue<Consumer<MinecraftClient>> tasks = new ConcurrentLinkedQueue<>();
    public static final String MOD_ID = "hiirosakura";
    public static final String MOD_NAME = "Hiiro Sakura";
    private static long trackingTick = 0;


    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        //初始化聊天泡泡
        HiiroSakuraChatShow.initialize();
        //MinecraftClient.tick注册
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);

    }

    /**
     * 客户端tick之后会调用
     * @param client {@link MinecraftClient}
     */
    public void onEndTick(MinecraftClient client) {
        tickers.forEach(minecraftClientConsumer -> minecraftClientConsumer.accept(client));
        HiiroSakuraClient.runTask(client);
        trackingTick++;
    }

    /**
     * 执行所有未被执行的客户端任务
     *
     * @param client {@link MinecraftClient}
     */
    public static void runTask(MinecraftClient client) {
        Iterator<Consumer<MinecraftClient>> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Consumer<MinecraftClient> next = iterator.next();
            next.accept(client);
            iterator.remove();
        }
    }

    /**
     * 添加客户端任务
     * 客户端每次tick结束后会执行，执行后的任务会被删除
     *
     * @param task {@link Consumer<MinecraftClient>}
     */
    public static void addTask(Consumer<MinecraftClient> task) {
        tasks.add(task);
    }

    /**
     * 添加Tick处理程序
     * <p>每次客户端tick结束都会执行一次
     *
     * @param handler {@link Consumer<MinecraftClient>}
     */
    public static void addTickHandler(Consumer<MinecraftClient> handler) {
        tickers.add(handler);
    }

    public static long getTrackingTick() {
        return trackingTick;
    }
}
