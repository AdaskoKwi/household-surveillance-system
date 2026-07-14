#include "esp_camera.h"
#ifndef CAMERA_MANAGER_H
#define CAMERA_MANAGER_H

#include <esp_camera.h>

class CameraManager {
  public:
    CameraManager();
    bool init();
    camera_fb_t* getFrame();
    void returnFrame(camera_fb_t* fb);
};

#endif