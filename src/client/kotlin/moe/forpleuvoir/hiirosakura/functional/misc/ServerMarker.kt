package moe.forpleuvoir.hiirosakura.functional.misc

object ServerMarker {

    @JvmStatic
    var name: String = ""
        private set

    @JvmStatic
    var address: String = ""
        private set

    @JvmStatic
    var lastServerName: String = ""
        private set

    @JvmStatic
    var lastServerAddress: String = ""
        private set
    var disConnectCounter = 0
        private set

    @JvmStatic
    fun clear() {
        name = ""
        address = ""
    }

    @JvmStatic
    fun setValue(name: String?, address: String?) {
        this.name = name ?: ""
        this.address = address ?: ""
        lastServerName = name ?: ""
        lastServerAddress = address ?: ""
        disConnectCounter = 0
    }

    fun disconnect() {
        disConnectCounter++
    }

}