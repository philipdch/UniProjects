#ifndef _GAMMAFILTER
#define _GAMMAFILTER
#include "Filter.h"
#include "array2d.h"

class FilterGamma :public Filter {
	
	float gamma;

public:
	virtual Image operator << (const Image& image);
	 
	void setGamma(float newVal); 

	float getGamma();

	FilterGamma();

	FilterGamma(float g);

	~FilterGamma();

};
#endif