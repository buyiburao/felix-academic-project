#ifndef __CART__IMPL
#define __CART__IMPL

#include <cstdlib>
#include <cassert>
#include <iostream>
#include <algorithm>
#include <functional>
#include <utility>
#include <ext/hash_set>
//#include <unordered_set>
#include <ext/algorithm>

#include <cmath>

#include <boost/timer.hpp>
#include "common.h"

typedef __gnu_cxx::hash_set<int> IntSet;

template <typename VecType>
FeatureIndex* buildFeatureIndex(const std::vector<VecType>& x, int nd) {
  using std::cout;
  using std::endl;
  using std::cerr;

  FeatureIndex *res = new FeatureIndex(x.size(), nd);
  for(int p=0;p<res->nDims;p++) {
    int* pd = res->getDim(p);    
    for(size_t i=0;i<res->nDatas;i++) pd[i] = i;
    std::sort(pd, pd+res->nDatas, FeatureComp<VecType>(p, x));
  }
  return res;
}

inline FeatureIndex* FeatureIndex::getSample(const ints_t& sampleList) const {
  assert(ind!=NULL);
  int2int_t sample(sampleList.size());
  for(size_t i=0;i<sampleList.size();i++) sample.insert(std::make_pair(sampleList[i], i));
  FeatureIndex *res = new FeatureIndex(sampleList.size(), nDims);
  
  for(int p=0;p<nDims;p++) {
    const int* pAll = getDim(p);
    int* pd = res->getDim(p);
    size_t j = 0;
    for(size_t i=0;i<nDatas;i++) {
      int2int_t::const_iterator it = sample.find(pAll[i]);
      if(it!=sample.end()) {
//        assert(it->second>=0 && it->second<res.nDatas);
        pd[j++] = it->second;
      }
    }
    assert(j==sampleList.size());
  }
  return res;
}

// requirement: begin < end
template <typename VecType1, typename VecType2, typename VecType3, typename VecType4>
void Cart<VecType1, VecType2, VecType3, VecType4>::splitNode(
    const Cart<VecType1, VecType2, VecType3, VecType4>::Features& feature,
    const VecType3& y, 
    const VecType2& weight, 
    int begin, int end, Node& node, NodeSplit& split) {
	int i, p, pi;
	double leftSum, rightSum, temp, best, bestvalue = 0.0, splitNum;
  double sumw, leftW;
	int besti = -1, bestp = -1;
  using std::cout;
  using std::endl;
//  cout << "[" << begin << "," << end << "]" << endl;
	splitNum = (int)(cartParam.splitRate*(end-begin));
	if(splitNum<cartParam.minNode) splitNum = cartParam.minNode;

	for(p=0;p<nDims;p++) {
    const int bufOffset = p*nDatas;
		FeatureComp<VecType1> fc(p, feature);
/*    
    cout << " " << p;
    for(int i=begin+1;i<end;i++) {
      if(feature[pind[bufOffset+i-1]][p]>feature[pind[bufOffset+i]][p]) {
        cout << "Error i = " << i << " " << feature[pind[bufOffset+i-1]][p] << " " << feature[pind[bufOffset+i]][p] << endl;
      }
    }
*/    
		assert(__gnu_cxx::is_sorted(pind+bufOffset+begin, pind+bufOffset+end, fc));	
	}
//  cout << endl;
	for(node.ymeans=sumw=0,i=begin;i<end;i++) {
//    cout << weight[pind[0][i]] << " " << y[pind[0][i]] << endl;
		node.ymeans+=weight[pind[i]]*y[pind[i]];
    sumw += weight[pind[i]];
	}

  assert(dcmp(sumw)!=0 && std::isfinite(sumw));
  if(dcmp(sumw)==0) {
    node.ymeans = 0.0;
		node.attribute = -1;
		split.improve = 0.0;		
		split.begin = begin; split.end = end; split.bound = -1;
    return;
  }
	node.ymeans/=sumw;
//  cout << "ymeans = " << node.ymeans <<  " " << sumw << endl;
	for(node.var=0,i=begin;i<end;i++) {
		node.var += weight[pind[i]]*(y[pind[i]]-node.ymeans)*(y[pind[i]]-node.ymeans);
	}
	node.var/=sumw;

  node.size = end-begin;
	if(end-begin<splitNum*2) {
		node.attribute = -1;
		split.improve = 0.0;		
		split.begin = begin; split.end = end; split.bound = -1;
		return;
	}
	std::random_shuffle(find.begin(), find.end());
	best = 0.0;
	for(pi=0;pi<nFeatures;pi++) {
		p = find[pi];
    const int bufOffset = p*nDatas;
		FeatureComp<VecType1> cp(p, feature);
				
		leftSum = weight[pind[bufOffset+begin]]*y[pind[bufOffset+begin]];
    leftW = weight[pind[bufOffset+begin]];

		for(rightSum=0.0,i=begin+1;i<end;i++) rightSum+=weight[pind[bufOffset+i]]*y[pind[bufOffset+i]];
		for(i=begin+1;i<end;i++) {
			if(i>begin+splitNum && i<end-splitNum && dcmp(leftW)!=0 && dcmp(sumw-leftW)!=0) {
				if(cp(pind[bufOffset+i-1], pind[bufOffset+i])) {        
					temp = leftSum*leftSum/leftW+rightSum*rightSum/(sumw-leftW);
					if(std::isfinite(temp) && dcmp(best-temp)<0) {
						best = temp;
						besti = i;  // be careful !
						bestp = p;
						bestvalue = (feature[pind[bufOffset+i-1]][bestp]+feature[pind[bufOffset+i]][bestp])/2;
					}
				}
			}
			leftSum += weight[pind[bufOffset+i]]*y[pind[bufOffset+i]];
			rightSum -= weight[pind[bufOffset+i]]*y[pind[bufOffset+i]];
      leftW += weight[pind[bufOffset+i]];
		}
	}
//  assert(std::isfinite(best));
//  assert(std::isfinite(node.ymeans));
	split.begin = begin; split.end = end; split.bound = -1;
	split.improve = best-node.ymeans*node.ymeans*sumw;
//  cout << "best = " << best << " ymeans = " << node.ymeans << " sumw = " << sumw << endl;
  node.improve  =split.improve;
  assert(std::isfinite(split.improve));
	if(dcmp(split.improve)>0) {		
    assert(besti>=0);  
		node.attribute = bestp;
		FeatureComp<VecType1> cp(bestp, feature);
		
		IntSet leftSet;
    const int bestpOffset = bestp*nDatas;
		for(i=begin;i<besti;i++) leftSet.insert(pind[bestpOffset+i]);
		int* tempInd = new int[end-begin];
		for(p=0;p<nDims;p++) if(p!=bestp) {
      const int bufOffset = p*nDatas;
			int count1 = 0, count2 = besti-begin;
			
			for(i=begin;i<end;i++) {
				if(leftSet.find(pind[bufOffset+i])!=leftSet.end()) {
					tempInd[count1++] = pind[bufOffset+i];
				} else {
					tempInd[count2++] = pind[bufOffset+i];
				}		
			}		
      assert(count1==besti-begin);
      assert(count2==end-begin);
      std::copy(tempInd, tempInd+(end-begin), pind+bufOffset+begin);	
		}
		split.bound = besti;
		delete[] tempInd;
		
		node.value = bestvalue;
	}
}


