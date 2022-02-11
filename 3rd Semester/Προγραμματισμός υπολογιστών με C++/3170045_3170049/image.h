#ifndef _IMAGE
#define _IMAGE
#include <string>
#include <iostream>
#include <array>
#include "vec3.h"
#include "imageio.h"
#include "ppm/ppm.h"
#include "array2d.h"

using namespace image;
using namespace math;

typedef math::Vec3<float> Color;

class Image : public Array2D<Color>, public ImageIO{
public:

	Image() {
		Array2D(0, 0);
	}

	bool ImageIO::load(const std::string& filename, const std::string& format) {
		std::string temp = "";
		buffer.clear();
		for (const char ch : format) { //convert string to uppercase 
			temp += toupper(ch);
		}
		if (temp != "PPM") {
			printf("Unsupported file format\n");
			return false;
		}

		int w = 0, h = 0;
		float* data = ReadPPM(filename.c_str(), &w, &h); //get float array with every value in this image (values are in range [0, 1])
		if (data == nullptr) {
			return false;
		}
		this->width = w;
		this->height = h;
		this->buffer.resize(w * h); 
		for (unsigned int i = 0; i < w * h; i++) { //each pixel consists of three r g b values
			Color* colour = new Color(); //create colour which consists of these three values
			colour->r = data[3 * i];
			colour->g = data[3 * i + 1];
			colour->b = data[3 * i + 2];
			this->buffer[i] = *colour; //insert colour in buffer
		}
		//now the buffer contains every "pixel"/colour in the image
		delete[] data; //free memory 
		return true;
	}

	bool ImageIO::save(const std::string& filename, const std::string& format) {
		std::string temp = "";
		for (const char& ch : format) { //convert string to uppercase
			temp += toupper(ch);
		}
		if (temp != "PPM") {
			printf("Unsupported file format\n");
			return false;
		}

		if (this->width == 0 || this->height == 0) {
			return false;
		}
		unsigned int arraySize = 3 * buffer.size(); //in the file each colour is represented by three r g b values. Since the buffer stores colours, the size of the array to be written to the file is 3 * (buffer size)
		float* rgbArray = new float[arraySize];
		for (unsigned int i = 0; i < buffer.size(); i++) {
			Color *color = new Color();
			*color = buffer[i]; //break colour up to get these values
			rgbArray[i * 3] = color->r;
			rgbArray[i * 3 + 1] = color->g;
			rgbArray[i * 3 + 2] = color->b;
		}
		bool writeSuccess = false;
		writeSuccess = WritePPM(rgbArray, this->width, this->height, filename.c_str());
		delete[] rgbArray;
		return writeSuccess;
	}
};
#endif