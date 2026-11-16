package com.vvtool.vitremote

/** Configure these two values after deploying your Cloudflare Worker.
 * Relay URL must be the HTTPS Worker origin, e.g. https://vitremote-relay.yourname.workers.dev
 * Use a long random secret (at least 32 characters) shared with Worker secret PAIRING_SECRET.
 */
object RelayConfig {
    const val ENABLED = true
    const val RELAY_URL = "https://YOUR-WORKER.workers.dev"
    const val PAIRING_SECRET = "REPLACE_WITH_LONG_RANDOM_SECRET_32_CHARS_MIN"
}
