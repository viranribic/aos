//
// Created by vribic on 03.03.17..
//
#include <iostream>
#include <unistd.h>
#include "Board.h"
#include <sys/ipc.h>
#include <sys/msg.h>
#include <signal.h>
#include <cstring>
#include <sstream>
#include <vector>
#include <iterator>



using namespace std;

int msg_id;
typedef struct {
    long mytype;
    int i,j;
    bool hit,defeat;
    char text[255];
} game_msg;

template<typename Out>
void split(const std::string &s, char delim, Out result) {
    std::stringstream ss;
    ss.str(s);
    std::string item;
    while (std::getline(ss, item, delim)) {
        *(result++) = item;
    }
}
std::vector<std::string> split(const std::string &s, char delim) {
    std::vector<std::string> elems;
    split(s, delim, std::back_inserter(elems));
    return elems;
}

void close_buffer(int failure){
    msgctl(msg_id,IPC_RMID,NULL);
    exit(0);
}

void receive_msg(int player_id,game_msg &msg);
void send_msg(int player_id, game_msg &msg,bool hit,bool defeat);
void admit_defeat(int player_id,game_msg & msg);
void msg_data(int player_id,game_msg &msg,bool first_turn);

int main(int argc,char **argv){
    sigset(SIGINT,close_buffer);
    if(argc!=5){
        cout<<"This program needs to get the player id,board rows, board columns and the number of ships."<<endl;
        exit(1);
    }

    int player_id =stoi(string(argv[1]));
    long int other_player_id=(player_id==1)?2L:1L;
    int rows=stoi(string(argv[2]));
    int cols=stoi(string(argv[3]));
    int ships=stoi(string(argv[4]));


    msg_id=stoi(string(getenv("MSG_KEY")));

    cout<<"Process "<<getpid()<<" acquired key: "<<msg_id<<endl;
    cout<<"Player"<<player_id<<" with board "<<rows<<"x"<<cols<<" has "<<ships<<" ships."<<endl;
    srand((unsigned int)(getpid()*time(NULL)));

    //Show board
    Board b(rows,cols,ships);
    b.print();

    game_msg msg;
    //Start message
    if(player_id==1){
        //Make first move
        send_msg(player_id,msg,false,false);
    }
    bool first_turn=true;
    while(1){
        if (system("CLS")) system("clear");
        //Get message or wait
        receive_msg(player_id,msg);
        if(msg.defeat){
            cout<<"Player"<<player_id<<" won!"<<endl;
            break;
        }
        msg_data(player_id,msg,first_turn);
        //Look where the process shoot
        bool hit=b.shoot(msg.i,msg.j);
        bool defeat=b.defeat();
        cout<<"Player"<<other_player_id<<" shoot at "<<msg.i<<" "<<msg.j<<" and it was a "<<((hit)?"hit!":"miss!")<<endl;
        cout<<"I am left with "<<b.remaining_ships()<<" ships."<<endl<<endl;
        cout<<"Current state of my board:"<<endl;
        b.print();
        if(defeat) {
            cout << "That blow destroyed all my ships!" << endl<<"Player "<<other_player_id<<" wins!"<<endl;
            admit_defeat(player_id,msg);
            break;
        }
        //Make your move
        send_msg(player_id,msg,hit,defeat);
        first_turn=false;
        cout<<endl<<endl;
    }

    char a;
    cin>>a;
    //end
    exit(0);

}

void send_msg(int player_id,game_msg& msg,bool hit, bool defeat){
    msg.mytype=player_id;
    cout<<"Your turn."<<endl<<"Guess the next position:"<<endl;
    //Read coords
    string line;
    getline(cin,line);
    vector<string> coords=split(line,' ');
    int i=stoi(coords[0]),j=stoi(coords[1]);


    //Read message
    //cout<<endl<<"Msg for other player?[Enter not to send anything]";
    //getline(cin,line);
    //Prep and send message
    msg.i=i;
    msg.j=j;
    msg.hit=hit;
    msg.defeat=defeat;
    line=to_string(i)+":"+to_string(j)+":"+line;
    memcpy(msg.text,line.c_str(),strlen(line.c_str())+1);
    if(msgsnd(msg_id,&msg,sizeof(msg),0)==-1){
        cerr<<"There was an error sending the first message."<<endl;
        exit(1);
    }
}

void admit_defeat(int player_id,game_msg & msg){
    msg.mytype=player_id;
    msg.defeat=true;
    if(msgsnd(msg_id,&msg,sizeof(msg),0)==-1){
        cerr<<"There was an error sending the first message."<<endl;
        exit(1);
    }
}


void  receive_msg(int player_id,game_msg& msg){
    long int other_player_id=(player_id==1)?2L:1L;
    cout<<"Waiting for other player..."<<endl;
    while(1){
        if(msgrcv(msg_id,&msg,sizeof(msg),other_player_id,0)==-1){
            cerr<<"There was an error sending the first message."<<endl;
            cout<<"Waiting for other player..."<<endl;
            continue;
        }
        break;
    }

}

void msg_data(int player_id,game_msg &msg,bool first_turn){
    long int other_player_id=(player_id==1)?2L:1L;
//    if(!string(msg.text).compare(""))
//        cout<<"Player "<<other_player_id<<" says: "<<msg.text<<endl;
    if(first_turn){
        return;
    }
    if(msg.hit)
        cout<<"Successful hit!"<<endl;
    else
        cout<<"Miss!"<<endl;
}

//Kako msgrcv radi interno da pri izlasku uz funkcije baca stack smashing detection?
