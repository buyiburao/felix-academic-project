#include <cmath>
#include <iostream>
#include <fstream>
#include <iterator>
#include <algorithm>
#include <iomanip>
//#include "common.h"
#include "rank_common.h"

using namespace std;
using namespace boost;

istream& operator>>(istream& in, Example& ex) {
  string tmp;
  if(!(in >> ex.y >> tmp)) return in;
  assert(tmp.substr(0, 4)=="qid:");
  assert(from_string<int>(ex.qid, tmp.substr(4)));
  getline(in,  tmp);
//  cout << "[" << tmp << "]" << endl;
  int commentStart = tmp.find_first_of("#");
  assert(from_string<FVector>(ex.x, tmp.substr(0, commentStart)+"\n"));
  ex.comment = tmp.substr(commentStart+1);
  return in;
}

ostream& operator<<(ostream& out, const Example& ex) {
  out << ex.y << "\tqid:" << ex.qid;
  for (int i=0; i<ex.x.size(); i++) if (ex.x[i] || (i+1 == ex.x.size())) {
    out << "\t" << i+1 << ":";
    VFloat x = ex.x[i];
    short ix = (int)x;
    if (x == (VFloat)ix)
      out << ix;
    else
      out << x;
  }
  if(!ex.comment.empty()) out << "\t#" << ex.comment;
  out << "\n";
  return out;
}


int loadDataset(const string& fn, Dataset& dataset) {
  dataset.clear();
  iostreams::filtering_istream f;
  build_stream(f, fn);
  Example ex;
  int dim = -1, cnt = 0;
  while(f >> ex) {
//    assert(cnt==0 || !(ex<dataset.back()));
    dataset.push_back(ex);
    if(dim<ex.x.size()) dim = ex.x.size();

    cnt++;
//    cout << cnt << endl;
    if(cnt%1000==0) cout << cnt<< ".";
  }
  cout << endl;
  return dim;
}

int saveDataset(const std::string& fn, const Dataset& dataset) {
  ofstream f(fn.c_str());
  copy(dataset.begin(), dataset.end(), ostream_iterator<Example>(f, ""));
  f.close();
  return 0;
}
////////////////////////////////////////////////
// Evaluation:
////////////////////////////////////////////////
#if 0
struct ScoreComp {
  const doubles_t& s;
  ScoreComp(const doubles_t& score) : s(score) { }
  bool operator()(const int& a, const int& b) const {
    return dcmp(s[a]-s[b])>0;
  }
};
#endif

void dcg(const doubles_t& t, const doubles_t& res, doubles_t& dcg) {
  const int N = 10;
  assert(t.size()==res.size());
  int *ind = new int[t.size()];
  for(size_t i=0;i<t.size();i++) ind[i] = i;
  std::sort(ind, ind+t.size(), ScoreComp(res));
  dcg.resize(N);
  double d = 0.0;
  for(size_t i=0;i<dcg.size();i++) {
//    printf("%lf %lf\n", t[ind[i]], res[ind[i]]);
    if(i<t.size()) d += (pow(2, t[ind[i]])-1)/log2(i==0?2:i+1);
    dcg[i] = d;
  }
  delete[] ind;
}

void eval(const doubles_t& t, const doubles_t& res, doubles_t& dcg, doubles_t& prec, double& ap) {
  const int N = 10;
/*
  cout << "T = ";
  copy(t.begin(), t.end(), ostream_iterator<double>(cout, " "));
  cout << "\nR = ";
  copy(res.begin(), res.end(), ostream_iterator<double>(cout, " "));
  cout << endl;
*/
  assert(t.size()==res.size());
  ints_t ind(t.size());
  for(size_t i=0;i<ind.size();i++) ind[i] = i;
  std::sort(ind.begin(), ind.end(), ScoreComp(res));
  dcg.resize(N); prec.resize(N); ap = 0.0;
  double d = 0.0, p = 0.0;
  for(int i=0;i<N;i++) {
    if(size_t(i)<t.size()) {
      d += (pow(2, t[ind[i]])-1)/log2(i==0?2:i+1);
      p += dcmp(t[ind[i]])>0?1:0;
    }
//    assert(i>=2 || dcmp(d-p)==0);
    dcg[i] = d; prec[i] = p/double(i+1);
  }
  p = 0.0;
  for(size_t i=0;i<ind.size();i++) if(dcmp(t[ind[i]])>0) {
    p++;
    ap += p/(i+1);
  }
/*  
  for(size_t i=0;i<ind.size();i++) {
    cout << "(" << t[ind[i]] << ", " << res[ind[i]] << ") ";
  }
  cout << endl;  */
/*
  cout << "DCG: ";
  for(size_t i=0;i<dcg.size();i++) cout << dcg[i] << " ";
  cout << endl;
*/
//  for(size_t i=0;i<prec.size();i++) cout << prec[i] << " ";
//  cout << endl;
  if(p!=0) {
    ap/=p;
  }
//  cout << ap << endl;
}

