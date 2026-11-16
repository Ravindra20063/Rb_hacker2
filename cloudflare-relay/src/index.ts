export interface Env {
  ROOMS: DurableObjectNamespace;
  PAIRING_SECRET: string;
  ASSETS: Fetcher;
}
export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);
    if (url.pathname === "/health") return new Response("vitRemote relay ready");
    if (!url.pathname.startsWith("/room/")) return env.ASSETS.fetch(request);
    const m = url.pathname.match(/^\/room\/([A-Za-z0-9_-]{6,40})$/);
    if (!m) return new Response("Not found", { status: 404 });
    if (request.headers.get("Upgrade") !== "websocket") return new Response("WebSocket required", { status: 426 });
    const role = url.searchParams.get("role");
    const token = url.searchParams.get("token") || "";
    if (role !== "device" && role !== "viewer") return new Response("Invalid role", { status: 400 });
    if (!env.PAIRING_SECRET || token.length < 6 || token !== env.PAIRING_SECRET) return new Response("Unauthorized", { status: 401 });
    const id = env.ROOMS.idFromName(m[1]);
    return env.ROOMS.get(id).fetch(request);
  }
};
export class Room {
  private device?: WebSocket;
  private viewer?: WebSocket;
  constructor(private state: DurableObjectState, private env: Env) {}
  async fetch(request: Request): Promise<Response> {
    const url = new URL(request.url);
    const role = url.searchParams.get("role");
    const pair = new WebSocketPair();
    const client = pair[0], server = pair[1];
    server.accept();
    if (role === "device") {
      this.closeOld(this.device);
      this.device = server;
    } else if (role === "viewer") {
      this.closeOld(this.viewer);
      this.viewer = server;
    } else return new Response("Invalid role", { status: 400 });
    server.addEventListener("message", event => {
      const peer = role === "device" ? this.viewer : this.device;
      if (peer && peer.readyState === WebSocket.OPEN) {
        try { peer.send(event.data); } catch {}
      }
    });
    server.addEventListener("close", () => {
      if (role === "device" && this.device === server) this.device = undefined;
      if (role === "viewer" && this.viewer === server) this.viewer = undefined;
    });
    server.addEventListener("error", () => {
      if (role === "device" && this.device === server) this.device = undefined;
      if (role === "viewer" && this.viewer === server) this.viewer = undefined;
    });
    return new Response(null, { status: 101, webSocket: client });
  }
  private closeOld(socket?: WebSocket) {
    if (socket && socket.readyState === WebSocket.OPEN) {
      try { socket.close(4001, "Replaced by newer connection"); } catch {}
    }
  }
}

