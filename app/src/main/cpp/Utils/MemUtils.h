#ifndef HOPECLIENT_MEMUTILS_H
#define HOPECLIENT_MEMUTILS_H

template <unsigned int IIdx, typename TRet, typename... TArgs>
static inline auto CallVFunc(void* thisptr, TArgs... argList) -> TRet {
    using Fn = TRet(__thiscall*)(void*, decltype(argList)...);
    return (*static_cast<Fn**>(thisptr))[IIdx](thisptr, argList...);
}


#endif //HOPECLIENT_MEMUTILS_H
