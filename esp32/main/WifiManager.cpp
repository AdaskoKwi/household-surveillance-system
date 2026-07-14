#include "WString.h"
#include "WifiManager.h"

WifiManager::WifiManager() {}

void WifiManager::begin() {
  WiFi.softAP(ssid, password);
}

int WifiManager::getDeviceCount() {
  wifi_sta_list_t stationList;
  esp_wifi_ap_get_sta_list(&stationList);

  return stationList.num;
}

String WifiManager::getIP() {
  return WiFi.softAPIP().toString();
}