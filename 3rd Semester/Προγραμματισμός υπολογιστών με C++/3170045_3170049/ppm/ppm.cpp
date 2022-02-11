#include <iostream>
#include <fstream>
#include <string>
#include <istream>
#include <ostream>
#include <bitset>
#include "ppm.h"

using namespace std;
typedef bitset<8> byte;

float* image::ReadPPM(const char* filename, int* w, int* h)
{
	cout << filename << endl;
	ifstream file(filename, ios::binary);
	if(!file){
		cerr << "Cannot open file" << endl;
		return nullptr;
	}
	string type = " ";
	int width = 0;
	int height = 0;
	int maxval = 0;
	file >> type; //read PPM image type
	if (type != "P6") {
		printf("ppm type not supported");
		cout << endl;
		return NULL;
	}
	file >> width >> height >> maxval;
	file.ignore();
	if (width < 0 || height < 0) {
		printf("incorrect image dimensions\n");
		cout << endl;
		return NULL;
	}

	if (maxval > 255) {
		printf(" color depth not supported");
		cout << endl;
		return NULL;
	}

	*w = width;
	*h = height;
	int arraySize = 3 * width * height; // Each pixel in the image consists of three values (r, g, b) thus the size of the array to store its data must be 3*width*height
	float* dataArray = new float[arraySize]; //create array to store image data
	unsigned char* temp = new unsigned char[arraySize]; //temporary array to be given in read(). Array must me of type unsigned char. Otherwise, negative values may be written
	file.read((char*)temp, (sizeof(unsigned char)) * arraySize);

	for (unsigned int i = 0; i < arraySize; i++) {
		dataArray[i] = (((float)(temp[i])) / 255.0f); //convert character values to float and map them to (0,1)
	}
	delete[]temp; //free memory
	file.close();
	return dataArray;
}

bool image::WritePPM(const float* data, int w, int h, const char* filename)
{
	ofstream file(filename, ios::binary);
	if (!file) {
		printf("Error opening file");
		cout << endl;
		return false;
	}
	file << "P6" << " " << w << " " << h << " " << 255 << "\n"; //write PPM type, image dimensions and maxVal to file

	unsigned char* buffer = new unsigned char[3 * w * h];
	for (int i = 0; i < 3 * w * h; i++) {
		buffer[i] = (unsigned char)(data[i] * 255); //convert values in the range [0, 1] to values from 0 - 255
	}
	unsigned int size = 3 * w * h;
	file.write((char*)buffer, size);
	if (file.fail()) {
		cerr << "Error writing to file" << endl;
		return false;
	}
	delete[] buffer; //free memory
	file.close();
	return true;
}
