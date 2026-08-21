cmake_minimum_required(VERSION 3.22.1)

project("starbridge")

add_library(
        starbridge
        SHARED
        native-lib.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        starbridge
        ${log-lib}
)
