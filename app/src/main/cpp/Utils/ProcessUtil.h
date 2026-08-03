#ifndef HOPECLIENT_PROCESSUTIL_H
#define HOPECLIENT_PROCESSUTIL_H

#include <stdio.h>
#include <dirent.h>
#include <string.h>
#include <thread>
#include <cstring>
#include <list>


struct So {
    uintptr_t start;
    uintptr_t end;
};

So getSo(const char* Name){
    FILE *fp;
    uintptr_t start = 0;
    uintptr_t end;
    char tmp[256];
    fp = NULL;
    char fname[128] = "/proc/self/maps";
    fp = fopen(fname, "r");
    while (!feof(fp)){
        fgets(tmp, 256, fp);
        if (strstr(tmp, Name) != NULL){
            if(start==0){
                sscanf(tmp,"%lx-%lx",&start,&end);
            }else{
                sscanf(tmp,"%*lx-%lx",&end);
            }
        }
    }
    if(start!=0&&end!=0) return {start,end};

    return {0,0};
}

int getPID(std::list<char*> names){
    int id = -1;
    DIR *dir;
    FILE *fp;
    char filename[32];
    char cmdline[256];
    struct dirent *entry;
    dir = opendir("/proc");
    while ((entry = readdir(dir)) != NULL){
        id = atoi(entry->d_name);
        if (id != 0){
            sprintf(filename, "/proc/%d/cmdline", id);
            fp = fopen(filename, "r");
            if (fp){
                fgets(cmdline, sizeof(cmdline), fp);
                fclose(fp);
                for(const auto& name : names) {
                    if (strcmp(name, cmdline) == 0){
                        return id;
                    }
                }

            }
        }
    }
    closedir(dir);
    return -1;
}



#endif //HOPECLIENT_PROCESSUTIL_H
