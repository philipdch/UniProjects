#ifndef _BLURFITLER
#define _BLURFLITER
#include "Filter.h"

class FilterBlur: public Filter, public Array2D<float>{

	int N;

public:
	virtual Image operator << (const Image& image);

	float getN();

	void setN(float newVal);

	FilterBlur();

	FilterBlur(int blur);

	~FilterBlur();
};
#endif
