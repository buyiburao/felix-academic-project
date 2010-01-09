#ifndef __GBTREE__COMMON
#define __GBTREE__COMMON

#include <sstream>
#include <iostream>
#include <fstream>
#include <iomanip>
#include <string>

#include <vectors.h>
#include <matrices.h>
#include <vector>

#include <ext/hash_set>
#include <ext/hash_map>


#include <boost/algorithm/string.hpp>
#include <boost/iostreams/categories.hpp>
#include <boost/iostreams/device/file.hpp> 
#include <boost/functional/hash.hpp>
#include <boost/iostreams/filtering_stream.hpp>
#include <boost/iostreams/copy.hpp>
#include <boost/iostreams/filter/bzip2.hpp>
#include <boost/iostreams/filter/gzip.hpp>

const double eps=1e-12;
static inline int dcmp(double x) {
	return x<-eps?-1:x>eps;
}

template <typename T>
static inline T sqr(T a) {
  return a*a;
}

template <class T>
inline bool from_string(T& t, const std::string& s, std::ios_base& (*f)(std::ios_base&) = std::dec)
{
  std::istringstream iss(s);  
  return (iss >> f >> t);
}


struct DataSet {
	double **feature;
	int data_num;
	int dim_num;

};

typedef std::vector<FVector> FVectors;
typedef std::vector<SVector> SVectors;
typedef std::vector<double> doubles_t;
typedef std::vector<int> ints_t;
typedef __gnu_cxx::hash_set<int> intset_t;
typedef __gnu_cxx::hash_map<int, int> int2int_t;

typedef std::pair<int, int> int_pair;
typedef std::vector<int_pair> int_pairs_t;
typedef __gnu_cxx::hash_map<int_pair, int, boost::hash<int_pair> > intp2int_t;


inline void build_stream(boost::iostreams::filtering_istream& stream,
     const std::string& file) {
  if(boost::ends_with(file, ".gz")  || boost::ends_with(file, ".bz2")) {
//    file_stream.exceptions(std::ios_base::badbit | std::ios_base::failbit);
    if (boost::ends_with(file,".gz")) {
      stream.push(boost::iostreams::gzip_decompressor());
    } 
    if(boost::ends_with(file, ".bz2")) {
      stream.push(boost::iostreams::bzip2_decompressor());    
    }
    stream.push(boost::iostreams::file_source(file, std::ios_base::in|std::ios_base::binary));
  } else {
    stream.push(boost::iostreams::file_source(file));
  }
}

inline void build_stream(boost::iostreams::filtering_ostream& stream,
     const std::string& file) {
  if(boost::ends_with(file, ".gz")  || boost::ends_with(file, ".bz2")) {
//    file_stream.open(file.c_str(), std::ios_base::out|std::ios_base::binary);
//    file_stream.exceptions(std::ios_base::badbit | std::ios_base::failbit);
    if (boost::ends_with(file,".gz")) {
      stream.push(boost::iostreams::gzip_compressor());
    } 
    if(boost::ends_with(file, ".bz2")) {
      stream.push(boost::iostreams::bzip2_compressor());    
    }
    stream.push(boost::iostreams::file_sink(file, std::ios_base::out|std::ios_base::binary));
  } else {
    stream.push(boost::iostreams::file_sink(file));
  }
}




#endif
