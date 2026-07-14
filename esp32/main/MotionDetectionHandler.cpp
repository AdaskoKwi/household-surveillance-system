#include "esp32-hal-gpio.h"
#include "MotionDetectionHandler.h"
#include "ServerManager.h"
#include "BleManager.h"

#define MOTION_SENSOR_PIN 19

MotionDetectionHandler::MotionDetectionHandler(ServerManager* serv, BleManager* bt): lastState(LOW), server(serv), bluetooth(bt) {}

void MotionDetectionHandler::begin() {
  pinMode(MOTION_SENSOR_PIN, INPUT);
}

void MotionDetectionHandler::handleMotionDetectionWiFi() {
  auto motionState = digitalRead(MOTION_SENSOR_PIN);

  if (motionState == HIGH && lastState == LOW) {
    server->wsNotify();
    lastState = HIGH;
  } else if (motionState == LOW) {
    lastState = LOW;
  }
}

void MotionDetectionHandler::handleMotionDetectionBLE() {
  auto motionState = digitalRead(MOTION_SENSOR_PIN);

  if (motionState == HIGH && lastState == LOW) {
    bluetooth->launchedByMotion = true;

    bluetooth->bleNotify();
    bluetooth->sendPhoto();
    
    bluetooth->launchedByMotion = false;
    lastState = HIGH;
  } else if (motionState == LOW) {
    lastState = LOW;
  }

  if (bluetooth->photoRequested) {
    delay(500);
    bluetooth->sendPhoto();
  }
}