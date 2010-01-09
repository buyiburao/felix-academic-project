#ifndef __GBTREE__CART
#define __GBTREE__CART
#include <iostream>
#include <cstdlib>
#include <vector>
#include <algorithm>
#include <ext/hash_set>
#include <ext/hash_map>
#include <functional>
#include "common.h"
#include "tree.h"

struct CartParam {
	int nLeafNodes;
	int minNode;
	int maxDepth;
	double sample;
	double splitRate;
  CartParam() : nLeafNodes(20), minNode(50), maxDepth(9), sample(0.4), splitRate(0.1) {}
  friend std::ostream& operator<<(std::ostream &f, const CartParam &v);  
};

inline std::ostream& operator<<(std::ostream &f, const CartParam &c) {
  return f << "nLeafNodes = " << c.nLeafNodes << "\nminNode = " << c.minNode << "\nmaxDepth = " << c.maxDepth << "\nsample = " << c.sample << "\nsplitRate = " << c.splitRate << std::endl;
}

struct MartParam {
	int debugLevel;
	int nIters;
	double learningRate;
	double samplePrec;
	CartParam cartParam;
  MartParam() : debugLevel(0), nIters(400), learningRate(0.01), samplePrec(0.4) {}
};

typedef std::vector<Node*> TreeVec;	
struct MartModel {
	double learningRate;
	TreeVec treeVec;
};

struct NodeSplit {
	int begin, end, bound;
	double improve;
};

struct Buffer {
  char* buf;
  size_t size;
  
  Buffer(size_t s=0) : size(s) {
    if(size!=0) buf = new char[size]; else buf = NULL;
  }

  ~Buffer() {
    if(buf!=NULL) delete[] buf;
  }

  void resize(size_t newsize) {
    if(buf!=NULL) delete[] buf;
    size = newsize;
    if(newsize!=0) buf = new char[newsize]; else buf = NULL;
  }

  template <typename T> T* get() {
    return (T*)buf;
  }
};

//Node* fit_cart(int data_num, int dim_num, const double* data, const double* target,
//							 const CartParam& cart_param, double* fit_target=NULL);
// w[i] should be positive
//Node* fit_cart(const DataSet& dataset, const double* w, const double* target, const CartParam& cart_param, double *fit_target=NULL, int** buf=NULL);

Node* fit_cart(const FVectors& x, int nDims, const doubles_t& w, const doubles_t& target, const CartParam& cart_param, doubles_t& fit_target, int** buf=NULL);
/*
inline Node* fit_cart(const FVectors& x, int nDims, const doubles_t& w, const doubles_t& target, const CartParam& cart_param, double *fit_target=NULL, int** buf=NULL) {
  DataSet d;
  d.data_num = x.size();
  d.dim_num = nDims;
  d.feature = new double*[d.data_num];
  for(int i=0;i<d.data_num;i++) {
    d.feature[i] = new double[nDims];
    assert(x[i].size()<=nDims);
    std::fill(d.feature[i], d.feature[i]+nDims, 0);
    for(int j=0;j<x[i].size();j++) {
      d.feature[i][j] = x[i][j];
    }
  }
  double *_w = new double[d.data_num], *_target = new double[d.data_num];
  std::copy(w.begin(), w.end(), _w);
  std::copy(target.begin(), target.end(), _target);
  Node* root = fit_cart(d, _w, _target, cart_param, fit_target, buf);
  delete[] _w;
  delete[] _target;
  for(int i=0;i<d.data_num;i++) {
    delete[] d.feature[i];
  }
  delete[] d.feature;
  return root;
}
*/

typedef __gnu_cxx::hash_map<int, int> int2int_t;

struct FeatureIndex {
  FeatureIndex(size_t aDatas = 0, int aDims = 0)  : nDatas(aDatas), nDims(aDims) {
    if(nDatas!=0) ind = new int[aDatas*aDims]; else ind = NULL;
  }

  ~FeatureIndex() {
    if(ind!=NULL) delete[] ind;
  }

  int* getDim(int d) { 
    return ind+d*nDatas;
  }

  const int* getDim(int d) const {
    return ind+d*nDatas;
  }

  FeatureIndex* getSample(const ints_t& sampleList) const;

  size_t nDatas;
  int nDims;
  int* ind;
};

template <typename VecType>
FeatureIndex* buildFeatureIndex(const std::vector<VecType>& x, int nd);

template <typename VecType>
struct FeatureComp : public std::binary_function<int, int, bool> {
  int fid;  
  const std::vector<VecType>& feature;
  FeatureComp(int feature_id, const std::vector<VecType>& f) : fid(feature_id), feature(f) {}
  bool operator()(const int& a, const int& b) const {
    return feature[a][fid]<feature[b][fid];
  }		
};


template <typename VecType1, typename VecType2, typename VecType3, typename VecType4>
class Cart {
  public:
    typedef std::vector<VecType1> Features;

    Cart(const CartParam& param) : cartParam(param) {}
    CartParam cartParam;

    Node* fit(const Features& x, int nd, const VecType2& w, const VecType3& target, VecType4& fit_target, FeatureIndex& buf);

  private:
    void splitNode(const Features& feature, const VecType3& y, const VecType2& weight, int begin, int end, Node& node, NodeSplit& split);   
    int nDatas, nDims, nFeatures;
    ints_t find;
    int *pind;
};

#include <cart_imp.h>

#endif
