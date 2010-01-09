#ifndef __RANK_MODEL__
#define __RANK_MODEL__

#include <string>
#include <tree.h>
#include <cart.h>
#include <rank_common.h>

struct GBTree {
  TreeVec treeVec;
  double learningRate, b;
  doubles_t w;
  int nDims;

  void train(const Dataset& trainset, int nDims, const Dataset& testset, const Dataset& valset, const MartParam& martParam, const Metric* m=NULL);
  void test(const Dataset& ds, doubles_t& res) const;
  double getLoss(const Dataset& ds, const doubles_t& res) const;
  void save(const std::string& fn) const;
  void load(const std::string& fn);

  ~GBTree() {
    for(size_t i=0;i<treeVec.size();i++) delete treeVec[i];
  }
};


#endif
