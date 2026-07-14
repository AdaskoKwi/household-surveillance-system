#include "WString.h"
#include "BleManager.h"
#include "CameraManager.h"
#include "DisplayManager.h"

#define LOADING_BAR_WIDTH 38

#define MAX_MTU 256

#define SERVICE_UUID "4fafc201-1fb5-459e-8fcc-c5c9c331914b"
#define SEND_PHOTO_CHARACTERISTIC_UUID "68ef2e0a-1227-421e-b7a9-3c5816c74c62"

void ServerCallbacks::onConnect(BLEServer* pServer) {
  manager->connected = true;
}

void ServerCallbacks::onDisconnect(BLEServer* pServer) {
  manager->connected = false;
  BLEDevice::startAdvertising();
}

void CharacteristicCallbacks::onRead(BLECharacteristic* pCharacteristic) {
  manager->photoRequested = true;
}

BleManager::BleManager(CameraManager* cam, DisplayManager* disp): pSendPhotoCharacteristic(nullptr), connected(false), photoRequested(false), isSendingPhoto(false), launchedByMotion(false), camera(cam), display(disp) {}

void BleManager::begin() {
  BLEDevice::init("My_ESP32");
  BLEDevice::setMTU(MAX_MTU);

  BLEServer *pServer = BLEDevice::createServer();
  pServer->setCallbacks(new ServerCallbacks(this));

  BLEService *pService = pServer->createService(SERVICE_UUID);

  BLECharacteristic *sendPhotoCharacteristic = pService->createCharacteristic(
    SEND_PHOTO_CHARACTERISTIC_UUID,
    BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY
  );

  sendPhotoCharacteristic->addDescriptor(new BLE2902());
  sendPhotoCharacteristic->setCallbacks(new CharacteristicCallbacks(this));

  pSendPhotoCharacteristic = sendPhotoCharacteristic;

  pService->start();

  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();

  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(true);
  pAdvertising->setMinPreferred(0x06);

  BLEDevice::startAdvertising();
}

void BleManager::sendPhoto() {
  isSendingPhoto = true;
  photoRequested = false;

  display->clear();
  display->drawStringCentered("Polaczono przez BLE", 64, 20);
  display->update();

  camera_fb_t *fb = camera->getFrame();

  if (!fb) {
    isSendingPhoto = false;
    return;
  }

  if (!launchedByMotion) {
    pSendPhotoCharacteristic->setValue("Photo requested - no motion detected");
    pSendPhotoCharacteristic->notify();
  }

  uint8_t *photoData = fb->buf;
  size_t photoSize = fb->len;

  String sizeHeader = "Photo size: " + String(photoSize);

  pSendPhotoCharacteristic->setValue(sizeHeader.c_str());
  pSendPhotoCharacteristic->notify();

  int chunkSize = 250;

  int counter = 0;
  int chunksCount = photoSize / chunkSize;

  for (int i = 0; i < photoSize; i += chunkSize) {
    size_t bytesToSend = (photoSize - i < chunkSize) ? (photoSize - i) : chunkSize;

    pSendPhotoCharacteristic->setValue(&photoData[i], bytesToSend);
    pSendPhotoCharacteristic->notify();

    if (counter % 10 == 0) {
      int progress = LOADING_BAR_WIDTH * counter / chunksCount;

      display->showLoadingBar(progress);
    }

    counter++;
    delay(50);
  }

  display->clear();
  display->drawStringCentered("Polaczono przez BLE", 64, 20);
  display->drawStringCentered("Sukces!", 64, 45);
  display->update();

  delay(500);

  camera->returnFrame(fb);

  isSendingPhoto = false;
}

void BleManager::bleNotify() {
  pSendPhotoCharacteristic->setValue("Motion");
  pSendPhotoCharacteristic->notify();
}