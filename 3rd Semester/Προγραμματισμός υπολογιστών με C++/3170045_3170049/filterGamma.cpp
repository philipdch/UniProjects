#include "filterGamma.h"

Image FilterGamma::operator<<(const Image& image)
{
	Image* tempImage = new Image(image);
	for (int j = 0; j < image.getHeight(); j++) {
		for (int i = 0; i < image.getWidth(); i++) {
			//p'(i, j) = p(i, j)^ã
			//each value is raised separately since a Color object cannot be given in pow()
			(*tempImage)(i, j).r = pow((*tempImage)(i, j).r, gamma);
			(*tempImage)(i, j).g = pow((*tempImage)(i, j).g, gamma);
			(*tempImage)(i, j).b = pow((*tempImage)(i, j).b, gamma);
			//Normalise values ([0, 1])
			(*tempImage)(i, j) = (*tempImage)(i, j).clampToLowerBound(0.0f);
			(*tempImage)(i, j) = (*tempImage)(i, j).clampToUpperBound(1.0f);
		}
	}
	return *tempImage;
}

void FilterGamma::setGamma(float newVal)
{
	gamma = newVal;
}

float FilterGamma::getGamma()
{
	return gamma;
}

FilterGamma::FilterGamma()
{
	FilterGamma(0.0);
}

FilterGamma::FilterGamma(float g)
{
	gamma = g;
}

FilterGamma::~FilterGamma()
{
}
