#ifndef _LINERAFILTER
#define _LINEARFILTER
#include "Filter.h"
#include "array2d.h"

class FilterLinear :public Filter {

	Color a;
	Color c;
public: 
	virtual Image operator << (const Image& image);

	Color getA();

	Color getC();

	void setValues(Color valA, Color valC);

	FilterLinear();

	FilterLinear(Color a, Color c);

	FilterLinear(float a, float c);

	~FilterLinear();
	
};
#endif