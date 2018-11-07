/*
 * main.cpp
 *
 *  Created on: Mar 1, 2017
 *      Author: vribic
 */

#include <iostream>
#include <unistd.h>
#include <wait.h>
#include <vector>
#include <queue>

#define READ_PIPE   0
#define WRITE_PIPE  1

#define TYPE_REQUEST 0
#define TYPE_RESPONSE 1
#define TYPE_EXIT 2
#define TYPE_STOP 3


typedef struct sync_msg{
    int type;
    int sender_pid;
    int sender_id;
    int Ti;
    sync_msg(){};
    sync_msg(int type_,int sender_pid_,int sender_id_,int Ti_):type(type_),sender_pid(sender_pid_),sender_id(sender_id_),Ti(Ti_){};
    //Should be inverted so that the lower numbers come first
    bool operator<(const sync_msg& rhs) const{
        if(Ti==rhs.Ti)
            return sender_pid>rhs.sender_pid;
        else
            return Ti>rhs.Ti;
    }

    //Should be inverted so that the lower numbers come first
    bool operator>(const sync_msg& rhs)const{
        if(Ti==rhs.Ti)
            return sender_pid<rhs.sender_pid;
        else
            return Ti<rhs.Ti;
    }

    bool operator==(const sync_msg& rhs) const{
        return Ti==rhs.Ti && sender_pid==rhs.sender_pid;
    }

} sync_msg;

using namespace std;

//Process
void worker_process(int id,const vector<int*> pipelines,const int repeat_loop,const int count_to);
//Sync
void critical_in(int id,const vector<int*> pipelines,int& Ci,priority_queue<sync_msg>& msg_q,vector<bool> working_processes);
void critical_out(int id,const vector<int*> pipelines,int& Ci,priority_queue<sync_msg>& msg_q);
//Sync functions
void sync_request(int &id,sync_msg& msg,priority_queue<sync_msg>& msg_q,int&Ci,vector<int*> pipelines);
void sync_response(int&id,sync_msg& msg,int&Ci,bool* other_responses);
void sync_exit(int &id,sync_msg& msg,priority_queue<sync_msg>& msg_q);
void sync_stop(int &id,sync_msg& msg,priority_queue<sync_msg>& msg_q,vector<bool>working_processes,bool * other_responses);

//Utilities
bool all_other_responses_received(bool* responses, unsigned long size);
void broadcast_finish(int id,const vector<int*> pipelines,vector<bool> working_processes);
void broadcast(int id,sync_msg& msg, vector<int*> pipes);
void init_other_responses(int id,vector<int*> pipelines,vector<bool>working_processes,bool* other_responses);
void remove_by_id(int &id,priority_queue<sync_msg> msg_q);

//Prints
void print_queue(int id,priority_queue<sync_msg>& q);

int main(int argc, char **argv) {
    //Arguments
    if(argc!=4){
        cout<<"This program needs to get the number of jobs to start, job loop repetitions and the number to count to."<<endl;
        exit(2);
    }

    int jobs_n=stoi(string(argv[1]));
    int repeat_loop=stoi(string(argv[2]));
    int count_to=stoi(string(argv[3]));

    //Init piplines
    vector<int*> pipelines;
    for(int i=0;i<jobs_n;i++){
        int* pdf=new int[2];
        if(pipe(pdf)== -1){
            cout<<"Pipieline coudn't be created!"<<endl;
            exit(1);
        }
        pipelines.push_back(pdf);
    }


    //Init game proceses
    for(int i=0;i<jobs_n;i++){
        //Create process pipe
        switch (fork()){
            case -1:
                cout<<"New process couldn't be created."<<endl;
                break;
            case 0: {
                close(pipelines[i][WRITE_PIPE]);
                worker_process(i,pipelines,repeat_loop,count_to);
                //cout<<"Process "<<i<<" done."<<endl<<endl;
                exit(0);//exit(0);
            }
            default:{
                close(pipelines[i][READ_PIPE]);
                //continue;
            }
        }
    }

    //Wait for players to finish

    while(waitpid(-1,NULL,0)) {
        if (errno == ECHILD)
            break;
    }

    //Delete all pipelines
    for(int i=0;i<pipelines.size();i++) {
        close(pipelines[i][0]);
        close(pipelines[i][1]);
        delete pipelines[i];
    }
    // Close message queue
    cout<<"Program finished. Press any key."<<endl;
    char a;
	cin >> a;
}




