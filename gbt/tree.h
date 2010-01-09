#ifndef __GBTREE__TREE
#define __GBTREE__TREE
#include <cstdlib>
#include <iostream>
#include <vector>
#include <vectors.h>
#include <common.h>

struct Node {
	int attribute;
  int size;
	double value;
	double ymeans;
  double improve;
	Node *left, *right;
	double var;
	
	Node() : attribute(-1), value(0), left(NULL), right(NULL) {}
  Node& getLeft() { return *left; }
  Node& getRight() { return *right; }
  virtual ~Node() {
    if(left!=NULL) delete left;
    if(right!=NULL) delete right;
  }
};

template <typename T>
static double apply_cart(int dim_num, const T& data, const Node* root) {
	const Node* temp = root;
  assert(temp!=NULL);
	while(temp!=NULL) {
//		printf("temp->att %d value %lf\n", temp->attribute, temp->value);
		if(temp->left==NULL||temp->right==NULL) return temp->ymeans;
		if(dcmp(data[temp->attribute]-temp->value)<0) {
			temp = temp->left;
		} else {
			temp = temp->right;
		}
	}
	assert(false);
	return -1.0;
}


//void apply_cart(int data_num, int dim_num, const double* data, const Node* root, double* y);
void apply_cart(const DataSet& dataset, const Node* root, double* y);
void save_tree(FILE* fp, const Node* const root);
Node* load_tree(FILE* fp);
//void free_tree(Node* root);

void save_tree(std::ostream& f, const Node* const root);
Node* load_tree(std::istream& f);

double apply_cart(const FVector& x, const Node* root);

double cal_dependent(const Node* root, int featureid);
#endif
