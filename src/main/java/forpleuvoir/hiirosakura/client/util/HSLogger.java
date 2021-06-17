package forpleuvoir.hiirosakura.client.util;

import forpleuvoir.hiirosakura.client.HiiroSakuraClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 日志辅助类
 *
 * @author forpleuvoir
 * <p>#project_name hiirosakura
 * <p>#package forpleuvoir.hiirosakura.client.util
 * <p>#class_name HSLogger
 * <p>#create_time 2021/6/16 22:41
 */
public record HSLogger(Class<?> clazz) {
    public static final Logger log = LoggerFactory.getLogger(HiiroSakuraClient.class);

    public static HSLogger getLogger(Class<?> clazz) {
        return new HSLogger(clazz);
    }

    public void info(String str, Object... params) {
        if (params != null && params.length != 0)
            log.info("({})" + str, clazz.getSimpleName(), params);
        else
            log.info("({}){}", clazz.getSimpleName(), str);
    }

    public void error(String str, Object... params) {
        if (params != null && params.length != 0)
            log.error("({})" + str, clazz.getSimpleName(), params);
        else
            log.error("({}){}", clazz.getSimpleName(), str);
    }

    public void error(Exception e) {
        log.error(String.format("(%s)%s", clazz.getSimpleName(), e.getMessage()), e);
    }

    public void warn(String str, Object... params){
        if (params != null && params.length != 0)
            log.warn("({})" + str, clazz.getSimpleName(), params);
        else
            log.warn("({}){}", clazz.getSimpleName(), str);
    }
}
