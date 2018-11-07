/*
 * main.cpp
 *
 *  Created on: Mar 1, 2017
 *      Author: vribic
 */

//#include "Board.h"
//#include "utils.h"
#include <iostream>
#include <unistd.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <memory.h>
#include <wait.h>

using namespace std;

int msg_id;

void close_buffer(int failure){
    msgctl(msg_id,IPC_RMID,NULL);
    exit(0);
}


int main(int argc, char **argv) {
    //Arguments
    if(argc!=4){
        cout<<"This program needs to get the board dimensions (two ints) and the number of ships per player(one int)."<<endl;
        exit(2);
    }
    string rows(argv[1]);
    string columns(argv[2]);
    string ships(argv[3]);
    // Open message queue
    auto msgid = msgget(getuid(),0600|IPC_CREAT);
    msg_id=msgid;
    string env_var="MSG_KEY";

    // Create env variables
    auto env_=env_var+"="+to_string(msgid);
    if(putenv(strdup((env_).c_str()))){
        cout<<"Program could not store the env. variable."<<endl;
        return -1;
    }else
        cout<<"ID stored in variable: "<<env_var<<endl;

    //Init game proceses
    for(int i=1;i<=2;i++)
        switch (fork()){
            case -1:
                cout<<"New process couldn't be created."<<endl;
                break;
            case 0: {
                cout<<getpid()<<endl;
                string cmd = "xterm -e './NOS_GAME " + to_string(i) + " "+rows+" "+columns+" "+ships+"'";
                //cout<<cmd<<endl;
                system(cmd.c_str());
                exit(1);
            }
            default:
                continue;
    }

    //Wait for players to finish
    while(waitpid(-1,NULL,0)) {
        if (errno == ECHILD)
            break;
    }
    // Close message queue
    string close_msg= msgctl(msg_id,IPC_RMID,NULL)?"Message queue didn't close successfully.":"Message queue successfully closed.";
    cout <<close_msg<<endl;
	char a;
	cin >> a;
}



