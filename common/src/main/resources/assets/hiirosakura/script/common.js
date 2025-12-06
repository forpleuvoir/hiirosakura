var _api = Java.type("moe.forpleuvoir.hiirosakura.functional.script.CommonApi").getINSTANCE();
var _inputSimulator = Java.type("moe.forpleuvoir.hiirosakura.input.InputSimulator").INSTANCE;
var Color = Java.type("moe.forpleuvoir.nebula.common.color.Color");
var HSVColor = Java.type("moe.forpleuvoir.nebula.common.color.HSVColor");

function color() {
    if (arguments.length === 1) {
        return new Color(arguments[0])
    }
    if (arguments.length === 3) {
        return new Color(arguments[0], arguments[1], arguments[2], 1.0, false)
    }
    if (arguments.length === 4) {
        return new Color(arguments[0], arguments[1], arguments[2], arguments[3], false)
    }
    return new Color(0, 0, 0, 1.0, false)
}

function hsv(hue, saturation, value, alpha) {
    if (arguments.length === 1) {
        return new HSVColor(arguments[0], false)
    }
    if (arguments.length === 3) {
        return new HSVColor(arguments[0], arguments[1], arguments[2], 1.0, false)
    }
    if (arguments.length === 4) {
        return new HSVColor(arguments[0], arguments[1], arguments[2], arguments[3], false)
    }
    return new HSVColor(0, 0, 0, 1.0, false)
}

function sendMessage(msg) {
    _api.sendMessage(msg);
}

function msg(msg) {
    sendMessage(msg)
}

function sendCommand(cmd) {
    _api.sendCommand(cmd);
}

function cmd(cmd) {
    sendCommand(cmd)
}

function sendNotification() {
    if (arguments.length === 1) {
        _api.sendNotification("Notification", arguments[0])
    } else if (arguments.length === 2) {
        _api.sendNotification(arguments[0], arguments[1])
    }
}

function notice() {
    if (arguments.length === 1) {
        _api.sendNotification("Notification", arguments[0])
    } else if (arguments.length === 2) {
        _api.sendNotification(arguments[0], arguments[1])
    }
}

function toast(content) {
    _api.toast(content);
}

function attack() {
    if (arguments.length === 0) {
        _api.attack(1)
    } else {
        _api.attack(arguments[0])
    }
}

function use() {
    if (arguments.length === 0) {
        _api.use(1)
    } else {
        _api.use(arguments[0])
    }
}

function pickItem() {
    if (arguments.length === 0) {
        _api.pickItem(1)
    } else {
        _api.pickItem(arguments[0])
    }
}

function move(dir, duration) {
    switch (dir) {
        case 'forward':
        case 0:
            _api.moveForward(duration);
            break;
        case 'back':
        case 1:
            _api.moveBack(duration);
            break;
        case 'left':
        case 2:
            _api.moveLeft(duration);
            break;
        case 'right':
        case 3:
            _api.moveRight(duration);
            break;
    }
}

function moveForward() {
    if (arguments.length === 0) {
        _api.moveForward(1)
    } else {
        _api.moveForward(arguments[0])
    }
}

function moveBack() {
    if (arguments.length === 0) {
        _api.moveBack(1)
    } else {
        _api.moveBack(arguments[0])
    }
}

function moveLeft() {
    if (arguments.length === 0) {
        _api.moveLeft(1)
    } else {
        _api.moveLeft(arguments[0])
    }
}

function moveRight() {
    if (arguments.length === 0) {
        _api.moveRight(1)
    } else {
        _api.moveRight(arguments[0])
    }
}

function jump() {
    if (arguments.length === 0) {
        _api.jump(1)
    } else {
        _api.jump(arguments[0])
    }
}

function sneak() {
    if (arguments.length === 0) {
        _api.sneak(1)
    } else {
        _api.sneak(arguments[0])
    }
}

function sprint() {
    if (arguments.length === 0) {
        _api.sprint(1)
    } else {
        _api.sprint(arguments[0])
    }
}

function getGlobalData(key) {
    return _api.getGlobalData(key)
}

function setGlobalData(key, data) {
    _api.setGlobalData(key, data)
}

function setCustomData(key, value) {
    _api.setCustomData(key, value)
}

function getCustomData(key) {
    return _api.getCustomData(key)
}

function delayLaunch(duration, action) {
    _api.delayLaunch(duration, action)
}

function scheduleStartTick() {
    if (arguments.length === 1) {
        _api.scheduleStartTick(0, arguments[0])
    } else if (arguments.length === 2) {
        _api.scheduleStartTick(arguments[0], arguments[1])
    }
}

function scheduleEndTick() {
    if (arguments.length === 1) {
        _api.scheduleEndTick(0, arguments[0])
    } else if (arguments.length === 2) {
        _api.scheduleEndTick(arguments[0], arguments[1])
    }
}

function setConfig(key, value) {
    _api.setConfig(key, value)
}

function enableEvent(event, enabled) {
    _api.enableEvent(event, enabled)
}