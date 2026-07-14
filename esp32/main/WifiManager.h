#ifndef WIFI_MANAGER_H
#define WIFI_MANAGER_H

#include "WString.h"
#include <esp_wifi.h>
#include <WiFi.h>

class WifiManager {
  public:
    WifiManager();
    void begin();
    int getDeviceCount();
    String getIP();
  private:
    const char* ssid = "ESP32-AP";
    const char* password = "kicia1234";
};

#endif