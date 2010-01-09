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
/*
struct GBTree {
  TreeVec treeVec;
  double learningRate;
  doubles_t w;
  int nDims;

  void train(const Dataset& trainset, int nDims, const Dataset& testset, const Dataset& valset, const MartParam& martParam);
  void test(const Dataset& ds, doubles_t& res) const;
  double getLoss(const Dataset& ds, const doubles_t& res) const;
  void save(const string& fn) const;
  void load(const string& fn);
};
*/

Dataset trainset, testset, valset;
string trainfn, testfn, modelfn, valfn, trainfn2, resfn;
MartParam martParam;
int task;
GBTree model;
Metric* m=NULL;

int parseArgs(int argc, char* argv[]) {
  if(strcmp(argv[1], "-t")==0) {
    task = 1;
    testfn = argv[2];
    modelfn = argv[3];    
    resfn = argv[4];
    if(argc>=6) valfn = argv[5];
    return 0;
  }
  if(strcmp(argv[1], "-a")==0) {
    task = 2;
    modelfn = argv[2];
    return 0;
  }

  trainfn = argv[1];
  modelfn = argv[2];
  martParam.learningRate = 0.01;
  martParam.nIters = 400;
  martParam.samplePrec = 0.4;
  martParam.cartParam.nLeafNodes = 20;
  martParam.cartParam.minNode = 5;
  martParam.cartParam.maxDepth = 10;
  martParam.cartParam.sample = 1.0;
  martParam.cartParam.splitRate = 0.1;
  string metric;

  for(int i=3;i<argc;i++) {
    if(strcmp("-lr", argv[i])==0) {
      assert(from_string<double>(martParam.learningRate, argv[++i]));
      continue;
    }
    if(strcmp("-iter", argv[i])==0) {
      assert(from_string<int>(martParam.nIters, argv[++i]));
      continue;
    }
    if(strcmp("-sample", argv[i])==0) {
      assert(from_string<double>(martParam.samplePrec, argv[++i]));
      continue;
    }
    if(strcmp("-size", argv[i])==0) {
      assert(from_string<int>(martParam.cartParam.nLeafNodes, argv[++i]));
      continue;
    }
    if(strcmp("-sr", argv[i])==0) {
      assert(from_string<double>(martParam.cartParam.splitRate, argv[++i]));
      continue;
    }
    if(strcmp("-depth", argv[i])==0) {
      assert(from_string<int>(martParam.cartParam.maxDepth, argv[++i]));
      continue;
    }
    if(strcmp("-minNode", argv[i])==0) {
      assert(from_string<int>(martParam.cartParam.minNode, argv[++i]));
      continue;      
    }
    if(strcmp("-csample", argv[i])==0) {
      assert(from_string<double>(martParam.cartParam.sample, argv[++i]));
      continue;
    }
    if(strcmp("-train2", argv[i])==0) {
      trainfn2 = argv[++i];
      continue;
    }
    if(strcmp("-t", argv[i])==0) {
      testfn = argv[++i];
      continue;
    }
    if(strcmp("-v", argv[i])==0) {
      valfn = argv[++i];
      continue;
    }
    if(strcmp("-metric", argv[i])==0) {
      metric = argv[++i];
      continue;
    }
    cout << "Unknow Param: " << argv[i] << endl;
    return -1;
  }

  if(metric.compare("dcg")==0) {
    m = new DcgMetric<GalaxyGain, GalaxyDiscount>(5);
  } else if(metric.compare("ndcg")==0) {
    m = new NdcgMetric<>(5);
  } else {
    m = new MSError();
  }
  return 0;
}

