// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.maths.random;

import java.util.Random;
import org.uncommons.maths.binary.BinaryUtils;

/**
 * Very fast pseudo random number generator.  See
 * <a href="http://school.anhb.uwa.edu.au/personalpages/kwessen/shared/Marsaglia03.html">this
 * page</a> for a description.  This RNG has a period of about 2^160, which is not as long
 * as the {@link MersenneTwisterRNG} but it is faster.
 * @author Daniel Dyer
 * @since 1.2
 */
public class XORShiftRNG extends Random implements RepeatableRNG
{
    private static final int SEED_SIZE_BYTES = 20; // Needs 5 32-bit integers.

    private final int[] state;
    private final byte[] seed;


    /**
     * Creates a new RNG and seeds it using the default seeding strategy.
     */
    public XORShiftRNG()
    {
        this(DefaultSeedGenerator.getInstance().generateSeed(SEED_SIZE_BYTES));
    }


    /**
     * Seed the RNG using the provided seed generation strategy.
     * @param seedGenerator The seed generation strategy that will provide
     * the seed value for this RNG.
     * @throws SeedException If there is a problem generating a seed.
     */
    public XORShiftRNG(SeedGenerator seedGenerator) throws SeedException
    {
        this(seedGenerator.generateSeed(SEED_SIZE_BYTES));
    }


    /**
     * Creates an RNG and seeds it with the specified seed data.
     * @param seed The seed data used to initialise the RNG.
     */
    public XORShiftRNG(byte[] seed)
    {
        if (seed == null)
        {
            throw new IllegalArgumentException("XOR shift RNG requires 160 bits of seed data.");
        }
        this.seed = seed.clone();
        this.state = BinaryUtils.convertBytesToInts(seed);
        assert state.length == 5 : "Wrong number of ints: " + state.length;
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getSeed()
    {
        return seed.clone();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected int next(int bits)
    {
        int t = (state[0] ^ (state[0] >> 7));
        state[0] = state[1];
        state[1] = state[2];
        state[2] = state[3];
        state[3] = state[4];
        state[4] = (state[4] ^ (state[4] << 6)) ^ (t ^ (t << 13));
        int value = (state[1] + state[1] + 1) * state[4];
        return value >>> (32 - bits);
    }
}