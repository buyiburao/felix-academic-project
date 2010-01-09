#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <algorithm>
#include <cmath>
#include <limits>
#include <rank_common.h>
#include <rank_gbtree.h>
#include <tree.h>
#include <cart.h>


using namespace std;
///////////////////////////////////////////
// GBTree
///////////////////////////////////////////

void GBTree::train(const Dataset& trainset, int nDims, const Dataset& testset, const Dataset& valset, const MartParam& martParam, const Metric* m) {
  this->nDims = nDims;
  this->learningRate = martParam.learningRate;

  doubles_t residues(trainset.size());  
  ints_t ind(trainset.size());
  size_t nSamples = static_cast<size_t>(floor(trainset.size()*martParam.samplePrec+eps));
  if(nSamples>trainset.size()) nSamples = trainset.size();
  FVectors x(nSamples), tx(trainset.size());
  for(size_t i=0;i<trainset.size();i++) tx[i] = trainset[i].x;
  doubles_t weight(nSamples), y(nSamples), yhat(trainset.size()), ty(testset.size()), vy(valset.size()), fity(nSamples);

  b = 0.0;
  for(size_t i=0;i<trainset.size();i++) b+= trainset[i].y;
  b/= trainset.size();
  for(size_t i=0;i<trainset.size();i++) residues[i] = trainset[i].y-b;
  for(size_t i=0;i<ind.size();i++) ind[i] = i;

  double bestT = -numeric_limits<double>::max();
  double bestV = -numeric_limits<double>::max();
  double bestTV = -numeric_limits<double>::max();

  Cart<FVector, doubles_t, doubles_t, doubles_t> cart(martParam.cartParam);
  FeatureIndex *find = buildFeatureIndex(tx, nDims);
  for(int iter=0;iter<martParam.nIters;iter++) {
    random_shuffle(ind.begin(), ind.end());
    for(size_t i=0;i<nSamples;i++) {
      x[i] = trainset[ind[i]].x;
      y[i] = residues[ind[i]];
      weight[i] = 1.0;      
//      if(dcmp(y[i]-1)==0) weight[i] = 1.0; else weight[i] = 1e-2;
    }
    FeatureIndex *sampleInd = find->getSample(ints_t(ind.begin(), ind.begin()+nSamples));
#if 0
  	for(int p=0;p<nDims;p++) {
  		FeatureComp<FVector> fc(p, x), fc2(p, tx);     
      const int* pindAll = find->getDim(p);
      assert(__gnu_cxx::is_sorted(pindAll, pindAll+tx.size(), fc2));
      
      cout << "p = " << p << endl;
      const int* pind = sampleInd->getDim(p);

      cout << endl;
      for(size_t i=1;i<nSamples;i++) {
        if(fc(pind[i], pind[i-1])) {
          for(size_t j=0;j<tx.size();j++) {
            if(pindAll[j]==ind[pind[i-1]]) {
              cout << " " << pindAll[j] << " " << pind[i-1] << " " << tx[pindAll[j]][p] << endl;
            }
            if(pindAll[j]==ind[pind[i]]) {
              cout << " " << pindAll[j] << " " << pind[i] << " " << tx[pindAll[j]][p] << endl;
            }
          }
          cout << "i = " << i << " " << pind[i-1] << " " << pind[i] << " " << x[pind[i-1]][p] << " " << x[pind[i]][p] << endl;
        }
      }
   		assert(__gnu_cxx::is_sorted(pind, pind+nSamples, fc));	
    }
#endif
    treeVec.push_back(cart.fit(x, nDims, weight, y, fity, *sampleInd));
    delete sampleInd;
    double w1 = 0.0, w2 = 0.0;
    for(size_t i=0;i<trainset.size();i++) {
      yhat[i] = apply_cart(trainset[i].x, treeVec[iter]);
      w1 += yhat[i]*residues[i]; w2 += yhat[i]*yhat[i];
    }
    assert(dcmp(w2)>0);
    w.push_back(w1/w2);
//    w.push_back(1.0);
//    cout << w1/w2 << endl;
    for(size_t i=0;i<trainset.size();i++) {
      residues[i]-=yhat[i]*learningRate*w[iter];
      yhat[i] = trainset[i].y-residues[i];
    }
   
    if(iter%10==0) {
      test(trainset, yhat);
//      doubles_t ndcg, ndcgT, ndcgV;
//      cal_ndcg(trainset, yhat, ndcg);
      double mtrain = m->evaluate(trainset, yhat);
      cout << "Iter = " << iter << " Loss = " << getLoss(trainset, yhat) << " NDCG5 = " << mtrain; 
      if(!testset.empty()) {
        test(testset, ty);
//        cal_ndcg(testset, ty, ndcgT);
        double mtest = m->evaluate(testset, ty);
        cout << " TLoss = " << getLoss(testset, ty) << " TNDCG5 = " << mtest; 
        if(iter>100 && dcmp(bestT-mtest)<0) bestT = mtest;
        if(!valset.empty()) {
          test(valset, vy);
//          cal_ndcg(valset, vy, ndcgV);
          double mval = m->evaluate(valset, vy);
          cout << " VLoss = " << getLoss(valset, vy) << " VNDCG5 = " << mval; 
          if(iter>100 && dcmp(bestV-mval)<0) {
            bestV = mval; bestTV =  mtest;
          }
        }
      }
      cout << endl;
    }
  }
  delete find;
  cout << "BestTest = " << bestT << endl;
  cout << "BestVal = " << bestV << endl;
  cout << "BestTestonVal = " << bestTV << endl;
}

void GBTree::test(const Dataset& ds, doubles_t& res) const {
  res.resize(ds.size());
  fill(res.begin(),res.end(), b);
  for(size_t t=0;t<treeVec.size();t++) {
    for(size_t i=0;i<ds.size();i++) {
      res[i] += learningRate*apply_cart(ds[i].x, treeVec[t])*w[t];
    }
  }   
}

double GBTree::getLoss(const Dataset& ds, const doubles_t& res) const {
  double loss = 0.0;
  for(size_t i=0;i<ds.size();i++) {
    loss += (res[i]-ds[i].y)*(res[i]-ds[i].y);    
//    if(dcmp(ds[i].y-1)==0) loss += (res[i]-ds[i].y)*(res[i]-ds[i].y);
//    else loss += 1e-2*(res[i]-ds[i].y)*(res[i]-ds[i].y);
  }
  return loss;
//  return loss/=ds.size();
}

void GBTree::save(const string& fn) const {
  ofstream f(fn.c_str());
  f << nDims << "\n" << learningRate << "\n" << b << treeVec.size() << endl;
  for(size_t i=0;i<w.size();i++) f << w[i] << " ";
  f << endl;
  for(size_t i=0;i<treeVec.size();i++) {
    save_tree(f, treeVec[i]);
  }
  f.close();
}

void GBTree::load(const string& fn) {
  ifstream f(fn.c_str());
  size_t nTrees;
  assert(f);
  f >> nDims >> learningRate >> b >> nTrees;  
  w.resize(nTrees);
  for(size_t i=0;i<w.size();i++) f >> w[i];
  treeVec.resize(nTrees);
  for(size_t i=0;i<treeVec.size();i++) {
    treeVec[i] = load_tree(f);
    assert(treeVec[i]!=NULL);
  }
  f.close();
}

