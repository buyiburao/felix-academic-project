#include <iostream>
#include <fstream>
#include <iterator>
#include <string>
#include <vector>
#include <algorithm>
#include <cmath>
#include <limits>
#include <ext/hash_map>
//#include <unordered_map>
#include <rank_common.h>
#include <rank_gbtree.h>
#include <tree.h>
#include <cart.h>

using namespace std;
typedef pair<int, int> intp_t;
typedef __gnu_cxx::hash_map<int, intp_t> int2intp_t;
typedef __gnu_cxx::hash_map<int, int> int2int_t;

struct GBRankParam {
  double learningRate;
  int nIterations;
  double samplePrec;
  double tau;
  CartParam cartParam;
};

struct GBRank {
  TreeVec treeVec;
  double learningRate;
  int nDims;
  double tau;

  void train(const Dataset& trainset, int nDims, const Dataset& testset, const Dataset& valset, const GBRankParam& param, const Metric* m=NULL);
  void test(const Dataset& ds, doubles_t& res) const;
  double predict(const Example& ex) const;
  double getLoss(const Dataset& ds, const doubles_t& res) const;
  void save(const std::string& fn) const;
  void load(const std::string& fn);

  double evaluate(const Dataset& ds, const doubles_t& r) const;

  ~GBRank() {
    for(size_t i=0;i<treeVec.size();i++) delete treeVec[i];
  }


  friend std::ostream& operator<<(std::ostream &f, const GBRank &v);
  friend std::istream& operator>>(std::istream &f, GBRank &v);
};

