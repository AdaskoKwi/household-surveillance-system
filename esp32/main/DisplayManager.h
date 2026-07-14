#ifndef DISPLAY_MANAGER_H
#define DISPLAY_MANAGER_H

#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <Wire.h>

#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
#define BAR_BITMAP_WIDTH 48
#define BAR_BITMAP_HEIGHT 32
#define LOADING_BAR_WIDTH 38

#define SDA_PIN 20
#define SCL_PIN 21

class DisplayManager {
public:
  DisplayManager();
  bool begin();
  void clear();
  void update();
  void drawStringCentered(const char* buf, int x = SCREEN_WIDTH / 2, int y = SCREEN_HEIGHT / 2);
  void drawBitmapCentered(const unsigned char* bitmap, int x = SCREEN_WIDTH, int y = SCREEN_HEIGHT);
  void showLoadingBar(int progress);
  void waitingForConnection();

  static const unsigned char bitmap_bt[128] PROGMEM;
  static const unsigned char bitmap_wifi[128] PROGMEM;
  static const unsigned char bitmap_loading_bar[192] PROGMEM;
private:
  Adafruit_SSD1306 display;
  GFXcanvas1 canvas;
  unsigned long lastTick;
  int dotCount;
};

#endif