/*

    Cordova Text-to-Speech Plugin
    https://github.com/vilic/cordova-plugin-tts

    by VILIC VANE
    https://github.com/vilic

    MIT License

*/

exports.speak = function (text, onfulfill, onreject) {
    var ThenFail = window.ThenFail;
    var promise;

    if (ThenFail && !onfulfill && !onreject) {
        promise = new ThenFail();
    }
    
    var options = {};

    if (typeof text == 'string') {
        options.text = text;

    } else {
        options = text;
    }

    cordova
        .exec(function () {
            if (promise) {
                promise.resolve();
            } else if (onfulfill) {
                onfulfill();
            }
        }, function (reason) {
            if (promise) {
                promise.reject(reason);
            } else if (onreject) {
                onreject(reason);
            }
        }, 'TTS', 'speak', [options]);

    return promise;
};