// Implementations...
void worker_process(int id,const vector<int*> pipelines,const int repeat_loop,const int count_to){
    srand(getpid());
    cout<<"Here is process "<<getpid()<<" with id "<<id<<endl;
    int Ci=0;
    priority_queue<sync_msg> msg_q;
    //Init the flags for managing working threads
    vector<bool> working_processes;
    for(int i=0;i<pipelines.size();i++)
        working_processes.push_back(true);


    for(int i=0;i<repeat_loop;i++){
        sleep(rand()%3);
        critical_in(id,pipelines,Ci,msg_q,working_processes);
        for(int j=0;j<count_to;j++){
            cout<<"Process "<<id<<"("<<getpid()<<"):: Loop: "<<i<<" Repeat:"<<j<<endl;
            sleep(1);
        }
        cout<<endl;
        critical_out(id,pipelines,Ci,msg_q);
    }
    broadcast_finish(id,pipelines, working_processes);
}

void critical_in(int id,const vector<int*> pipelines,int& Ci,priority_queue<sync_msg>& msg_q,vector<bool> working_processes){
    //Generate request ...and add it to your queue, ordered by timestamp
    sync_msg msg_request(TYPE_REQUEST,getpid(),id,Ci);
    //...and add it to your queue, ordered by timestamp
    msg_q.push(msg_request);

    broadcast(id,msg_request,pipelines);

    //Wait for replies from all nodes
    bool other_responses[pipelines.size()];
    init_other_responses(id,pipelines,working_processes,other_responses);

    while(1) {
        sync_msg next_msg;
        (void) read(pipelines[id][READ_PIPE], &next_msg, sizeof(next_msg));
        if(next_msg.type==TYPE_REQUEST){
            sync_request(id,next_msg,msg_q,Ci,pipelines);

        } else if(next_msg.type==TYPE_RESPONSE){
            sync_response(id,next_msg,Ci,other_responses);

        } else if(next_msg.type==TYPE_EXIT){
            sync_exit(id,next_msg,msg_q);

        }else if(next_msg.type==TYPE_STOP){
            sync_stop(id,next_msg,msg_q,working_processes,other_responses);
            //print_queue(id,msg_q);

        }else{
            //cerr << "Invalid message type: "<<next_msg.type<<" . " << getpid() << endl;
            exit(-1);
        }

        if (all_other_responses_received(other_responses, pipelines.size())) {
            //If all other responses are true check if your are next

            sync_msg top_msg=msg_q.top();
            if(top_msg.sender_pid==getpid()){
                //Leave the queue
//                cout<<"Process "<<id<<" entering critical section."<<endl;
                return;
            }
        }
    }
}

bool all_other_responses_received(bool* responses, unsigned long size){
    for(int i=0;i<size;i++)
        if(!responses[i])
            return false;
    return true;
}

void critical_out(int id,const vector<int*> pipelines,int& Ci,priority_queue<sync_msg>& msg_q){
    sync_msg msg_exit=msg_q.top();
    msg_q.pop();
    msg_exit.type=TYPE_EXIT;
    broadcast(id,msg_exit,pipelines);
}

void broadcast_finish(int id,const vector<int*> pipelines,vector<bool> working_processes){
    sync_msg msg_exit;
    msg_exit.type=TYPE_STOP;
    msg_exit.sender_id=id;
    msg_exit.Ti=-1;
    msg_exit.sender_pid=getpid();
    //Send this msg to all processes
    for(int i=0;i<pipelines.size();i++){
        if(i == id || !working_processes[i]) continue;
        (void)write(pipelines[i][WRITE_PIPE],&msg_exit,sizeof(msg_exit));
    }
}

