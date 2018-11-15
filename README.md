This is a library tool to aid in the translation of messages for various locales.
<p>
This tool is used to allow Java programmers to quickly add future support
for multiple languages for various locales in their coding up-front without 
having to code any differently to support the swapping out of messages for different
locales.
<p>
This means that messages can still appear in the code where they are being used without
having to replace them with less descriptive variables.
<p>
Also, This tool allows for initial functionality without a language library file and then
one can be added easily in the future, by simply specifying the path to where you plan on
putting the file once it is available.
<p>
The language library files are required to be in a JSON format like the following example:
<pre>
  {
     "zh" : {
         "Hello how are you today?" : "你好，今天好嗎？",
         "Hope you day is nice!" : "希望你的一天好！"
     },
     "sv" : {
         "Hello how are you today?" : "Hej hur mår du idag?",
         "Hope you day is nice!" : "Hoppas att dagen är snäll!"
     },
     "fr" : {
         "Hello how are you today?" : "Bonjour comment vas tu aujourd'hui?",
         "Hope you day is nice!" : "J'espère que votre journée est belle!"
     },
     "ja" : {
         "Hello how are you today?" : "今日こんにちは。",
         "Hope you day is nice!" : "あなたは1日がいいといいですね！"
     }
  }
</pre>