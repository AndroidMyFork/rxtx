
cmake_minimum_required(VERSION 3.6)
add_library( rxtxSerial
             SHARED
             IMPORTED )
set_target_properties( # Specifies the target library.
                       rxtxSerial

                       # Specifies the parameter you want to define.
                       PROPERTIES IMPORTED_LOCATION

                       # Provides the path to the library you want to import.
                       app/src/${ANDROID_ABI}/librxtxSerialb.so )