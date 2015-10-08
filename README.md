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
// basic usage
TTS
    .speak('hello, world!', function () {
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
    }, function () {
        alert('success');
    }, function (reason) {
        alert(reason);
    });
```

**Tips:** `speak` an empty string to interrupt.

## API Definitions

The `onfulfilled` callback will be called when the speech finishes,
and the `onrejected` callback (Windows Phone only) will be called when an error occurs.

If the API is invoked when it's still speaking, the previous speaking will be canceled immediately,
but the `onfulfilled` callback of the previous speaking will be called when it stops.

```typescript
declare module TTS {
    interface IOptions {
        /** text to speak */
        text: string;
        /** a string like 'en-US', 'zh-CN', etc */
        locale?: string;
        /** speed rate, 0 ~ 1 */
        rate?: number;
    }

    function speak(options: IOptions, onfulfilled: () => void, onrejected: (reason) => void): void;
    function speak(text: string, onfulfilled: () => void, onrejected: (reason) => void): void;
}
```
