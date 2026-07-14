#ifndef SERVER_MANAGER_H
#define SERVER_MANAGER_H

#include <ESPAsyncWebServer.h>

class CameraManager;

class ServerManager {
  public:
    ServerManager(CameraManager* cam);
    void begin();
    void handleConnectionInfo(AsyncWebServerRequest* request);
    void handlePhoto(AsyncWebServerRequest* request);
    void wsNotify();
    void wsCleanup();
  private:
    AsyncWebServer server;
    AsyncWebSocket ws;
    CameraManager* camera;
};

#endif