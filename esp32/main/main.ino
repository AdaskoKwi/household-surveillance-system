#include "DisplayManager.h"
#include "WifiManager.h"
#include "CameraManager.h"
#include "ServerManager.h"
#include "BleManager.h"
#include "MotionDetectionHandler.h"

//
//  DOPISAĆ JAK PRZYGOTOWANE ZOSTAŁY BITMAPY Z OBRAZKAMI
//

DisplayManager display;
WifiManager wifi;
CameraManager camera;
ServerManager server(&camera);
BleManager bluetooth(&camera, &display);
MotionDetectionHandler motionHandler(&server, &bluetooth);

void setup() {
  Serial.begin(115200);

  display.begin();
  wifi.begin();
  server.begin();
  bluetooth.begin();
  motionHandler.begin();
  bool cameraStatus = camera.init();

  if (cameraStatus == false) {
    Serial.println("Blad kamery");
    return;
  }
}

void loop() {
  if (bluetooth.isSendingPhoto) return;

  display.clear();

  auto wifiDeviceCount = wifi.getDeviceCount();

  if (wifiDeviceCount > 0) {
    display.drawStringCentered("Polaczono przez WiFi", 64, 16);
    String ip = wifi.getIP();
    display.drawStringCentered(ip.c_str(), 64, 30);
    display.drawBitmapCentered(DisplayManager::bitmap_wifi, SCREEN_WIDTH, SCREEN_HEIGHT + SCREEN_HEIGHT / 2);

    motionHandler.handleMotionDetectionWiFi();

  } else if (bluetooth.connected) {
    display.drawStringCentered("Polaczono przez BLE", 64, 20);
    display.drawBitmapCentered(DisplayManager::bitmap_bt, SCREEN_WIDTH, SCREEN_HEIGHT + SCREEN_HEIGHT / 2);

    motionHandler.handleMotionDetectionBLE();

  } else {
    display.waitingForConnection();
  }

  server.wsCleanup();
  display.update();
  delay(10);
}