void GBRank::train(const Dataset& trainset, int nDims, const Dataset& testset, const Dataset& valset, const GBRankParam& param, const Metric* m) {
  this->nDims = nDims;
  this->learningRate = param.learningRate;
  tau = param.tau;
  treeVec.clear();

  FVectors tx(trainset.size());
  for(size_t i=0;i<trainset.size();i++) tx[i] = trainset[i].x;
  FeatureIndex *find = buildFeatureIndex(tx, nDims);

  Cart<FVector, doubles_t, doubles_t, doubles_t> cart(param.cartParam);

  int2intp_t qmap;
//  int2int_t qsamples;
  int nSamples = static_cast<int>(floor(trainset.size()*param.samplePrec+eps));
  for(size_t u,l=0;l<trainset.size();l=u) {
    u = upper_bound(trainset.begin()+l, trainset.end(), trainset[l])-trainset.begin();
    qmap.insert(make_pair(trainset[l].qid, make_pair(l, u)));
//    int t = static_cast<int>(floor((u-l)*param.samplePrec+eps));
//    qsamples.insert(make_pair(trainset[l].qid, t));
//    nSamples += t;
//    cout << trainset[l].qid << "\t" << u-l << endl;
  }

  ints_t ind(trainset.size());
  for(size_t i=0;i<trainset.size();i++) ind[i] = i;
  doubles_t yhat(trainset.size(), 0.0), weight(trainset.size()), fity(nSamples), target(trainset.size());
  FVectors x;
  doubles_t w(trainset.size()), t(trainset.size());
  double bestT = -numeric_limits<double>::max(), bestV = -numeric_limits<double>::max(), bestTV = -numeric_limits<double>::max();
  for(int iter=0;iter<param.nIterations;iter++) {
    weight.resize(nSamples); target.resize(nSamples); x.resize(nSamples);
    random_shuffle(ind.begin(), ind.end());
    for(int p=0;p<nSamples;p++) {
      const int i = ind[p];
      weight[p] = 0.0; target[p] = 0.0;
      const Example& xi = trainset[i];
      int2intp_t::const_iterator it = qmap.find(xi.qid);
      for(int j=it->second.first;j<it->second.second;j++) {
        const Example& xj = trainset[j];
        assert(xi.qid==xj.qid);
        if(xi.y>xj.y) {
          const double det = yhat[j]-yhat[i]+tau;
          if(dcmp(det)>0) {
            target[p] += det;
          }
          weight[p]++;          
        } else if(xi.y<xj.y) {
          const double det = yhat[i]-yhat[j]+tau;
          if(dcmp(det)>0) {
            target[p] -= det;
          }
          weight[p]++;          
        }
      }
      if(dcmp(weight[p])!=0) {
        target[p]/=weight[p]; 
        weight[p] = 1.0;
      } else target[p] = 0.0;
      x[p] = trainset[i].x;
    }

    FeatureIndex *sampleInd = find->getSample(ints_t(ind.begin(), ind.begin()+nSamples));
    treeVec.push_back(cart.fit(x, nDims, weight, target, fity, *sampleInd));
    delete sampleInd;
    for(size_t i=0;i<trainset.size();i++) {
      double t = apply_cart(trainset[i].x, treeVec.back());
      yhat[i] += learningRate*t;
    }
    if(iter%10==0 && m) {
      cout << "Iter = " << iter << " Loss = " << getLoss(trainset, yhat) << " NDCG5 = " << m->evaluate(trainset, yhat);
      if(!testset.empty()) {
        doubles_t tres;
        test(testset, tres);
        double tm = m->evaluate(testset, tres);
        cout << " TLoss = " << getLoss(testset, tres) << " TNDCG5 = "  << tm;
      
        if(iter>100 && dcmp(bestT-tm)<0) bestT = tm;
        if(!valset.empty()) {
          doubles_t vres;
          test(valset, vres);
          double vm = m->evaluate(valset, vres);
          
          cout << " VLoss = " << getLoss(valset, vres) << " VNDCG5 = " << vm; 
          if(iter>100 && dcmp(bestV-vm)<0) {
            bestV = vm; bestTV =  tm;
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

double GBRank::getLoss(const Dataset& ds, const doubles_t& res) const {
  double loss = 0.0;
//  cout << "\nMargin" << endl;
  for(size_t u,l=0;l<ds.size();l=u) {
    u = upper_bound(ds.begin()+l, ds.end(), ds[l])-ds.begin();
//    cout << "QID " << ds[l].qid << endl;
    for(size_t i=l;i<u;i++) for(size_t j=i+1;j<u;j++) {
      double det = res[i]-res[j];
      if(ds[i].y>ds[j].y && dcmp(det-tau)<0) loss += (tau-det)*(tau-det);
      if(ds[i].y<ds[j].y && dcmp(det+tau)>0) loss += (det+tau)*(det+tau);
      if(ds[i].y>ds[j].y) {
//        cout << det << "\t";
      } else if(ds[i].y<ds[j].y) {
//        cout << det << "\t";
      }
    }
//    cout << endl;
  }
//  cout << "Margin End" << endl;
  return loss;
}

double GBRank::predict(const Example& ex) const {
  double res = 0.0;
  for(size_t i=0;i<treeVec.size();i++) {
    res = (res*i+learningRate*apply_cart(ex.x, treeVec[i]))/(i+1);
  }
  return res;
}

void GBRank::test(const Dataset& ds, doubles_t& res) const {
  res.resize(ds.size(), 0);
  for(size_t i=0;i<ds.size();i++) {
    res[i] = predict(ds[i]);
  }
}

void GBRank::save(const string& fn) const {
  ofstream f(fn.c_str());
  f << (*this) << endl;
  f.close();
}

void GBRank::load(const string& fn) {
  ifstream f(fn.c_str());
  f >> (*this);
  f.close();
}

ostream& operator<<(ostream &f, const GBRank &v) {
  f << v.learningRate << "\n" << v.nDims << "\n" << v.tau << "\n";
  f << v.treeVec.size() << "\n";
  for(TreeVec::const_iterator it=v.treeVec.begin();it!=v.treeVec.end();it++) {
    save_tree(f, *it);
  }
  return f;
}

istream& operator>>(istream &f, GBRank &v) {
  f >> v.learningRate >> v.nDims >> v.tau;
  size_t nTrees;
  f >> nTrees;
  v.treeVec.resize(nTrees);
  for(size_t i=0;i<nTrees;i++) {
    v.treeVec[i] = load_tree(f);
  }
  return f;
}


string trainfn, testfn, valfn, modelfn, resfn;
Dataset trainset, testset, valset;
int task = 0;
GBRankParam param;
Metric* m;

int parseArg(int argc, char* argv[]) {
  if(strcmp("-t", argv[1])==0) {
    task = 1;
    testfn = argv[2];
    modelfn = argv[3];
    resfn = argv[4];
    if(argc>=5) valfn = argv[4];
    return 0;
  }
  
  trainfn = argv[1];
  modelfn = argv[2];

  param.learningRate = 0.001;
  param.nIterations = 400;
  param.samplePrec = 0.4;
  param.tau = 0.5;
  param.cartParam.nLeafNodes = 20;
  param.cartParam.minNode = 5;
  param.cartParam.maxDepth = 10;
  param.cartParam.sample = 0.4;
  param.cartParam.splitRate = 0.1;
  string metric;

  for(int i=3;i<argc;i++) {
    if(strcmp("-lr", argv[i])==0) {
      assert(from_string<double>(param.learningRate, argv[++i]));
      continue;
    }
    if(strcmp("-iter", argv[i])==0) {
      assert(from_string<int>(param.nIterations, argv[++i]));
      continue;
    }
    if(strcmp("-sample", argv[i])==0) {
      assert(from_string<double>(param.samplePrec, argv[++i]));
      continue;
    }
    if(strcmp("-size", argv[i])==0) {
      assert(from_string<int>(param.cartParam.nLeafNodes, argv[++i]));
      continue;
    }
    if(strcmp("-sr", argv[i])==0) {
      assert(from_string<double>(param.cartParam.splitRate, argv[++i]));
      continue;
    }
    if(strcmp("-minNode", argv[i])==0) {
      assert(from_string<int>(param.cartParam.minNode, argv[++i]));
      continue;      
    }
    if(strcmp("-csample", argv[i])==0) {
      assert(from_string<double>(param.cartParam.sample, argv[++i]));
      continue;
    }
    if(strcmp("-tau", argv[i])==0) {
      assert(from_string<double>(param.tau, argv[++i]));
      continue;
    }
    if(strcmp("-depth", argv[i])==0) {
      assert(from_string<int>(param.cartParam.maxDepth, argv[++i]));
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
    m = new DcgMetric<>(5);
  } else {
    m = new NdcgMetric<>(5);
  }
  return 0;
}

int main(int argc, char* argv[]) {
  parseArg(argc, argv);

  if(task==1) {
    cout << "Loading model from " << modelfn << endl;
    GBRank model;
    model.load(modelfn);
    cout << "Loading test data from " << testfn << endl;
    int nDims = loadDataset(testfn, testset);
    if(nDims<0) {
      cout << "Error reading test data from " << testfn << endl;
      return 0;
    }
//    sort(testset.begin(), testset.end()); 
    doubles_t res, tprec;
    double tmap;
    model.test(testset, res);
    ofstream f(resfn.c_str());
    copy(res.begin(), res.end(), ostream_iterator<double>(f, "\n"));
    f.close();
    doubles_t tndcg;
    cal_dcg(testset, res, 5, tndcg, GalaxyGain(), GalaxyDiscount());
    cout << "TNDCG5 = " << tndcg[4] << endl;
    return 0;
  }
  cout << "Reading training data" << endl;
  int nDims = loadDataset(trainfn, trainset);
  if(nDims<0) {
    cout << "Error reading training data from " << trainfn << endl;
    return 0;
  }
  std::sort(trainset.begin(), trainset.end());  
  for(size_t i=0;i<trainset.size();i++) trainset[i].x.resize(nDims);
  cout << "Read " << trainset.size() << " training data. Dim = " << nDims << endl; 
  if(!testfn.empty()) {
    int td = loadDataset(testfn, testset);
    if(td<0) {
      cout << "Error reading test data from " << testfn << endl;
      return 0;
    }
    for(size_t i=0;i<testset.size();i++) testset[i].x.resize(nDims);
    sort(testset.begin(), testset.end());
  }
  if(!valfn.empty()) {
    int td = loadDataset(valfn, valset);
    if(td<0) {
      cout << "Error reading vali data from " << testfn << endl;
      return 0;
    }
    for(size_t i=0;i<valset.size();i++) valset[i].x.resize(nDims);
    std::sort(valset.begin(), valset.end());      
  }

  cout << "Training models " << endl;
  GBRank model;
  model.train(trainset, nDims, testset, valset, param, m);
  cout << "Saving models to " << modelfn << endl;
  model.save(modelfn);

  if(!m) delete m;
  return 0;
}