int main(int argc, char* argv[]) {
  if(parseArgs(argc, argv)!=0) {
    return -1;
  }

  if(task==1) { // test
    model.load(modelfn);

    int nDims = loadDataset(testfn, testset);
    if(nDims<0) {
      cout << "Error Read testing data: " << testfn << endl;
      return -1;
    }
    cout << "Test Dimension:\t" << nDims << endl;

    if(!valfn.empty()) {
      int td = loadDataset(valfn, valset);
      if(td<0) {
       cout << "Error Read testing data: " << valfn <<  endl;
       return -1;
      }
      assert(nDims==td);
    }
    
    doubles_t ty(testset.size(), model.b), vy(valset.size(), model.b);
    doubles_t ndcgT, ndcgV, precT, precV, bestndcg, bestprec;
    double mapT, mapV, bestmap = -numeric_limits<double>::max(), bestV = -numeric_limits<double>::max();
    doubles_t tres, tvres;

    
    for(size_t iter=0;iter<model.treeVec.size();iter++) {
      for(size_t i=0;i<testset.size();i++) {
        double yhat = apply_cart(testset[i].x, model.treeVec[iter]);
        ty[i] += model.learningRate*model.w[iter]*yhat;        
      }
      for(size_t i=0;i<valset.size();i++) {
        double yhat = apply_cart(valset[i].x, model.treeVec[iter]);
        vy[i] += model.learningRate*model.w[iter]*yhat;                
      }
      if(iter%10==0) {
        cout << "Iter = " << iter;
        cal_metric(testset, ty, ndcgT, precT, mapT);
        cout << " TLoss = " << model.getLoss(testset, ty) << " TNDCG5 = " << ndcgT[4] << endl; 
        if(!valset.empty()) {
          cal_metric(valset, vy, ndcgV, precV, mapV);          
          if(iter>100 && dcmp(bestV-ndcgV[4])<0) {
            bestV = ndcgV[4];
            bestndcg = ndcgT; bestprec = precT; bestmap = mapT;
          }         
        } else {
          if(iter>100&& dcmp(bestV-ndcgT[4])<0) {
            bestV = ndcgT[4];
            bestndcg = ndcgT; bestprec = precT; bestmap = mapT;
          }
        }
      }
    }
    cout << endl;
    cout << "Precision:";
    for(size_t i=0;i<bestprec.size();i++) cout << "\t" << bestprec[i];
    cout << endl;
    cout << "MAP:\t" << bestmap << endl;
    cout << "NDCG:";
    for(size_t i=0;i<bestndcg.size();i++) cout << "\t" << bestndcg[i];
    cout << endl;
    

    ofstream f(resfn.c_str());
    for(size_t i=0;i<ty.size();i++) f << ty[i] << endl;
    f.close();
    return 0;
  }

  if(task==2) { // model analysis
    model.load(modelfn);
    doubles_t d(model.nDims);
    for(size_t i=0;i<model.treeVec.size();i++) {
      for(int fid=0;fid<model.nDims;fid++) {      
        d[fid] += cal_dependent(model.treeVec[i], fid);
      }
      if(i%10==0) {
        for(size_t j=0;j<d.size();j++) cout << '\t' << d[j]/(i+1);
        cout << endl;            
      }
    }
    return 0;
  }

  int nDims = loadDataset(trainfn, trainset);
  if(nDims<=0) {
    cout << "Error Reading training data:" << trainfn << endl;
    return -1;
  }

  if(!trainfn2.empty()) {
    Dataset trainset2;
    int td = loadDataset(trainfn2, trainset2);
    if(td<0) {
      cout << "Error Read train2 data: " << trainfn2 <<  endl;
      return -1;
    }
    cout << "Train2 Dimension:\t" << td << endl;
//    trainset.push_back(trainset2.begin(), trainset2.end());
    for(Dataset::const_iterator it=trainset2.begin();it!=trainset2.end();it++) {
      trainset.push_back(*it);
    }
  }
  cout << "Dimension:\t" << nDims << endl;
  if(!testfn.empty()) {
    int td = loadDataset(testfn, testset);
    if(td<0) {
      cout << "Error Read testing data: " << testfn << endl;
      return -1;
    }
    cout << "Test Dimension:\t" << td << endl;
    assert(nDims==td);
  }

  if(!valfn.empty()) {
    int td = loadDataset(valfn, valset);
    if(td<0) {
      cout << "Error Read testing data: " << valfn <<  endl;
      return -1;
    }
    assert(nDims==td);
  }

  for(Dataset::iterator it=trainset.begin();it!=trainset.end();it++) it->x.resize(nDims);
  for(Dataset::iterator it=testset.begin();it!=testset.end();it++) it->x.resize(nDims);
  for(Dataset::iterator it=valset.begin();it!=valset.end();it++) it->x.resize(nDims);

  sort(trainset.begin(), trainset.end());
  sort(testset.begin(), testset.end());
  sort(valset.begin(), valset.end());

  
  model.train(trainset, nDims, testset, valset, martParam, m);
  model.save(modelfn);

  if(!m) delete m;
  return 0;
}
