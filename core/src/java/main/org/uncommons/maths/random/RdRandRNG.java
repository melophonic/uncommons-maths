package org.uncommons.maths.random;

import java.util.Random;

import net.nullschool.util.DigitalRandom;

import org.uncommons.maths.binary.BinaryUtils;

/**
 * <p>RdRand implementation, using the RNG on board Intel Ivy Bridge processors</p>
 *
 * @author Christopher McMahon
 */
public class RdRandRNG extends Random 
{
    private static final int SEED_SIZE_BYTES = 8;

    private final byte[] seed;

    private final DigitalRandom rdRand = new DigitalRandom();

    /**
     * Creates a new RNG and seeds it using the default seeding strategy.
     */
    public RdRandRNG()
    {
        this(DefaultSeedGenerator.getInstance().generateSeed(SEED_SIZE_BYTES));
    }


    /**
     * Seed the RNG using the provided seed generation strategy.
     * @param seedGenerator The seed generation strategy that will provide
     * the seed value for this RNG.
     * @throws SeedException If there is a problem generating a seed.
     */
    public RdRandRNG(SeedGenerator seedGenerator) throws SeedException
    {
        this(seedGenerator.generateSeed(SEED_SIZE_BYTES));
    }


    /**
     * Creates an RNG and seeds it with the specified seed data.
     * @param seed The seed data used to initialise the RNG.
     */
    public RdRandRNG(byte[] seed)
    {
        super(createLongSeed(seed));
        this.seed = seed.clone();        
        //rdRand.setSeed(seed); Intel's DRNG implementation does not support setting a seed.

    }


    /**
     * Helper method to convert seed bytes into the long value required by the
     * super class.
     */
    private static long createLongSeed(byte[] seed)
    {
        if (seed == null || seed.length != SEED_SIZE_BYTES)
        {
            throw new IllegalArgumentException("RdRand RNG requires a 64-bit (8-byte) seed.");
        }
        return BinaryUtils.convertBytesToLong(seed, 0);
    }


    /**
     * {@inheritDoc}
     */    
    public byte[] getSeed()
    {
        return seed.clone();
    }
    
    
    @Override
    public int next(int bits)
    {
    	int result = rdRand.nextInt();
    	return result >>> (32 - bits);
    }
}
