#include "ServerManager.h"
#include "CameraManager.h"

ServerManager::ServerManager(CameraManager* cam): server(80), ws("/ws"), camera(cam) {}

void ServerManager::begin() {
  server.on("/photo", HTTP_GET, [this](AsyncWebServerRequest *request) {
    this->handlePhoto(request);
  });

  server.on("/is-connected", HTTP_GET, [this](AsyncWebServerRequest *request) {
    this->handleConnectionInfo(request);
  });
  
  server.addHandler(&ws);
  server.begin();
}

void ServerManager::handleConnectionInfo(AsyncWebServerRequest* request) {
  String message = "connected";

  request->send(200, "text/html", message);
}

void ServerManager::handlePhoto(AsyncWebServerRequest* request) {
  camera_fb_t *fb = camera->getFrame();

  if (!fb) {
    request->send(500, "text/plain", "Camera error");
    return;
  }

  AsyncWebServerResponse *response = request->beginResponse(200, "image/jpeg", fb->buf, fb->len);
  response->addHeader("Content-Disposition", "inline; filename=capture.jpg");
  request->send(response);

  camera->returnFrame(fb);
}

void ServerManager::wsNotify() {
  ws.textAll("ALARM_MOTION");
}

void ServerManager::wsCleanup() {
  ws.cleanupClients();
}