Project GrIGE
=============
Project GrIGE is my "Greatly Improved Game Engine", the successor to an attempt that I made a couple years back at writing a simple Java game engine.
Hopefully this one turns out to be actually useful.  
It is very much a work in progress.

Currently Supported Functionality
---------------------------------
* Rendering of any reasonable number of textured sprites
* Full 2D lighting (supporting point, directional, and spot lights) with hard shadows
* Sprite normal and self-illumination mapping
* Simultaneous playing of multiple audio sources
* Keyboard and mouse input polling
* Easy camera movement
* Object Selection/Picking based on screen position
* Basic text rendering
* Sprite animation (using SpriteSheetPacker)
* Full, interactive UI support via the Nifty library

Current TODO
-----------------------------------
* Fix objects casting shadows even if they're hidden (e.g if they get rendered behind a larger object)
* Fix the openal startup crash
* Render sprites back to front
* Disconnect updating and rendering

Yet-to-be-(maybe)-implemented Functionality
-----------------------------------
* Soft shadows
* Basic particles
* Pixel-perfect collision detection
* Basic multiplayer/network support
