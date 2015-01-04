/*
    Cordova Text-to-Speech Plugin
    https://github.com/vilic/cordova-plugin-tts
    
    by VILIC VANE
    https://github.com/vilic

    MIT License
*/

using System;
using System.Diagnostics;
using System.Runtime.Serialization;
using System.Threading.Tasks;
using Windows.Phone.Speech.Synthesis;
using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;

namespace Cordova.Extension.Commands {
    [DataContract]
    class Options {
        [DataMember]
        public string text;
        [DataMember]
        public string locale;
        [DataMember]
        public double? rate;
    }

    class TTS : BaseCommand {
        SpeechSynthesizer synth = new SpeechSynthesizer();

        string lastCallbackId;

        public async void speak(string argsJSON) {
            if (lastCallbackId != null) {
                DispatchCommandResult(new PluginResult(PluginResult.Status.OK), lastCallbackId);
                lastCallbackId = null;
                synth.CancelAll();
            }

            var args = JsonHelper.Deserialize<string[]>(argsJSON);
            var options = JsonHelper.Deserialize<Options>(args[0]);
            lastCallbackId = args[1];

            var locale = options.locale != null ? options.locale : "en-US";
            var rate = options.rate != null ? options.rate : 1.0;

            var ssml =
@"<?xml version=""1.0""?>
<speak version=""1.0""
    xmlns=""http://www.w3.org/2001/10/synthesis""
    xml:lang=""" + locale + @""">
    <prosody pitch=""x-high"" rate=""" + rate + @""">" + xmlEncode(options.text) + @"</prosody>
</speak>";

            try {
                await synth.SpeakSsmlAsync(ssml);
                lastCallbackId = null;
                DispatchCommandResult(new PluginResult(PluginResult.Status.OK));
            } catch (OperationCanceledException) {
                // do nothing
            } catch (Exception e) {
                Debug.WriteLine(e.Message);
                lastCallbackId = null;
                DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, e.Message));
            }
        }

        string xmlEncode(string text) {
            return text
                .Replace("&", "&amp;")
                .Replace("<", "&lt;")
                .Replace(">", "&gt;");
        }
    }
}
