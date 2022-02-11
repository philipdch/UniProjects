#include "filterBlur.h"

Image FilterBlur::operator<<(const Image& image)
{
	Image* tempImage = new Image(image);
	int w = image.getWidth();
	int h = image.getHeight();
	Color blurValue = 0;
	for (int j = 0; j < h; j++) {
		for (int i = 0; i < w; i++) {
			for (int m = -N / 2; m < N / 2; m++) { //first sum loop 
				if ((i + m) >= 0 && (i + m) < w) { //check if 0 <= i + m < w
					for (int n = -N / 2; n < N / 2; n++) { //second sum loop
						if ((j + n) >= 0 && (j + n) < h) { //check if 0 <= j + n < h
							blurValue += (*tempImage)(i + m, j + n) * (*this)(m + N / 2, n + N / 2); 
						}
					}
				}
			}
			(*tempImage)(i, j) = blurValue;
			blurValue = 0;
		}
	}
	return *tempImage;
}

float FilterBlur::getN()
{
	return N;
}

void FilterBlur::setN(float newVal)
{
	N = newVal + 1; //since an odd number is given, it must be converted to an even
	buffer.resize(N * N);
	for (int i = 0; i < pow(N, 2); i++) {
		buffer[i] = 1 / (pow(N, 2));
	}
}

FilterBlur::FilterBlur()
{
	FilterBlur(1);
}

FilterBlur::FilterBlur(int blur)
{
	N = blur + 1;
	Array2D(N, N);
	for (int i = 0; i < pow(N, 2); i++) {
		buffer.push_back(1 / (pow(N, 2)));
	}
}

FilterBlur::~FilterBlur()
{
}


