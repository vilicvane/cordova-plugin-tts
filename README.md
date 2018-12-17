# Cordova Text-to-Speech Plugin

## Platforms

iOS 7+  
Windows Phone 8  
Android 4.0.3+ (API Level 15+)

## Installation

```sh
cordova plugin add cordova-plugin-tts
```

## Usage

```javascript
// make sure your the code gets executed only after `deviceready`.
document.addEventListener('deviceready', function () {
    // basic usage
    TTS
        .speak('hello, world!').then(function () {
            alert('success');
        }, function (reason) {
            alert(reason);
        });

    // or with more options
    TTS
        .speak({
            text: 'hello, world!',
            locale: 'en-GB',
            rate: 0.75
        }).then(function () {
            alert('success');
        }, function (reason) {
            alert(reason);
        });
}, false);
```

**Tips:** `speak` an empty string to interrupt.

```typescript
declare namespace TTS {
    interface IOptions {
        /** text to speak */
        text: string;
        /** a string like 'en-US', 'zh-CN', etc */
        locale?: string;
        /** speed rate, 0 ~ 1 */
        rate?: number;
        /** ambient(iOS) */
        category?: string;
    }

    function speak(options: IOptions): Promise<void>;
    function speak(text: string): Promise<void>;
    function stop(): Promise<void>;
    function checkLanguage(): Promise<string>;
    function openInstallTts(): Promise<void>;
}
```