template <typename VecType1, typename VecType2, typename VecType3, typename VecType4>
Node* Cart<VecType1, VecType2, VecType3, VecType4>::fit(
    const Cart<VecType1, VecType2, VecType3, VecType4>::Features& x, int nd, 
    const VecType2& w, const VecType3& target, VecType4& fit_target, FeatureIndex& buf) {
  using std::cout;
  using std::cerr;
  using std::endl;
  
  nDatas = x.size();
  nDims = nd;
	nFeatures = (int)floor(nDims*cartParam.sample);
	if(nFeatures>nDims) nFeatures = nDims;
								 	
	int i, j, k, bestnode=-1;
	double bestimprove;
	Node *root = new Node();	
	Node **leafNode = new Node*[cartParam.nLeafNodes];
	NodeSplit *nodeSplit = new NodeSplit[cartParam.nLeafNodes];
	ints_t depth(cartParam.nLeafNodes);
	
	find.resize(nDims);
	for(i=0;i<nDims;i++) find[i] = i;
	
	// init pind for each dimemnsion
  pind = buf.ind;

	// init root node;		
	leafNode[0] = root;
	depth[0] = 1;
	splitNode(x, target, w, 0, nDatas, *(leafNode[0]), nodeSplit[0]);
	for(i=0;i<cartParam.nLeafNodes-1;i++) {
 //   printf("i = %d\n", i);
		for(bestimprove=0,j=0;j<=i;j++) {
			if(depth[j]<=cartParam.maxDepth&&dcmp(nodeSplit[j].improve-bestimprove)>0) {
				bestimprove = nodeSplit[j].improve;
				bestnode = j;
			}
		}
		
		if(dcmp(bestimprove)==0) {
//			printf("heheh %d\n", i);
			break;
		}
//		fprintf(stderr, "split attr %d improve %lf\n", leafNode[bestnode]->attribute, bestimprove);
		leafNode[bestnode]->right = new Node(); 
		leafNode[bestnode]->left = new Node();
				
		splitNode(x, target, w, nodeSplit[bestnode].bound, nodeSplit[bestnode].end,
							 *(leafNode[bestnode]->right), nodeSplit[i+1]);
		//fprintf(stderr, "jejhe %d \n", i);
		splitNode(x, target, w, nodeSplit[bestnode].begin, nodeSplit[bestnode].bound,
							 *(leafNode[bestnode]->left), nodeSplit[bestnode]);
							 
		//fprintf(stderr, "jejhe\n");					 
		leafNode[i+1] = leafNode[bestnode]->right;
		depth[i+1] = depth[bestnode]+1;
		leafNode[bestnode] = leafNode[bestnode]->left;		
		depth[bestnode] = depth[bestnode]+1;
	}

	fit_target.resize(nDatas);
  for(j=0;j<=i&&j<cartParam.nLeafNodes;j++) {
    //			printf("%d %.4lf %.4lf, ", nodeSplit[j].end-nodeSplit[j].begin, leafNode[j]->var, nodeSplit[j].improve);
    for(k=nodeSplit[j].begin;k<nodeSplit[j].end;k++) {
      fit_target[pind[k]] = leafNode[j]->ymeans;
    }
  }	
//		printf("\n");
	delete []nodeSplit;
	delete []leafNode;

//  fprintf(stderr, "fit_cart out\n");	
	return root;
}

#endif
