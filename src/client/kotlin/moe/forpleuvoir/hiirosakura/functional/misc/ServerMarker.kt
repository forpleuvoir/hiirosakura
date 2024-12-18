package moe.forpleuvoir.hiirosakura.functional.misc

object ServerMarker {

    @JvmStatic
    var name: String? = null
        private set

    @JvmStatic
    var address: String? = null
        private set

    @JvmStatic
    var lastServerName: String? = null
        private set

    @JvmStatic
    var lastServerAddress: String? = null
        private set
    var disConnectCounter = 0
        private set

    @JvmStatic
    fun clear() {
        name = null
        address = null
    }

    @JvmStatic
    fun setValue(name: String?, address: String?) {
        this.name = name
        this.address = address
        lastServerName = name
        lastServerAddress = address
        disConnectCounter = 0
    }

    fun disconnect() {
        disConnectCounter++
    }

}