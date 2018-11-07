#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/time.h>
#include <sys/wait.h>
#include <signal.h>
#include <csignal>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include <cstring>
#include <sstream>
#include <errno.h>
#include <unistd.h>
#include "Board.h"

using namespace std;

typedef struct {
    long mtype;
    char mtext[200];
    int mrow;
    int mcolumn;
} message_buf;

int msqid;

void error(char const *s);
string toString(int num);
void closeQueue();
void signalHandler(int signum);
char* toCharArray(int num);

int main(){

    //register signal SIGINT and signal handler
    signal(SIGINT, signalHandler);

    cout<<"----- BATTLESHIP game -----"<<endl;
    cout<<"Insert board dimensions: ";
    int row, column, numShips;
    cin>>row>>column;
    cout<<"Insert number od ships per player: ";
    cin>>numShips;

    key_t key = getuid(); //identifikacijski broj korisnika

    //creating message queue
    msqid = msgget(key, 0600 | IPC_CREAT);
    if (msqid == -1)
        error("Error: creating message queue");

    //creating environment variable
    string env = "MSG_KEY=" + toString(msqid);
    char *cenv = new char[env.length()+1];
    strcpy(cenv, env.c_str());
    if(putenv(cenv)){ // if !=0 no success
        error("Error: creating env variable");
    }
    else{
        cout<<"ID stored in env variable:"<<getenv("MSG_KEY")<<endl;
    }

    //creating child processes --> 2 players
    pid_t pID;
    for(int playerId = 1; playerId <=2; playerId++){

        pID = fork();

        if(pID == 0){
            //Code only executed by child process
            cout<<"Child proces with pid:"<<getpid()<<endl;
            cout<<"------- Player "<<playerId<< " -------"<<endl;
            int otherPlayer = (playerId == 1)?2:1;
            //cout<<"Other player is "<<otherPlayer<<endl;

            Board board(row, column, numShips);


            message_buf sbuf; //send buffer
            sbuf.mtype = playerId;
            char text[] = "Spreman";
            int bufLength = strlen(text)+1;
            memcpy(sbuf.mtext, text, bufLength);

            if(msgsnd(msqid, &sbuf, bufLength, 0) == -1){ //getenv("MSG_KEY") umjesto msqid
                cout<<msqid<<sbuf.mtype<<sbuf.mtext<<endl;
                error("sending fail!");
            }
            else
                cout<<"Player "<<playerId<<" send message: "<<sbuf.mtext<<endl;

            message_buf rbuf; //receive buffer
            if(msgrcv(msqid,&rbuf, sizeof(rbuf)-sizeof(long), otherPlayer, 0) == -1){
                error("receiving message fail!");
            }else
                cout<<"Player "<<playerId<< " received message: "<<rbuf.mtext<< " from "<<rbuf.mtype<<endl;


//            if(playerId == 1){
//                sleep(5);
//                message_buf sbuf; //send buffer
//                sbuf.mtype = 1;
//
//                cout<<"[Player "<<playerId<<"] attack here: [row] [column]"<<endl;
//                int _row,_column;
//                cin>>_row>>_column;
//                sbuf.mrow = _row;
//                sbuf.mcolumn = _column;
//
//                char text[] = "Napadam";
//                int bufLength = strlen(text)+1;
//                memcpy(sbuf.mtext, text, bufLength);
//
//                if(msgsnd(msqid, &sbuf, bufLength, 0) == -1){ //getenv("MSG_KEY") umjesto msqid
//                    cout<<msqid<<sbuf.mtype<<sbuf.mtext<<endl;
//                    error("sending fail!");
//                }
//                else
//                    cout<<"[Player "<<playerId<<"] send msg successfully! Yeah!"<<endl;
//
//            }
//
//            if(playerId ==2){
//                message_buf rbuf2; //receive buffer
//                if(msgrcv(msqid,&rbuf2, sizeof(rbuf2)-sizeof(long), 0, 0) == -1){
//                        error("receiving message fail!");
//                }else
//                    cout<<"Player "<<playerId<< " received message: "<<rbuf.mtext<<endl;
//
//            }



            exit(1);
        }
        else if(pID < 0){
            error("Failed to fork!");
        }
        else{
            //Code only executed by parent process
            //cout<<"I'm parent and my pID is "<<getpid()<<endl;
            wait(NULL);

        }
    }
    //Code executed by both parent and child


    //close message queue
    closeQueue();

    return 0;
}

void error(char const *s){
    perror(s);
    exit(EXIT_FAILURE);
}

string toString(int num){
    stringstream convert;
    convert << num;
    return convert.str();
}

void closeQueue(){
    if( msgctl(msqid, IPC_RMID, NULL)== -1){
        error("Message queue could not be deleted.\n");
    }

    cout<< "Message queue successfully deleted."<<endl;
    exit(EXIT_SUCCESS);
}

void signalHandler(int signum){
    cout<< "\nInterrupt signal ("<<signum<<") received.\n";
    //cleanup and close up stuff here
    //terminate program
    closeQueue();
    exit(signum);
}

char* toCharArray(int num){
    string str = toString(num);
    char * cstr = new char[str.length()+1];
    strcpy(cstr, str.c_str());
    return cstr;
}