//
// Created by vribic on 03.03.17..
//

#include "Board.h"

Board::Board(int rows, int cols,int ship_n_) :row(rows), col(cols),ship_n(ship_n_) {
    board = new char[rows*cols];
    for (int i = 0; i<rows; i++)
        for (int j = 0; j<cols; j++)
            (*this)(i, j) = '-';

    int interval = (*this).r()*(*this).c();
    assert(ship_n_ <= interval);
    while (ship_n_-->0) {
        int i = rand() % (*this).r();
        int j = rand() % (*this).c();
        //Find the next free spot
        while ((*this)(i, j) == 'o') {
            i++;
            if (i >= (*this).r()) {
                i = 0;
                j++;
                if (j >= (*this).c()) {
                    i = 0;
                    j = 0;
                }
            }
        }
        (*this)(i, j) = 'o';
    }
}
Board::~Board() {
    if (board)
        delete board;
}
char& Board::operator() (int i, int j) {
    assert(i >= 0 && i<row && j >= 0 && j<col);
    return board[i*row + j];
}
int Board::r() {
    return row;
}
int Board::c() {
    return col;
}
void Board::print() {
    for (int i = 0; i<row; i++) {
        for (int j = 0; j<col; j++)
            cout << board[i*row + j] << " ";
        cout << endl;
    }
}

bool Board::shoot(int i, int j)
{
    if ((*this)(i, j) == 'o') {
        (*this)(i, j) = '-';
        ship_n--;
        return true;
    }
    return false;
}

bool Board::defeat(){
    return ship_n==0;
}

int Board::remaining_ships() {
    return ship_n;
}

