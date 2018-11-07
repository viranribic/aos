cmake_minimum_required(VERSION 3.6)
project(NOS)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES_GAME main_process.cpp)
set(SOURCE_FILES_WHOLE main.cpp)

add_executable(NOS_GAME ${SOURCE_FILES_GAME})
add_executable(NOS2 ${SOURCE_FILES_WHOLE})