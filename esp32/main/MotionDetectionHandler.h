#ifndef MOTION_DETECTION_HANDLER
#define MOTION_DETECTION_HANDLER

class ServerManager;
class BleManager;

class MotionDetectionHandler {
  public:
    MotionDetectionHandler(ServerManager* serv, BleManager* bt);
    void begin(); 
    void handleMotionDetectionWiFi();
    void handleMotionDetectionBLE();
  private:
    bool lastState;

    ServerManager* server;
    BleManager* bluetooth;
};

#endif