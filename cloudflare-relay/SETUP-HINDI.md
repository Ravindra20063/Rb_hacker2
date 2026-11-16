# vitRemote Cloudflare Relay — हिंदी setup

## ध्यान दें
यह relay internet पर browser viewer और target Android phone के बीच WebSocket traffic forward करता है। फोन के मालिक को Android Accessibility तथा screen-capture की पहली system अनुमति स्वयं देनी होगी। Android द्वारा बाद में मांगी जाने वाली screen-capture अनुमति को इस ऐप द्वारा bypass नहीं किया जाता।

## Cloudflare deploy (मोबाइल Termux या कंप्यूटर)
1. Cloudflare account बनाकर Workers & Pages में login करें।
2. Node.js और Wrangler install करें: `npm install -g wrangler`
3. इस `cloudflare-relay` folder में जाएं और `wrangler login` चलाएं।
4. एक लंबा random shared secret (कम-से-कम 32 characters) बनाएं।
5. `npx wrangler secret put PAIRING_SECRET` चलाकर वही secret paste करें।
6. Deploy करें: `npx wrangler deploy`
7. Wrangler आपको Worker URL देगा, उदाहरण `https://vitremote-relay.<account>.workers.dev`।

## Android project config
1. Android source में `app/src/main/java/com/vvtool/vitremote/RelayConfig.kt` खोलें।
2. `RELAY_URL` में अपना Worker URL डालें और `PAIRING_SECRET` में वही secret डालें।
3. Cloudflare viewer page config के लिए `cloudflare-relay/public/index.html` में `RELAY_ENDPOINT` और `RELAY_SECRET` को उसी URL और secret से replace करें।
4. फिर relay folder से `npx wrangler deploy` दोबारा चलाएं ताकि viewer page update हो।
5. Android project GitHub पर upload करके Actions APK build workflow run करें (या Android Studio/Gradle build)।

## Pairing / usage
- Target phone में ऐप start रखें, Accessibility service enabled हो। PIN ऐप में displayed permanent pairing PIN है।
- Controller phone browser में Worker URL खोलें, URL के अंत में `?relay=1` जोड़ें।
- PIN डालें; same room PIN वाले device tunnel से viewer connect होगा।
- नेटवर्क drop होने पर Android relay client 5 सेकंड के अंतराल पर reconnect करता है।

## Security
- Secret को public repo में रखने से बचें; APK में client secret embed होने के कारण इसे production-grade secret storage न समझें। Private GitHub repository रखें।
- Pairing PIN साझा न करें; Worker shared secret leaked हो जाए तो Cloudflare पर secret rotate करें।
- यह remote control तभी इस्तेमाल करें जब device owner ने स्पष्ट अनुमति दी हो।
