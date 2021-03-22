package com.xjw.bloomfileter;

/**
 * 分配的位数越多，误判率越低但是越占内存
 * <p>
 * 4个位误判率大概是0.14689159766308
 * <p>
 * 8个位误判率大概是0.02157714146322
 * <p>
 * 16个位误判率大概是0.00046557303372
 * <p>
 * 32个位误判率大概是0.00000021167340
 */
public enum MisjudgmentRate {
    //4 8 16 32位
    VERY_SMALL(new int[]{2, 3, 5, 7}),
    SMALL(new int[]{2, 3, 5, 7, 11, 13, 17, 19}),
    MIDDLE(new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53}),
    HIGH(new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
            101, 103, 107, 109, 113, 127, 131});

    private int[] seeds;

    private MisjudgmentRate(int[] _seeds) {
        this.seeds = _seeds;
    }

    public int[] getSeeds() {
        return seeds;
    }

    public void setSeeds(int[] seeds) {
        this.seeds = seeds;
    }
}
