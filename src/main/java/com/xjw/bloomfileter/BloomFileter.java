package com.xjw.bloomfileter;

import sun.applet.Main;

import java.io.*;
import java.util.BitSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 判断某个字符串是否不在缓存中或者可能在缓存中
 *
 */
public class BloomFileter implements Serializable {

    private static final long serialVersionUID = -5221305273707291280L;

    private final int[] seeds;

    private final int size;

    private final BitSet notebook;

    private final MisjudgmentRate rate;

    private final AtomicInteger userCount = new AtomicInteger(0);

    private final Double autoClearRate;

    public BloomFileter(int dataCount){
        this(MisjudgmentRate.MIDDLE,dataCount,null);
    }

    /**
     *
     * @param rate 误判率
     * @param dataCount 数据处理规模 1000/10k/100k
     * @param autoClearRate
     *                自动清空过滤器内部信息的使用比率，传null则表示不会自动清理，
     * 	 *            当过滤器使用率达到100%时，则无论传入什么数据，都会认为在数据已经存在了
     * 	 *            当希望过滤器使用率达到80%时自动清空重新使用，则传入0.8
     */
    public BloomFileter(MisjudgmentRate rate, int dataCount, Double autoClearRate){
        long bitSize = rate.getSeeds().length * dataCount;
        if(bitSize < 0 || bitSize > Integer.MAX_VALUE){
            throw new RuntimeException("文件太大");
        }
        this.rate = rate;
        seeds = rate.getSeeds();
        size = (int) bitSize;
        notebook = new BitSet(size);
        this.autoClearRate = autoClearRate;
    }
    
    public void add(String data){
        checkNeedClear();
        for (int i = 0; i < seeds.length; i++) {
            int index = hash(data, seeds[i]);
            setTrue(index);
        }
    }

    //校验数据知否不存在
    public boolean check(String data){
        for (int i = 0; i < seeds.length; i++) {
            int index = hash(data, seeds[i]);
            if (!notebook.get(index)) {
                return false;
            }
        }
        return true;
    }

    public boolean addIfNotExist(String data){
        checkNeedClear();
        int[] indexs = new int[seeds.length];
        boolean exists = true;
        int index;
        for (int i = 0; i < seeds.length; i++) {
            indexs[i] = index = hash(data,seeds[i]);
            if(exists){
                if(!notebook.get(index)){
                    exists = false;
                    for (int j = 0; j <= i; j++) {
                        setTrue(indexs[j]);
                    }
                }
            }else {
                setTrue(index);
            }
        }
        return exists;
    }

    private void checkNeedClear() {
        if(autoClearRate == null){
            return;
        }
        if(getUseRate() >= autoClearRate){
            synchronized (this) {
                if (getUseRate() >= autoClearRate) {
                    notebook.clear();
                    userCount.set(0);
                }
            }
        }
    }

    public void setTrue(int index){
        userCount.incrementAndGet();
        notebook.set(index,true);
    }

    private int hash(String data, int seeds){
        char[] value = data.toCharArray();
        int hash = 0;
        if(value.length > 0){
            for (int i = 0; i < value.length; i++) {
                hash = i*hash + value[i];
            }
        }
        hash = hash *seeds % size;
        return Math.abs(hash);
    }

    public double getUseRate(){
        return (double) userCount.intValue();
    }

    //对象写入文件
    public void saveFilterToFile(String path){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //文件读取对象
    public static BloomFileter readFilterFromFile(String path){
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(path));
            return (BloomFileter) objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 清空过滤器中的记录信息
     */
    public void clear(){
        userCount.set(0);
        notebook.clear();
    }

    public MisjudgmentRate getRate(){
        return rate;
    }

    public static void main(String[] args) {
//        BloomFileter fileter = new BloomFileter(7);
//        System.out.println(fileter.addIfNotExist("1111111111111"));
//        System.out.println(fileter.addIfNotExist("2222222222222222"));
//        System.out.println(fileter.addIfNotExist("3333333333333333"));
//        System.out.println(fileter.addIfNotExist("444444444444444"));
//        System.out.println(fileter.addIfNotExist("5555555555555"));
//        System.out.println(fileter.addIfNotExist("6666666666666"));
//        System.out.println(fileter.addIfNotExist("1111111111111"));
//        fileter.saveFilterToFile("C:\\Users\\john\\Desktop\\1111\\11.obj");
//        fileter = readFilterFromFile("C:\\Users\\john\\Desktop\\111\\11.obj");
//        System.out.println(fileter.getUseRate());
//        System.out.println(fileter.addIfNotExist("1111111111111"));
        BitSet s = new BitSet();
        s.set(0,1);
        System.out.println(s.get(2));
    }
}
