# Docker test images

This directory contains Dockerfiles for building docker images that can be used
to test DND. Right now there is only one image useful for testing, but others
will follow.

# Building
There's a script called `build.sh` for building the images in the right order.
However, they can easily be built manually if so desired.


# Images

## dnd/cumulus
Simulates the TeCo cumulus server. It can be reached on port 51525 like the
real on and returns fixed values for the sensors. These values can be changed
by calling the set.py scripts in the sensor directories. Start the image by
calling

    docker run dnd/cumulus

or, if you want to run it in the background:

    docker run -d dnd/cumulus

## dnd/lighttpd
The base for dnd/cumulus. Install lighttpd on top of dnd/universe

## dnd/universe
Docker's default ubuntu base, but with the universe repo enabled.
