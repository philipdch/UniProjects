#ifndef _FILTER
#define _FILTER
#include "image.h"
#include "vec3.h"

using namespace math;
using namespace image;
typedef Vec3<float> Color;

class Filter {

protected:
	Filter() {}
	Filter(Filter& copy){}
	~Filter() {}

public:
	virtual Image operator << (const Image& image) = 0;

};
#endif
