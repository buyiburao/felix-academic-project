#include <cstdio>
#include <cassert>
#include <cstring>
#include <iostream>

#include "tree.h"
#include "common.h"

using namespace std;


double apply_cart(const FVector& x, const Node* root) {
  return apply_cart(x.size(), (const double*)x, root); 
}


void apply_cart(const DataSet& data, const Node* root, double* y) {
	int i;
	for(i=0;i<data.data_num;i++) {
		y[i] = apply_cart(data.dim_num, data.feature[i], root);
	}
}


void save_tree(FILE* fp, const Node* const root) {
	if(root==NULL) return;
	fprintf(fp, "[\n");
	fprintf(fp, "attribute %d\nvalue %lf\nymeans %lf\nvar %lf\n", root->attribute, root->value, root->ymeans, root->var);
  fprintf(fp, "size %d\n", root->size);
	if(root->left==NULL) assert(root->right==NULL);
	if(root->right==NULL) assert(root->left==NULL);
	save_tree(fp, root->left);
	save_tree(fp, root->right);
	
	fprintf(fp, "]\n");
}

static char buf[1000];
static Node* _load_tree(FILE* fp) {
	Node *root = new Node();

/*	if(strcmp(buf, "[")!=0) {
		printf("File format error!");
		return NULL;
	}*/
	int ret = fscanf(fp, " attribute %d\nvalue %lf\nymeans %lf\nvar %lf\n",
		 &root->attribute, &root->value, &root->ymeans, &root->var);
  assert(ret==4);
  ret = fscanf(fp, "size %d", &root->size);
  assert(ret==1);
	ret = fscanf(fp, " %s", buf);
  assert(ret==1);
	if(strcmp(buf, "]")!=0) {
		assert(strcmp(buf, "[")==0);
		root->left = _load_tree(fp);
		ret = fscanf(fp, " %s", buf);
    assert(ret==1);
		assert(strcmp(buf, "[")==0);
		root->right = _load_tree(fp);
		ret = fscanf(fp, " %s", buf);
    assert(ret==1);
	} else {
		root->left = root->right = NULL;
	}		
	return root;
}

Node* load_tree(FILE *fp) {
	int ret = fscanf(fp, "%s", buf);	
  assert(ret==1);
	assert(strcmp(buf, "[")==0);
	return _load_tree(fp);
}

void free_tree(Node* root) {
	if(root==NULL) return;
	free_tree(root->left);
	free_tree(root->right);
	delete root;
}

void save_tree(std::ostream& f, const Node* const root) {
	if(root==NULL) return;
	f << "[\n" << "attribute " << root->attribute;
  f << "\nvalue " << root->value << "\nymeans " << root->ymeans << "\nvar " << root->var << "\n";
  f << "size " << root->size << "\n";
  f << "improve " << root->improve << "\n";
	if(root->left==NULL) assert(root->right==NULL);
	if(root->right==NULL) assert(root->left==NULL);
	save_tree(f, root->left);
	save_tree(f, root->right);
  f << "]\n";
}

static Node* _load_tree(std::istream& f) {
	Node *root = new Node();
  string buf, val;

  while(f >> buf) {
    if(buf.compare("[")==0 || buf.compare("]")==0) break;
    if(buf.compare("attribute")==0) {
      f >> root->attribute;
      continue;
    }
    if(buf.compare("value")==0) { 
      f >> root->value;
      continue;
    }
    if(buf.compare("ymeans")==0) {
      f >> root->ymeans;
      continue;
    }
    if(buf.compare("var")==0) {
      f >> root->var;
      continue;
    }
    if(buf.compare("size")==0) {
      f >> root->size;
      continue;
    }
    if(buf.compare("improve")==0) {
      f >> root->improve;
      continue;
    }
    cout << "Error: unknow fields for root!" << buf << endl;
    delete root;
    return NULL;
  }

/*
  f >> buf >> root->attribute;
  assert(buf.compare("attribute")==0);

  f >> buf >> root->value;
  assert(buf.compare("value")==0);

  f >> buf >> root->ymeans;
  assert(buf.compare("ymeans")==0);
  
  f >> buf >> root->var;
  assert(buf.compare("var")==0);

  f >> buf >> root->size;
  assert(buf.compare("size")==0);
  
  f >> buf;
*/
	if(buf.compare("]")!=0) {
		assert(buf.compare("[")==0);
		root->left = _load_tree(f);
    assert(root->left!=NULL);
    f>>buf;		
		assert(buf.compare("[")==0);
		root->right = _load_tree(f);
    assert(root->right!=NULL);
    f>>buf;
	} else {
		root->left = root->right = NULL;
	}		
	return root;  
}

Node* load_tree(std::istream& f) {
  string buf;
  f >> buf;
//  cout << buf << endl;
	assert(buf.compare("[")==0);
	return _load_tree(f);  
}

double cal_dependent(const Node* root, int featureid) {
  if(root->left==NULL) return 0.0;  
  assert(root->right!=NULL);

  return (root->attribute==featureid?root->improve:0.0)+cal_dependent(root->left, featureid)+cal_dependent(root->right, featureid);

}
