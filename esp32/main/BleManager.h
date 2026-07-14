#ifndef BLE_MANAGER_H
#define BLE_MANAGER_H

#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLEDevice.h>
#include <BLE2902.h>

class BleManager;

class ServerCallbacks: public BLEServerCallbacks {
    BleManager* manager;
public:
    ServerCallbacks(BleManager* m) : manager(m) {}
    void onConnect(BLEServer* pServer) override;
    void onDisconnect(BLEServer* pServer) override;
};

class CharacteristicCallbacks: public BLECharacteristicCallbacks {
    BleManager* manager;
public:
    CharacteristicCallbacks(BleManager* m) : manager(m) {}
    void onRead(BLECharacteristic* pCharacteristic) override;
};

class CameraManager;
class DisplayManager;

class BleManager {
  public:
    BleManager(CameraManager* cam, DisplayManager* disp);
    void begin();
    void sendPhoto();
    void bleNotify();

    bool connected;
    bool photoRequested;
    bool isSendingPhoto;
    bool launchedByMotion;
  private:
    BLECharacteristic *pSendPhotoCharacteristic;

    friend class CharacteristicCallbacks;
    friend class ServerCallbacks;

    CameraManager* camera;
    DisplayManager* display;
};

#endif