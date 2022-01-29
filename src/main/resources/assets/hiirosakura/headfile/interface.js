var _jsi = Java.type("forpleuvoir.hiirosakura.client.feature.timertask.executor.jsexcutor.JavaScriptInterface").getInstance()
var _TimerTask = Java.type("forpleuvoir.hiirosakura.client.feature.timertask.TimerTask")
var _Timer = Java.type("forpleuvoir.hiirosakura.client.feature.timertask.Timer")

function sendChatMessage(message) {
    _jsi.sendChatMessage(message);
}

function msg(message) {
    sendChatMessage(message);
}

function cmd(cmd) {
    if (cmd.startsWith("/")) {
        msg(cmd);
    } else {
        msg("/" + cmd);
    }
}

function attack() {
    if (arguments.length === 1) {
        _jsi.attack(arguments[0]);
    } else {
        _jsi.doAttack();
    }
}

function use() {
    if (arguments.length === 1) {
        _jsi.use(arguments[0]);
    } else {
        _jsi.doItemUse();
    }
}

function pick() {
    if (arguments.length === 1) {
        _jsi.pickItem(arguments[0]);
    } else {
        _jsi.doItemPick();
    }
}

function sneak(tick) {_jsi.sneak(tick);}

function jump(tick) {_jsi.jump(tick);}

function move(dir, tick) {
    switch (dir) {
        case 'forward':
            _jsi.forward(tick);
            break;
        case 'back':
            _jsi.back(tick);
            break;
        case 'left':
            _jsi.left(tick);
            break;
        case 'right':
            _jsi.right(tick);
            break;
    }
}

function onceTask(executor) {
    _Timer.scheduleEnd(executor)
}