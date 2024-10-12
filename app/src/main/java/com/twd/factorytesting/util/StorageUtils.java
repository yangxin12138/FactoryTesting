package com.twd.factorytesting.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 16:31 2024/10/12
 */
public class StorageUtils {

    public static  String getTotalStorageSize() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/partitions"));
            String line;
            while ((line = br.readLine())!= null) {
                if (line.contains("mmcblk0")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 3) {
                        long kbValue = Long.parseLong(parts[3]); //7667712
                        Log.i("StorageUtils", "getTotalStorageSize: kb ==" + kbValue);
                        long GB = 1024 * 1024;
                        final long[] deviceRomMemoryMap = {2*GB, 4*GB, 8*GB, 16*GB, 32*GB, 64*GB, 128*GB, 256*GB, 512*GB, 1024*GB, 2048*GB};
                        String[] displayRomSize = {"2GB","4GB","8GB","16GB","32GB","64GB","128GB","256GB","512GB","1024GB","2048GB"};
                        int i;
                        for(i = 0 ; i < deviceRomMemoryMap.length; i++) {
                            Log.i("StorageUtils", "getTotalStorageSize: deviceRomMemoryMap[] = " + deviceRomMemoryMap[i]);
                            if(kbValue <= deviceRomMemoryMap[i]) {
                                break;
                            }
                        }
                        return displayRomSize[i];
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static  String getTotalMemorySize() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
            String line;
            while ((line = br.readLine())!= null) {
                if (line.contains("MemTotal")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 2) {
                        long kbValue = Long.parseLong(parts[1]); //954312
                        Log.i("StorageUtils", "getTotalMemorySize: kb ==" + kbValue);
                        long GB = 1024 * 1024;
                        final long[] deviceRamMemoryMap = {GB,2*GB, 4*GB, 8*GB, 16*GB, 32*GB, 64*GB, 128*GB, 256*GB, 512*GB, 1024*GB, 2048*GB};
                        String[] displayRamSize = {"1GB","2GB","4GB","8GB","16GB","32GB","64GB","128GB","256GB","512GB","1024GB","2048GB"};
                        int i;
                        for(i = 0 ; i < deviceRamMemoryMap.length; i++) {
                            Log.i("StorageUtils", "getTotalMemorySize: deviceRamMemoryMap[] = " + deviceRamMemoryMap[i]);
                            if(kbValue <= deviceRamMemoryMap[i]) {
                                break;
                            }
                        }
                        return displayRamSize[i];
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
