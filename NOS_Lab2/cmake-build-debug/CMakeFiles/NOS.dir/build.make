# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.6

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /opt/programs/programing-tools/clion/clion-2016.3.3/bin/cmake/bin/cmake

# The command to remove a file.
RM = /opt/programs/programing-tools/clion/clion-2016.3.3/bin/cmake/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/vribic/CLionProjects/NOS_Lab2

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/vribic/CLionProjects/NOS_Lab2/cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/NOS.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/NOS.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/NOS.dir/flags.make

CMakeFiles/NOS.dir/main.cpp.o: CMakeFiles/NOS.dir/flags.make
CMakeFiles/NOS.dir/main.cpp.o: ../main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/vribic/CLionProjects/NOS_Lab2/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/NOS.dir/main.cpp.o"
	/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/NOS.dir/main.cpp.o -c /home/vribic/CLionProjects/NOS_Lab2/main.cpp

CMakeFiles/NOS.dir/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/NOS.dir/main.cpp.i"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/vribic/CLionProjects/NOS_Lab2/main.cpp > CMakeFiles/NOS.dir/main.cpp.i

CMakeFiles/NOS.dir/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/NOS.dir/main.cpp.s"
	/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/vribic/CLionProjects/NOS_Lab2/main.cpp -o CMakeFiles/NOS.dir/main.cpp.s

CMakeFiles/NOS.dir/main.cpp.o.requires:

.PHONY : CMakeFiles/NOS.dir/main.cpp.o.requires

CMakeFiles/NOS.dir/main.cpp.o.provides: CMakeFiles/NOS.dir/main.cpp.o.requires
	$(MAKE) -f CMakeFiles/NOS.dir/build.make CMakeFiles/NOS.dir/main.cpp.o.provides.build
.PHONY : CMakeFiles/NOS.dir/main.cpp.o.provides

CMakeFiles/NOS.dir/main.cpp.o.provides.build: CMakeFiles/NOS.dir/main.cpp.o


# Object files for target NOS
NOS_OBJECTS = \
"CMakeFiles/NOS.dir/main.cpp.o"

# External object files for target NOS
NOS_EXTERNAL_OBJECTS =

NOS: CMakeFiles/NOS.dir/main.cpp.o
NOS: CMakeFiles/NOS.dir/build.make
NOS: CMakeFiles/NOS.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/vribic/CLionProjects/NOS_Lab2/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable NOS"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/NOS.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/NOS.dir/build: NOS

.PHONY : CMakeFiles/NOS.dir/build

CMakeFiles/NOS.dir/requires: CMakeFiles/NOS.dir/main.cpp.o.requires

.PHONY : CMakeFiles/NOS.dir/requires

CMakeFiles/NOS.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/NOS.dir/cmake_clean.cmake
.PHONY : CMakeFiles/NOS.dir/clean

CMakeFiles/NOS.dir/depend:
	cd /home/vribic/CLionProjects/NOS_Lab2/cmake-build-debug && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/vribic/CLionProjects/NOS_Lab2 /home/vribic/CLionProjects/NOS_Lab2 /home/vribic/CLionProjects/NOS_Lab2/cmake-build-debug /home/vribic/CLionProjects/NOS_Lab2/cmake-build-debug /home/vribic/CLionProjects/NOS_Lab2/cmake-build-debug/CMakeFiles/NOS.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/NOS.dir/depend

