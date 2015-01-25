/*

    Cordova Text-to-Speech Plugin
    https://github.com/vilic/cordova-plugin-tts

    by VILIC VANE
    https://github.com/vilic

    MIT License

*/

exports.speak = function (text, onfulfilled, onrejected) {
    var ThenFail = window.ThenFail;
    var promise;

    if (ThenFail && !onfulfilled && !onrejected) {
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
            } else if (onfulfilled) {
                onfulfilled();
            }
        }, function (reason) {
            if (promise) {
                promise.reject(reason);
            } else if (onrejected) {
                onrejected(reason);
            }
        }, 'TTS', 'speak', [options]);

    return promise;
};