#include <string>
#include <iostream>
#include "Vec3.h"
#include "image.h"
#include "filterLinear.h"
#include "filterGamma.h"
#include "filterBlur.h"
#include "ppm/ppm.h"

using namespace std;
using namespace image;
using namespace math;

int main(int argc, char* argv[]) {
	string filename = "";
	if (argc < 5) {// 5 = minimum amount of arguments required (ex -f gamma 2 \image06.ppm)
		cout << "Incorrect number of arguments!\nEither a filter or a valid file path was not given" << endl;
		return 0;
	}
	filename = argv[argc - 1];
	bool image_loaded = false;
	Image* newImage = new Image(); //create image object. Will be used to store the image data 
	image_loaded = newImage->load(filename, "ppm"); //load image 
	if (image_loaded == true) {
		cout << "Image width :" << newImage->getWidth() << endl;
		cout << "Image height : " << newImage->getHeight() << endl;
	}
	else {
		return 0;
	}
	string filter;
	string id = argv[1];
	int i = 1;
	FilterLinear *linearFilter = new FilterLinear();
	FilterGamma *gammaFilter = new FilterGamma();
	FilterBlur* blurFilter = new FilterBlur();
	int filterCount = 0;
	while (id == "-f") {
		filter = argv[++i];
		for (int ch = 0; ch < filter.size(); ch++) {
			filter[ch] = tolower(filter.at(ch));
		}
		if (filter == "linear") {
			Color* a = new Color();
			Color* c = new Color();
			try {
				a->r = stof(argv[++i]);
				a->g = stof(argv[++i]);
				a->b = stof(argv[++i]);
				c->r = stof(argv[++i]);
				c->g = stof(argv[++i]);
				c->b = stof(argv[++i]);
			}
			catch (invalid_argument & e) {
				cout << "The parameter given does not represent a float" << endl;
				return 0;
			}
			linearFilter = new FilterLinear(*a, *c);
			*newImage = (*linearFilter) << (*newImage);
		}
		else if (filter == "gamma") {
			float g = stof(argv[++i]);
			gammaFilter->setGamma(g);
			*newImage = (*gammaFilter) << (*newImage);
		}
		else if(filter == "blur"){
			float n = stof(argv[++i]);
			blurFilter->setN(n);
			*newImage = (*blurFilter) << (*newImage);
		}
		else {
			if (filterCount == 0) {
				cout << "No filter specified" << endl;
			}
			else {
				cout << "Incorrect filter name" << endl;
			}
			return 0;
		}
		filterCount++;
		id = argv[++i];
	}
	if (filterCount == 0) {
		cout << "No filter specified" << endl;
		return 0;
	}
	string newName = "filtered_" + filename.substr(filename.find_last_of('\\')+1);
	newName = filename.substr(0, filename.find_last_of('\\')+1) + newName;
	cout << "new File Name: " << newName << endl;
	bool image_saved = newImage->save(newName, "ppm"); //Create a new file with the negative of the image given
	delete blurFilter;
	delete linearFilter;
	delete gammaFilter;
	delete newImage;
	return 0;

}
