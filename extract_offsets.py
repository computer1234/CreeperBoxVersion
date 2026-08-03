#!/usr/bin/env python3
"""
extract_offsets.py - 从 Map 文件提取需要混淆的函数偏移地址并自动写入 SSP 文件
"""

import re
import os

# 需要混淆的函数关键字
# 注意: getSessionTimeout (12字节) 和 getMaxTimeDrift (8字节) 太短，不适合混淆
FUNCTIONS_TO_OBFUSCATE = [
    "performSecurityCheck",
    "waitForVerification", 
    "shouldContinueRendering",
    # "getSessionTimeout",    # 太短 (12 bytes)
    # "getMaxTimeDrift",      # 太短 (8 bytes)
    "validateRenderingConfig",
    "isEnvironmentClean",
    "getTextureSampling",
    "getAnisotropicLevel",
    "getMipmapBias",
    "getShadowQuality",
    "getCurrentTimeMs",
    "getDecayFactor",
    "performFullScan",
    "verifyAllBlocks",
    "getIntegrityFactor",
]

# 基础路径
BASE_DIR = r"c:\Users\1\Desktop\Android\CreeperBoxVersion\app\build\intermediates\cxx\Debug\1i435f36\obj"

# 架构列表
ARCHITECTURES = ["arm64-v8a", "armeabi-v7a"]


def extract_offsets(map_file):
    """从 map 文件提取偏移地址"""
    with open(map_file, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()
    
    found = []  # [(addr, func_name), ...]
    
    for func_name in FUNCTIONS_TO_OBFUSCATE:
        # 匹配格式: 地址 + 函数名
        pattern = rf'([0-9a-fA-F]+)\s+[0-9a-fA-F]+\s+[0-9a-fA-F]+\s+\d+.*?{func_name}'
        match = re.search(pattern, content)
        
        if match:
            addr = int(match.group(1), 16)
            if addr > 0x1000:
                found.append((addr, func_name))
                continue
        
        # 尝试另一种格式
        for line in content.split('\n'):
            if func_name in line:
                match2 = re.search(r'^[\s]*([0-9a-fA-F]+)', line)
                if match2:
                    addr = int(match2.group(1), 16)
                    if addr > 0x1000:
                        found.append((addr, func_name))
                        break
    
    return found


def generate_ssp(offsets):
    """生成 SSP 文件内容"""
    lines = [
        '<?xml version="1.0" encoding="utf-8"?>',
        '<ssprotect>',
        '\t<base_info>',
        '\t\t<app>Virbox Protector 3 Trial</app>',
        '\t\t<version>3.5.0.21419</version>',
        '\t</base_info>',
        '\t<option>',
        '\t\t<import>1</import>',
        '\t\t<zip>1</zip>',
        '\t\t<memory_check>1</memory_check>',
        '\t\t<strip_symtab>1</strip_symtab>',
        '\t\t<anti_debugging>1</anti_debugging>',
        '\t</option>',
        '\t<file>',
        '\t\t<path>protected\\libcreeperbox.so</path>',
        '\t</file>',
        '\t<sign>',
        '\t\t<sns>0</sns>',
        '\t\t<snk_path></snk_path>',
        '\t</sign>',
        '\t<function>',
        '\t\t<SENSENODEFUNC1 type="function" name="JNI_OnLoad">',
        '\t\t\t<flags>2</flags>',
        '\t\t</SENSENODEFUNC1>',
    ]
    
    # 添加提取的函数
    for i, (addr, func_name) in enumerate(offsets, start=2):
        lines.append(f'\t\t<SENSENODEFUNC{i} type="function" name="SENSE{addr:08x}">')
        lines.append('\t\t\t<flags>1</flags>')
        lines.append(f'\t\t</SENSENODEFUNC{i}>')
    
    lines.append('\t</function>')
    lines.append('</ssprotect>')
    lines.append('')
    
    return '\n'.join(lines)


def main():
    for arch in ARCHITECTURES:
        arch_dir = os.path.join(BASE_DIR, arch)
        map_file = os.path.join(arch_dir, "libcreeperbox.map")
        ssp_file = os.path.join(arch_dir, "libcreeperbox.so.ssp")
        
        if not os.path.exists(map_file):
            print(f"[!] 跳过 {arch}: map 文件不存在")
            continue
        
        print(f"\n[*] 处理架构: {arch}")
        print(f"    Map 文件: {map_file}")
        
        # 提取偏移
        offsets = extract_offsets(map_file)
        
        print(f"    找到 {len(offsets)} 个函数:")
        for addr, name in offsets:
            print(f"        0x{addr:08X}  {name}")
        
        # 生成并写入 SSP
        ssp_content = generate_ssp(offsets)
        with open(ssp_file, 'w', encoding='utf-8') as f:
            f.write(ssp_content)
        
        print(f"    [✓] SSP 文件已写入: {ssp_file}")
    
    print("\n[完成] 所有架构处理完毕!")


if __name__ == "__main__":
    main()
