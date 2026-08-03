#ifndef HOPECLIENT_STRINGUTIL_H
#define HOPECLIENT_STRINGUTIL_H

void byteToChar(const unsigned char* byteCode,char* strCode,int codeLen){
    const char hexChars[] = "0123456789ABCDEF";
    for (int i = 0; i < codeLen; i++) {
        strCode[i * 2] = hexChars[(byteCode[i] >> 4) & 0x0F];
        strCode[i * 2 + 1] = hexChars[byteCode[i] & 0x0F];
    }

}



bool cmpStrCode(const char* code,const char *readStr,int cmpLen){
    for(int i =0;i<cmpLen;i++){
        if(code[i]=='?'){
            continue;
        }
        if(code[i]!=readStr[i]){
            return false;
        }
    }
    return true;
}


#endif //HOPECLIENT_STRINGUTIL_H
