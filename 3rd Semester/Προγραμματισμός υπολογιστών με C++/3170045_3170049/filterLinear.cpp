#include "filterLinear.h"

Image FilterLinear::operator<<(const Image& image)
{
	Image* tempImage = new Image(image);
	for (int j = 0; j < image.getHeight(); j++){
		for (int i = 0; i < image.getWidth(); i++) {
			(*tempImage)(i, j) = (*tempImage)(i, j) * a + c;  //p'(i,j) = p(i, j)*a + c
			//Normalise values ([0, 1])
			(*tempImage)(i, j) = (*tempImage)(i, j).clampToLowerBound(0.0f);
			(*tempImage)(i, j) = (*tempImage)(i, j).clampToUpperBound(1.0f);
		}
	}
	return *tempImage;
}

Color FilterLinear::getA()
{
	return a;
}

Color FilterLinear::getC()
{
	return c;
}

void FilterLinear::setValues(Color valA, Color valC)
{
	this->a = valA;
	this->c = valC;
}

FilterLinear::FilterLinear()
{
	FilterLinear(Color(0.0f), Color(0.0f));
}

FilterLinear::FilterLinear(Color a, Color c) {
	setValues(a, c);
}

FilterLinear::FilterLinear(float a, float c) {
	setValues(Color(a), Color(c));
}

FilterLinear::~FilterLinear()
{
}
