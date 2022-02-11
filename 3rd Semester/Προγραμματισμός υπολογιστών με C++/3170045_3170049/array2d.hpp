#include <stdio.h>
#include <iostream>
#include <string>
#include <stdexcept>
#include "array2d.h"
#include "Vec3.h"


using namespace std;
using namespace math;
template<typename T>
inline const unsigned int math::Array2D<T>::getWidth() const
{
	return width;
}

template<typename T>
inline const unsigned int math::Array2D<T>::getHeight() const
{
	return height;
}

template<typename T>
inline T* Array2D<T>::getRawDataPtr()
{
	return &buffer;
}

template<typename T>
inline void Array2D<T>::setData(const T* const& data_ptr)
{
	if (width == 0 || height == 0)
		return;
	buffer.clear();
	for (unsigned int i = 0; i < width * height; i++) {
		const T item = data_ptr[i];
		buffer.push_back(item);
	}
	std::cout << "buffer size: " << buffer.size() << endl;
	return;
}

template<typename T>
inline T& Array2D<T>::operator()(unsigned int x, unsigned int y)
{
	// TODO: insert return statement here
	try {
		return buffer.at( y*width + x);
	}
	catch(out_of_range &ex){
		cerr<< "Index out of range!" << ex.what() << endl;
	}
}

template<typename T>
inline Array2D<T>::Array2D(unsigned int width, unsigned int height, const T* data_ptr)
{
	this->width = width;
	this->height = height;
	this->buffer.resize(width * height);
	for (unsigned int i = 0; i < width * height; i++) {
		buffer.push_back(0);
	}
	if(data_ptr!= nullptr)
		setData(data_ptr);
}

template<typename T>
inline Array2D<T>::Array2D(const Array2D& src)
{
	width = src.width;
	height = src.height;
	buffer = src.buffer;
}

template<typename T>
inline Array2D<T>::~Array2D(){}


template <typename T> 
Array2D<T>& Array2D<T>::operator = (const Array2D<T>& right)
{
	width = right.width;
	height = right.height;
	buffer.clear();
	for (int i = 0; i < width * height; i++) {
		buffer.push_back(right.buffer[i]);
	}
	return *this;
}



