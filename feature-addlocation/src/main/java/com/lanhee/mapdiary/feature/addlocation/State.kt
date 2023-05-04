package com.lanhee.mapdiary.feature.addlocation

enum class State {
    STATE_CAM_MOVE, //카메라 움직일 때
    STATE_CAM_IDLE, //카메라 멈췄을 때
    STATE_FAIL,     //지오코딩 실패
    STATE_SUCCESS   //지오코딩 성공
}