void print_queue(int id,priority_queue<sync_msg>& q){
    // The latest process exited the critical section
    priority_queue<sync_msg> help_q;
    int index=0;
    while(!q.empty()){
        sync_msg msg=q.top();
        cout<<id<<"@"<<index<<"->"<<msg.type<<":"<<msg.sender_pid<<":"<<msg.sender_id<<":"<<msg.Ti<<endl;

        help_q.push(msg);
        q.pop();
        index++;
    }
    while(!help_q.empty()){
        q.push(help_q.top());
        help_q.pop();
    }
}

void broadcast(int id,sync_msg& msg, vector<int*> pipelines){
    //Send this msg to all processes
    for(int i=0;i<pipelines.size();i++){
        if(i == id) continue;
        (void)write(pipelines[i][WRITE_PIPE],&msg,sizeof(msg));
        //cout<<"Process "<<id<<" send request to "<< i<<endl;
    }
}

void init_other_responses(int id,vector<int*> pipelines,vector<bool>working_processes,bool *other_responses){
    for(int i=0;i<pipelines.size();i++){
        if(working_processes[i])
            other_responses[i]=false; //waiting to receive
        else
            other_responses[i] = true;
    }
    other_responses[id]=true;
}

void sync_request(int& id,sync_msg& msg,priority_queue<sync_msg>& msg_q,int&Ci,vector<int*> pipelines){
    //Someone sent you a request
    //Push message
    msg_q.push(msg);
    Ci=max(Ci,msg.Ti)+1;
    //cout<<"Process "<<id<<" received request from "<< next_msg.sender_id<<endl;
//            print_queue(id,msg_q);

    //Return response
    sync_msg msg_response;
    msg_response.type = TYPE_RESPONSE;
    msg_response.sender_pid=getpid();
    msg_response.Ti=Ci;
    msg_response.sender_id=id;
    (void) write(pipelines[msg.sender_id][WRITE_PIPE], &msg_response, sizeof(msg_response));
    //cout<<"Process "<<id<<" send response to "<< next_msg.sender_id<<endl;
}

void sync_response(int&id,sync_msg& msg,int&Ci,bool* other_responses){
    //My position was acknowledged!
    other_responses[msg.sender_id] = true;
    Ci=max(Ci,msg.Ti)+1;
    //cout<<"Process "<<id<<" received response from "<< next_msg.sender_id<<endl;

}

void sync_exit(int &id,sync_msg& msg,priority_queue<sync_msg>& msg_q){
    // The latest process exited the critical section
//            priority_queue<sync_msg> help_q;
//            while(msg_q.top().sender_id!=next_msg.sender_id){
//                help_q.push(msg_q.top());
//                msg_q.pop();
//                cerr<<"Msg not first!"<<endl;
//            }
    msg_q.pop();
//            while(!help_q.empty()){
//                msg_q.push(help_q.top());
//                help_q.pop();
//            }
    //TODO clean for print
    //cout<<"Process "<<id<<" received exit from "<< msg.sender_id<<endl;
    //print_queue(id,msg_q);
}

void sync_stop(int &id,sync_msg& msg,priority_queue<sync_msg>& msg_q,vector<bool>working_processes,bool * other_responses){
    //This process should die
    //cout<<"Process "<<id<<" received response from "<< next_msg.sender_id<<endl;
    working_processes[msg.sender_id]=false;
    other_responses[msg.sender_id] = true;
    remove_by_id(msg.sender_id,msg_q);
    //TODO clean for print
    //cout<<"Process "<<id<<" received stop from "<< msg.sender_id<<endl;
}

void remove_by_id(int &id,priority_queue<sync_msg> msg_q){
        // The latest process exited the critical section
            priority_queue<sync_msg> help_q;
            while(!msg_q.empty()){
                sync_msg msg=msg_q.top();
                if(msg.sender_id==id) continue;
                help_q.push(msg);
                msg_q.pop();
            }

            while(!help_q.empty()){
                msg_q.push(help_q.top());
                help_q.pop();
            }
}

