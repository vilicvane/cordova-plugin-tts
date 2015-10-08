/*

    Cordova Text-to-Speech Plugin
    https://github.com/vilic/cordova-plugin-tts

    by VILIC VANE
    https://github.com/vilic

    MIT License

*/

exports.speak = function (text, onfulfilled, onrejected) {
    var options = {};

    if (typeof text == 'string') {
        options.text = text;
    } else {
        options = text;
    }

    cordova
        .exec(function () {
            onfulfilled();
        }, function (reason) {
            onrejected(reason);
        }, 'TTS', 'speak', [options]);
};