void cal_ndcg(const Dataset& data, const doubles_t& res, doubles_t& avg) {  
  doubles_t t, r, d, m;

  ints_t ind(data.size());
  for(size_t i=0;i<ind.size();i++) ind[i] = i;
  QidComp qidComp(data);
  sort(ind.begin(), ind.end(), qidComp);

  int num = 0;
  avg.clear();
  for(size_t u,l=0;l<data.size();l=u) {
    u = upper_bound(ind.begin()+l, ind.end(), ind[l], qidComp)-ind.begin();
    t.clear();
    r.clear();
    for(size_t i=l;i<u;i++) {
      t.push_back(data[ind[i]].y); r.push_back(res[ind[i]]);
    }
    dcg(t, r, d);
    dcg(t, t, m);
    if(avg.empty()) avg.resize(d.size());
    for(size_t j=0;j<avg.size();j++) if(fabs(m[j])>1e-4) {
      avg[j]+=d[j]/m[j];
    }
    num++;    
  }
  for(size_t j=0;j<avg.size();j++) avg[j]/=num;
}

void cal_metric(const Dataset& data, const doubles_t& res, doubles_t& ndcg, doubles_t& prec, double& map, double& cp) {
  doubles_t t, r, dcgt, prect, m;
  double apt = 0, corrp = 0.0, totalp = 0.0;

  ints_t ind(data.size());
  for(size_t i=0;i<ind.size();i++) ind[i] = i;
  QidComp qidComp(data);
  sort(ind.begin(), ind.end(), qidComp);

  int num = 0;
  ndcg.clear(); prec.clear(); map = 0.0;
  for(size_t u,l=0;l<data.size();l=u) {
    u = upper_bound(ind.begin()+l, ind.end(), ind[l], qidComp)-ind.begin();
    
    for(size_t i=l;i<u;i++) for(size_t j=i+1;j<u;j++) {
      if(dcmp(data[ind[i]].y-data[ind[j]].y)!=0) {
        totalp++;
        double d = (data[ind[i]].y-data[ind[j]].y)*(res[ind[i]]-res[ind[j]]);
        if(dcmp(d)>0) corrp++;
      }
    }

    t.clear();
    r.clear();
    for(size_t i=l;i<u;i++) {
      t.push_back(data[ind[i]].y); r.push_back(res[ind[i]]);
    }
    eval(t, r, dcgt, prect, apt);
    dcg(t, t, m);
    if(ndcg.empty()) ndcg.resize(dcgt.size());
    if(prec.empty()) prec.resize(prect.size());

    map += apt;
    for(size_t j=0;j<ndcg.size();j++) {
      if(fabs(m[j])>1e-4) {
        ndcg[j]+=dcgt[j]/m[j];
//        cout << j << " " << dcgt[j] << " " << m[j] << endl;
//        assert(j>1 || dcmp(ndcg[j]-prect[j])==0);
      }
      prec[j]+=prect[j];
    }
    num++;    
  }
  for(size_t j=0;j<ndcg.size();j++) ndcg[j]/=num;
  for(size_t j=0;j<prec.size();j++) prec[j]/=num;
  map/=num;
  cp = corrp/totalp;
}


void dcg_yandex(const doubles_t& t, const doubles_t& res, doubles_t& dcg) {
  const int N = 2010;
  assert(t.size()==res.size());
  int *ind = new int[t.size()];
  for(size_t i=0;i<t.size();i++) ind[i] = i;
  std::sort(ind, ind+t.size(), ScoreComp(res));
  dcg.resize(N);
  double d = 0.0;
  for(size_t i=0;i<dcg.size();i++) {
//    printf("%lf %lf\n", t[ind[i]], res[ind[i]]);
    if(i<t.size()) d += (t[ind[i]])/log2(i+2);
    dcg[i] = d;
  }
  delete[] ind;
}


void cal_dcg_yandex(const Dataset& data, const doubles_t& res, doubles_t& avg) {
  doubles_t t, r, dcgt;
  int q = 0;
  avg.clear();
  ints_t ind(data.size());
  for(size_t i=0;i<ind.size();i++) ind[i] = i;
  QidComp qidComp(data);
  sort(ind.begin(), ind.end(), qidComp);
  for(size_t u,l=0;l<data.size();l=u) {
    u = upper_bound(ind.begin()+l, ind.end(), ind[l], qidComp)-ind.begin();
    t.clear();    r.clear();
    for(size_t i=l;i<u;i++) {
      t.push_back(data[ind[i]].y); r.push_back(res[ind[i]]);
    }
    dcg_yandex(t, r, dcgt);
    if(avg.empty()) avg.resize(dcgt.size(), 0);
    for(size_t i=0;i<avg.size();i++) avg[i]+=dcgt[i];
    q++;
  }
  for(size_t i=0;i<avg.size();i++) avg[i]/=q;
}
