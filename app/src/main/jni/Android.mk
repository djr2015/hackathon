#
# Copyright 2014 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)
PROJECT_ROOT_FROM_JNI:= ../../../../..
PROJECT_ROOT:= $(call my-dir)/../../../../..

LIBRARY_PATH:= $(call my-dir)/../../../libraries
LIBRARY_PATH_FROM_JNI:= ../../../libraries

include $(CLEAR_VARS)
LOCAL_MODULE    := libpoint_cloud_jni_example
LOCAL_SHARED_LIBRARIES := tango_client_api
LOCAL_CFLAGS    := -std=c++11

LOCAL_C_INCLUDES := $(LIBRARY_PATH)/tango_support_api/include/ \
                    $(LIBRARY_PATH)/tango-gl/include \
                    $(LIBRARY_PATH)/third-party/glm/

LOCAL_SRC_FILES := jni_interface.cc \
                   point_cloud_data.cc \
                   point_cloud_drawable.cc \
                   point_cloud_app.cc \
                   pose_data.cc \
                   scene.cc \
                   tango_event_data.cc \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/axis.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/camera.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/conversions.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/drawable_object.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/frustum.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/gesture_camera.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/grid.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/line.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/shaders.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/trace.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/transform.cpp \
                   $(LIBRARY_PATH_FROM_JNI)/tango-gl/util.cpp

LOCAL_LDLIBS    := -llog -lGLESv2 -L$(SYSROOT)/usr/lib
include $(BUILD_SHARED_LIBRARY)

$(call import-add-path, $(LIBRARY_PATH))
$(call import-module,tango_client_api)
