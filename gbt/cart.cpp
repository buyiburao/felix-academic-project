#ifndef __CART__IMPL
#define __CART__IMPL

#include <cstdio>
#include <cstdlib>
#include <cassert>
#include <iostream>
#include <algorithm>
#include <functional>
#include <ext/hash_set>
//#include <unordered_set>
#include <ext/algorithm>
#include <cmath>

#include "cart.h"
#include "common.h"

typedef __gnu_cxx::hash_set<int> IntSet;
//typedef std::unordered_set<int> IntSet;
using std::cout;
using std::endl;

static double splitRate = 0.1;
static int nDatas, nDims, nFeatures;
static CartParam cartParam;
static int *find;

static int **pind;

// requirement: begin < end
static void splitNode(const FVectors& feature, const doubles_t& y, const doubles_t& weight, int begin, int end, Node& node, NodeSplit& split) {
	int i, p, pi;
	double leftSum, rightSum, temp, best, bestvalue = 0.0, splitNum;
  double sumw, leftW;
	int besti = -1, bestp = -1;

	splitNum = (int)(splitRate*(end-begin));
	if(splitNum<cartParam.minNode) splitNum = cartParam.minNode;

	for(p=0;p<nDims;p++) {
		FeatureComp<FVector> fc(p, feature);
		assert(__gnu_cxx::is_sorted(pind[p]+begin, pind[p]+end, fc));	
	}
	for(node.ymeans=sumw=0,i=begin;i<end;i++) {
//    cout << weight[pind[0][i]] << " " << y[pind[0][i]] << endl;
		node.ymeans+=weight[pind[0][i]]*y[pind[0][i]];
    sumw += weight[pind[0][i]];
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
		node.var += weight[pind[0][i]]*(y[pind[0][i]]-node.ymeans)*(y[pind[0][i]]-node.ymeans);
	}
	node.var/=sumw;

  node.size = end-begin;
	if(end-begin<=cartParam.minNode) {
		node.attribute = -1;
		split.improve = 0.0;		
		split.begin = begin; split.end = end; split.bound = -1;
		return;
	}
	std::random_shuffle(find, find+nDims);
	best = 0.0;
	for(pi=0;pi<nFeatures;pi++) {
		p = find[pi];
		FeatureComp<FVector> cp(p, feature);
				
		leftSum = weight[pind[p][begin]]*y[pind[p][begin]];
    leftW = weight[pind[p][begin]];

		for(rightSum=0.0,i=begin+1;i<end;i++) rightSum+=weight[pind[p][i]]*y[pind[p][i]];
		for(i=begin+1;i<end;i++) {
			if(i>begin+splitNum && i<end-splitNum && dcmp(leftW)!=0 && dcmp(sumw-leftW)!=0) {
				if(cp(pind[p][i-1], pind[p][i])) {        
					temp = leftSum*leftSum/leftW+rightSum*rightSum/(sumw-leftW);
					if(std::isfinite(temp) && dcmp(best-temp)<0) {
						best = temp;
						besti = i;  // be careful !
						bestp = p;
						bestvalue = (feature[pind[p][i-1]][bestp]+feature[pind[p][i]][bestp])/2;
					}
				}
			}
			leftSum += weight[pind[p][i]]*y[pind[p][i]];
			rightSum -= weight[pind[p][i]]*y[pind[p][i]];
      leftW += weight[pind[p][i]];
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
		FeatureComp<FVector> cp(bestp, feature);
		
		IntSet leftSet;
		for(i=begin;i<besti;i++) leftSet.insert(pind[bestp][i]);
		int* tempInd = new int[end-begin];
		for(p=0;p<nDims;p++) if(p!=bestp) {
			int count1 = 0, count2 = besti-begin;
			
			for(i=begin;i<end;i++) {
				if(leftSet.find(pind[p][i])!=leftSet.end()) {
					tempInd[count1++] = pind[p][i];
				} else {
					tempInd[count2++] = pind[p][i];
				}		
			}		
      assert(count1==besti-begin);
      assert(count2==end-begin);
      std::copy(tempInd, tempInd+(end-begin), pind[p]+begin);	
		}
		split.bound = besti;
		delete[] tempInd;
		
		node.value = bestvalue;
	}
}


Node* fit_cart(const FVectors& x, int nd, const doubles_t& w, const doubles_t& target, const CartParam& cart_param, doubles_t& fit_target, int** buf) {
//Node* fit_cart(const DataSet& dataset, const double* w, const double* target,
//							 const CartParam& cart_param, double* fit_target, int** buf) {
	// init global variables						 	
	//x = data; 

//  fprintf(stderr, "fit_cart in\n");
//	y = target;
//  weight = w;
	//data = dataset;
//	feature = dataset.feature;
//	nDatas = dataset.data_num;
//	nDims = dataset.dim_num;
  nDatas = x.size();
  nDims = nd;
	nFeatures = (int)floor(nDims*cart_param.sample);
	if(nFeatures>nDims) nFeatures = nDims;
	cartParam = cart_param;
	splitRate = cart_param.splitRate;
								 	
	int i, j, k, bestnode=-1;
	double bestimprove;
	Node *root = new Node();	
	Node **leafNode = new Node*[cart_param.nLeafNodes];
	NodeSplit *nodeSplit = new NodeSplit[cart_param.nLeafNodes];
	int *depth = new int[cart_param.nLeafNodes];
	
	find = new int[nDims];
	for(i=0;i<nDims;i++) find[i] = i;
	
	// init pind for each dimemnsion
	pind = buf;
	if(pind==NULL) { // the buf is not created 
		pind = new int*[nDims];
		for(i=0;i<nDims;i++) {
			pind[i] = new int[nDatas];
		}
	}

	for(i=0;i<nDims;i++) {
		for(j=0;j<nDatas;j++) {
			pind[i][j] = j;
		}
		FeatureComp<FVector> fc(i, x);
		std::sort(pind[i], pind[i]+nDatas, fc);
	}
	
	// init root node;		
	leafNode[0] = root;
	depth[0] = 1;
	splitNode(x, target, w, 0, nDatas, *(leafNode[0]), nodeSplit[0]);

	for(i=0;i<cart_param.nLeafNodes-1;i++) {
 //   printf("i = %d\n", i);
		for(bestimprove=0,j=0;j<=i;j++) {
			if(depth[j]<=cart_param.maxDepth&&dcmp(nodeSplit[j].improve-bestimprove)>0) {
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
  for(j=0;j<=i&&j<cart_param.nLeafNodes;j++) {
    //			printf("%d %.4lf %.4lf, ", nodeSplit[j].end-nodeSplit[j].begin, leafNode[j]->var, nodeSplit[j].improve);
    for(k=nodeSplit[j].begin;k<nodeSplit[j].end;k++) {
      fit_target[pind[0][k]] = leafNode[j]->ymeans;
    }
  }	
//		printf("\n");
	delete []nodeSplit;
	delete []leafNode;

	if(buf==NULL) {
		for(i=0;i<nDims;i++) delete[] pind[i];
		delete[] pind;
	}
//  fprintf(stderr, "fit_cart out\n");	
	return root;
}

#endif
