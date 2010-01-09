#ifndef __RANK_COMMON_
#define __RANK_COMMON_
#include <iostream>
#include <vector>
#include <sstream>
#include <string>
//#include <unordered_map>
#include <ext/hash_map>
#include <iomanip>
#include <cmath>

#include <vectors.h>
#include <matrices.h>
#include <common.h>

struct Example {
  FVector x;
  int qid;
  double y;
  std::string comment;

  Example() : x(0), qid(-1), y(0) {}
  Example(const SVector& xx, int q, double& yy) : x(xx), qid(q), y(yy) {}
  bool operator<(const Example& b) const {
    return qid<b.qid;
  }
  friend std::istream& operator>>(std::istream& in, Example& ex);
  friend std::ostream& operator<<(std::ostream& out, const Example& ex);
};

typedef std::vector<Example> Dataset;
typedef std::pair<int, int> IntPair;
typedef std::vector<IntPair> IntPairVec;
typedef std::vector<double> doubles_t;
typedef std::vector<int> ints_t;
//typedef std::unordered_map<int, double> Int2Double;
typedef __gnu_cxx::hash_map<int, double> Int2Double;

int loadDataset(const std::string& fn, Dataset& dataset);
int saveDataset(const std::string& fn, const Dataset& dataset);

struct QidComp {
  const Dataset& data;
  QidComp(const Dataset& d) : data(d) {}
  bool operator()(const int a, const int b) const {
    return data[a]<data[b];
  }
};

struct ScoreComp {
  const doubles_t& s;
  ScoreComp(const doubles_t& score) : s(score) { }
  bool operator()(const int& a, const int& b) const {
    return dcmp(s[a]-s[b])>0;
  }
};

struct LetorGain : public std::unary_function<double, double> {
  double operator()(double g) const {
    return pow(2, g)-1;
  }
};

struct IdentifyGain : public std::unary_function<double, double> {
  double operator()(double g) const {
    return g;
  }
};

struct GalaxyGain : public std::unary_function<double, double> {
  double operator()(double g) const {
    if(dcmp(g-4)>=0) return 10;
    if(dcmp(g-3)>=0) return 7;
    if(dcmp(g-2)>=0) return 4;
    if(dcmp(g-1)>=0) return 2;
    return 0;
  }
};

struct LetorDiscount : public std::unary_function<double, int> {
  double operator()(int p) const {
    return 1.0/log2(p==0?2:p+1);
  }
};

struct GalaxyDiscount : public std::unary_function<double, int> {
  double operator()(int p) const {
    return 1.0/log2(p+2);
  }
};

template <typename GainFun, typename DiscountFun>
void cal_dcg(const doubles_t& y, const doubles_t& yhat, int cut, doubles_t& res, const GainFun& g, const DiscountFun& c) {
  assert(y.size()==yhat.size());
  ints_t ind(y.size());
  for(size_t i=0;i<ind.size();i++) ind[i] = i;
//  for(size_t i=0;i<y.size();i++) ind[i] = i;
  std::sort(ind.begin(), ind.end(), ScoreComp(yhat));
  res.resize(cut);
  double d = 0.0;
  for(size_t i=0;i<res.size();i++) {
//    printf("%lf %lf\n", t[ind[i]], res[ind[i]]);
    if(i<y.size()) d += g(y[ind[i]])*c(i);
    res[i] = d;
  }
}

template <typename GainFun, typename DiscountFun>
void cal_dcg(const Dataset& data, const doubles_t& yhat, int cut, doubles_t& res, const GainFun& g, const DiscountFun& c) {
  doubles_t dcg, t, r;
  ints_t ind(data.size());
  for(size_t i=0;i<ind.size();i++) ind[i] = i;
  QidComp qidComp(data);
  sort(ind.begin(), ind.end(), qidComp);

  res.resize(cut, 0.0);
  int num = 0;
  for(size_t u,l=0;l<data.size();l=u, num++) {
    u = upper_bound(ind.begin()+l, ind.end(), ind[l], qidComp)-ind.begin();
    t.resize(u-l); r.resize(u-l);

    for(size_t i=l;i<u;i++) {
//      std::cout << data[ind[i]].comment << "\t" << data[ind[i]].y << "\t" << yhat[ind[i]] << std::endl;
      t[i-l] = data[ind[i]].y; r[i-l] = yhat[ind[i]];
    }
    cal_dcg(t, r, cut, dcg, g, c);
//    std::cout << "Qid: " << data[ind[l]].qid << "\t" << dcg.back() << std::endl;
    for(size_t i=0;i<res.size();i++) res[i]+=dcg[i];
  }
  for(size_t i=0;i<res.size();i++) res[i]/=num;
}

void cal_ndcg(const Dataset& data, const doubles_t& res, doubles_t& avg);
//void cal_prec(const Dataset& data, doubles_t& res, doubles_t& avg);
//void cal_map(const Dataset& data, doubles_t& res, doubles_t& avg);
void cal_metric(const Dataset& data, const doubles_t& res, doubles_t& ndcg, doubles_t& prec, double& map, double& cp);
void cal_dcg_yandex(const Dataset& data, const doubles_t& res, doubles_t& dcg);
inline void cal_metric(const Dataset& data, const doubles_t& res, doubles_t& ndcg, doubles_t& prec, double& map) {
  double cp;
  cal_metric(data, res, ndcg, prec, map, cp);
}

struct Metric {
  virtual ~Metric() {}
  virtual double evaluate(const Dataset& ds, const doubles_t& r) const = 0;
};

template <typename GainFun=GalaxyGain, typename DiscountFun=GalaxyDiscount>
struct NdcgMetric : public Metric {
  GainFun g;
  DiscountFun c;
  int pos;

  NdcgMetric(int p=5) : pos(p) {}
  double evaluate(const Dataset& ds, const doubles_t& r) const {
    doubles_t ndcg, y(ds.size()), max;
    for(size_t i=0;i<y.size();i++) y[i] = ds[i].y;
    cal_dcg(ds, r, pos, ndcg, g, c); 
    cal_dcg(ds, y, pos, max, g, c); 
    if(dcmp(max.back())==0) return 1;
    return ndcg.back()/max.back();
  }
};


template <typename GainFun=GalaxyGain, typename DiscountFun=GalaxyDiscount>
struct DcgMetric : public Metric {
  GainFun g;
  DiscountFun c;
  int pos;

  DcgMetric(int p=5) : pos(p) {}
  double evaluate(const Dataset& ds, const doubles_t& r) const {
    doubles_t dcg;
    cal_dcg(ds, r, pos, dcg, g, c);
    return dcg.back();
  }
};

struct MSError : public Metric {
  double evaluate(const Dataset& ds, const doubles_t& r) const {
    double mae = 0.0;
    for(size_t i=0;i<ds.size();i++) mae += (ds[i].y-r[i])*(ds[i].y-r[i]);
    return -mae/ds.size();
  }
};

#endif
