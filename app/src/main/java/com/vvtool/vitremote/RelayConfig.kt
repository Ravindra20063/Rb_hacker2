package com.vvtool.vitremote

/** Configure these two values after deploying your Cloudflare Worker.
 * Relay URL must be the HTTPS Worker origin, e.g. https://vitremote-relay.yourname.workers.dev
 * Use a long random secret (at least 32 characters) shared with Worker secret PAIRING_SECRET.
 */
object RelayConfig {
    const val ENABLED = true
    const val RELAY_URL = "https://vitremote-relay.bhatiravindara64.workers.dev"
    const val PAIRING_SECRET = "00d64a41a58048d93a4219ffac9fbd5c259a7ee90eb0ae427746a2e2dc87323a"
}
