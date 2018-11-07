//
// Created by vribic on 03.03.17..
//

#ifndef NOS_BOARD_H
#define NOS_BOARD_H
#include <iostream>
#include <assert.h>

using namespace std;

class Board{
private:
    int row,col,ship_n;
    char *board;
public:
    Board(int rows, int cols,int ship_n=1);
    ~Board();
    char& operator() (int i, int j);
    int r();
    int c();
    void print();
    bool shoot(int,int);
    bool defeat();
    int remaining_ships();
};

#endif //NOS_BOARD_H
