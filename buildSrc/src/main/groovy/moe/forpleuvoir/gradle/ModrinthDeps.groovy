package moe.forpleuvoir.gradle

import groovy.json.JsonSlurper

/**
 * 通过 Modrinth 公开 API 查询项目版本。
 */
final class ModrinthDeps {

    private static final Map<String, String> RESOLVED_VERSION_IDS = [:]

    private ModrinthDeps() {
    }

    /**
     * 返回 [slug] 项目里版本号等于 [versionNumber] 且加载器包含 [loader] 的版本 ID；没有匹配版本时返回 null。
     * 同一版本号可能按加载器发布多条记录，因此必须用 [loader] 区分。
     * 查询结果按「项目 + 版本号 + 加载器」缓存在当前进程内，供多个模块复用同一次请求。
     * 网络异常、非 2xx 响应等直接抛出，由调用方决定回退策略。
     */
    static String resolveVersionId(String slug, String versionNumber, String loader, int timeoutMillis) {
        def cacheKey = "${slug}|${versionNumber}|${loader}"
        if (RESOLVED_VERSION_IDS.containsKey(cacheKey)) {
            return RESOLVED_VERSION_IDS[cacheKey]
        }
        def connection = new URI("https://api.modrinth.com/v2/project/${slug}/version").toURL().openConnection()
        connection.setRequestProperty("User-Agent", "forpleuvoir/hiirosakura-build")
        connection.connectTimeout = timeoutMillis
        connection.readTimeout = timeoutMillis
        def versions = new JsonSlurper().parse(connection.getInputStream())
        def match = versions.find { version ->
            def number = version.version_number
            def loaders = version.loaders instanceof List ? version.loaders : []
            number == versionNumber && (loader == null || loaders.contains(loader))
        }
        def versionId = match == null ? null : match.id
        RESOLVED_VERSION_IDS[cacheKey] = versionId
        return versionId
    }
}
