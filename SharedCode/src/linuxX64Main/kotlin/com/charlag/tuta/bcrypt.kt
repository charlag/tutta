// Copyright (c) 2006 Damien Miller <djm@mindrot.org>
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
// ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
// OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

package com.charlag.tuta

/**
 * The basic protocol for the OpenBSD bcrypt password hashing schema which is based on Blowfish
 *
 *
 * Blowfish is notable among block ciphers for its expensive key setup phase. It starts off with subkeys in a standard state, then uses this
 * state to perform a block encryption using part of the key, and uses the result of that encryption (which is more accurately a hashing) to
 * replace some of the subkeys. Then it uses this modified state to encrypt another part of the key, and uses the result to replace more of
 * the subkeys. It proceeds in this fashion, using a progressively modified state to hash the key and replace bits of state, until all subkeys
 * have been set.
 *
 *
 * Provos and Mazières took advantage of this, and took it further. They developed a new key setup algorithm for Blowfish, dubbing the resulting
 * cipher "Eksblowfish" ("expensive key schedule Blowfish"). The key setup begins with a modified form of the standard Blowfish key setup, in
 * which both the salt and password are used to set all subkeys. There are then a number of rounds in which the standard Blowfish keying algorithm
 * is applied, using alternatively the salt and the password as the key, each round starting with the subkey state from the previous round.
 * In theory, this is no stronger than the standard Blowfish key schedule, but the number of rekeying rounds is configurable; this process can
 * therefore be made arbitrarily slow, which helps deter brute-force attacks upon the hash or salt.
 *
 *
 * The bcrypt algorithm is the result of encrypting the text "OrpheanBeholderScryDoubt" 64 times using Blowfish. In bcrypt the usual Blowfish
 * key setup function is replaced with an expensive key setup (EksBlowfishSetup) function.
 *
 * This is a converted and simplified version of Bcrypt from https://github.com/patrickfav/bcrypt
 */
object BCryptOpenBSDProtocol {
    /**
     * Perform the central password hashing step in the
     * bcrypt scheme
     *
     * @param rounds   the actual rounds, not the binary logarithm
     * @param salt     the binary salt to hash with the password
     * @param password the password to hash
     * of rounds of hashing to apply
     * @param cdata    the plaintext to encrypt
     * @return an array containing the binary hashed password
     */
    fun cryptRaw(
        rounds: Long,
        salt: ByteArray,
        password: ByteArray,
        cdata: IntArray = bf_crypt_ciphertext.copyOf()
    ): ByteArray {
        if (rounds < 0) {
            throw IllegalArgumentException("rounds must not be negative")
        }
        val clen = cdata.size
        if (salt.size != SALT_LENGTH) {
            throw IllegalArgumentException("bad salt length")
        }
        val P: IntArray = P_orig.copyOf()
        val S: IntArray = S_orig.copyOf()
        enhancedKeySchedule(P, S, salt, password)
        for (roundCount in 0 until rounds) {
            key(P, S, password)
            key(P, S, salt)
        }
        for (i in 0..63) {
            for (j in 0 until (clen shr 1)) {
                encipher(P, S, cdata, j shl 1)
            }
        }
        val ret = ByteArray(clen * 4)
        var i = 0
        var j = 0
        while (i < clen) {
            ret[j++] = (cdata[i] shr 24 and 0xff).toByte()
            ret[j++] = (cdata[i] shr 16 and 0xff).toByte()
            ret[j++] = (cdata[i] shr 8 and 0xff).toByte()
            ret[j++] = (cdata[i] and 0xff).toByte()
            i++
        }
        return ret
    }

    /**
     * Perform the "enhanced key schedule" step described by
     * Provos and Mazieres in "A Future-Adaptable Password Scheme"
     * http://www.openbsd.org/papers/bcrypt-paper.ps
     *
     * @param data salt information
     * @param key  password information
     */
    private fun enhancedKeySchedule(P: IntArray, S: IntArray, data: ByteArray, key: ByteArray) {
        var i: Int
        val koffp = intArrayOf(0)
        val doffp = intArrayOf(0)
        val lr = intArrayOf(0, 0)
        val plen = P.size
        val slen = S.size
        i = 0
        while (i < plen) {
            P[i] = P[i] xor streamToWord(key, koffp)
            i++
        }
        i = 0
        while (i < plen) {
            lr[0] = lr[0] xor streamToWord(data, doffp)
            lr[1] = lr[1] xor streamToWord(data, doffp)
            encipher(P, S, lr, 0)
            P[i] = lr[0]
            P[i + 1] = lr[1]
            i += 2
        }
        i = 0
        while (i < slen) {
            lr[0] = lr[0] xor streamToWord(data, doffp)
            lr[1] = lr[1] xor streamToWord(data, doffp)
            encipher(P, S, lr, 0)
            S[i] = lr[0]
            S[i + 1] = lr[1]
            i += 2
        }
    }

    /**
     * Key the Blowfish cipher
     *
     * @param key an array containing the key
     */
    private fun key(P: IntArray, S: IntArray, key: ByteArray) {
        var i: Int
        val koffp = intArrayOf(0)
        val lr = intArrayOf(0, 0)
        val plen = P.size
        val slen = S.size
        i = 0
        while (i < plen) {
            P[i] = P[i] xor streamToWord(
                key,
                koffp
            )
            i++
        }
        i = 0
        while (i < plen) {
            encipher(P, S, lr, 0)
            P[i] = lr[0]
            P[i + 1] = lr[1]
            i += 2
        }
        i = 0
        while (i < slen) {
            encipher(P, S, lr, 0)
            S[i] = lr[0]
            S[i + 1] = lr[1]
            i += 2
        }
    }

    /**
     * Blowfish encipher a single 64-bit block encoded as
     * two 32-bit halves
     *
     * @param lr  an array containing the two 32-bit half blocks
     * @param off the position in the array of the blocks
     */
    private fun encipher(P: IntArray, S: IntArray, lr: IntArray, off: Int) {
        var i: Int
        var n: Int
        var l = lr[off]
        var r = lr[off + 1]
        l = l xor P[0]
        i = 0
        while (i <= BLOWFISH_NUM_ROUNDS - 2) {

            // Feistel substitution on left word
            n = S[l shr 24 and 0xff]
            n += S[0x100 or (l shr 16 and 0xff)]
            n = n xor S[0x200 or (l shr 8 and 0xff)]
            n += S[0x300 or (l and 0xff)]
            r = r xor (n xor P[++i])

            // Feistel substitution on right word
            n = S[r shr 24 and 0xff]
            n += S[0x100 or (r shr 16 and 0xff)]
            n = n xor S[0x200 or (r shr 8 and 0xff)]
            n += S[0x300 or (r and 0xff)]
            l = l xor (n xor P[++i])
        }
        lr[off] =
            r xor P[BLOWFISH_NUM_ROUNDS + 1]
        lr[off + 1] = l
    }

    /**
     * Fixed lenght of the bcrypt salt
     */
    const val SALT_LENGTH = 16

    /**
     * The max length of the password in bytes excluding lats null-terminator byte
     */
    const val MAX_PW_LENGTH_BYTE = 71

    /**
     * Minimum allowed cost factor
     */
    const val MIN_COST = 4

    /**
     * Maximum allowed cost factor
     */
    const val MAX_COST = 31

    /**
     * Ascii hex pointer for '2'
     */
    const val MAJOR_VERSION: Byte = 0x32

    /**
     * The raw hash out length in byte
     */
    const val HASH_OUT_LENGTH = 23

    /**
     * Ascii hex pointer for '$'
     */
    const val SEPARATOR: Byte = 0x24


    // Blowfish parameters
    private const val BLOWFISH_NUM_ROUNDS = 16

    // Initial contents of key schedule
    private val P_orig = intArrayOf(
        0x243f6a88, -0x7a5cf72d, 0x13198a2e, 0x03707344,
        -0x5bf6c7de, 0x299f31d0, 0x082efa98, -0x13b19377,
        0x452821e6, 0x38d01377, -0x41ab9931, 0x34e90c6c,
        -0x3f53d649, -0x3683af23, 0x3f84d5b5, -0x4ab8f6e9,
        -0x6de92a27, -0x768604e5
    )
    private val S_orig = intArrayOf(
        -0x2ecef45a, -0x67204a54, 0x2ffd72db, -0x2fe52049,
        -0x471e5013, 0x6a267e96, -0x45836fbb, -0xed38067,
        0x24a19947, -0x4c6e9309, 0x0801f2e2, -0x7a7103ea,
        0x636920d8, 0x71574e69, -0x5ba7015d, -0xb6cc282,
        0x0d95748f, 0x728eb658, 0x718bcd58, -0x7deab512,
        0x7b54a41d, -0x3da5a64b, -0x63cf2ac7, 0x2af26013,
        -0x3a2e4fdd, 0x286085f0, -0x35be86e8, -0x4724c711,
        -0x71862350, 0x603a180e, 0x6c9e0e8b, -0x4fe175c2,
        -0x28ea883f, -0x42ceb4d9, 0x78af2fda, 0x55605c60,
        -0x19aada0d, -0x55aa546c, 0x57489862, 0x63e81440,
        0x55ca396a, 0x2aab10b6, -0x4b33a3cc, 0x1141e8ce,
        -0x5eab7951, 0x7c72e993, -0x4c11ebef, 0x636fbc2a,
        0x2ba9c55d, 0x741831f6, -0x31a3c1ea, -0x64786ce2,
        -0x502945cd, 0x6c24cf5c, 0x7a325381, 0x28958677,
        0x3b8f4898, 0x6b4bb9af, -0x3b4017e5, 0x66282193,
        0x61d809cc, -0x4de566f, 0x487cac60, 0x5dec8032,
        -0x107ba2a3, -0x167a8a4f, -0x23d9dcfe, -0x149ae478,
        0x23893e81, -0x2c69533b, 0x0f6d6ff3, -0x7c0bbdc7,
        0x2e0b4482, -0x5b7bdffc, 0x69c8f04a, -0x61e064a2,
        0x21c66842, -0x9169366, 0x670c9c61, -0x542c7710,
        0x6a51a0d2, -0x27abd098, -0x69f058d8, -0x54aecc5d,
        0x6eef0b6c, 0x137a3be4, -0x45c40fb0, 0x7efb2a98,
        -0x5e0e9ae3, 0x39af0176, 0x66ca593e, -0x7dbcf178,
        -0x731179e7, 0x456f9fb4, 0x7d84a5c3, 0x3b8b5ebe,
        -0x1f908a28, -0x7a3edf8d, 0x401a449f, 0x56c16aa6,
        0x4ed3aa62, 0x363f7706, 0x1bfedf72, 0x429b023d,
        0x37d0d724, -0x2ff5edb8, -0x24f0152d, 0x49f1c09b,
        0x075372c9, -0x7f66e485, 0x25d479d8, -0x9172109,
        -0x1c01afe6, -0x4986b3c5, -0x68931f43, 0x04c006ba,
        -0x3e56b04a, 0x409f60c4, 0x5e5c9ec2, 0x196a2463,
        0x68fb6faf, 0x3e6c53b5, 0x1339b2eb, 0x3b52ec6f,
        0x6dfc511f, -0x64cf6ad4, -0x337ebabc, -0x50a142f7,
        -0x411c2ffc, -0x21ccb503, 0x660f2807, 0x192e4bb3,
        -0x3f3457a9, 0x45c8740f, -0x2df4a0c7, -0x462c0425,
        0x5579c0bd, 0x1a60320a, -0x295eff3a, 0x402c7279,
        0x679f25fe, -0x4e05c34, -0x715a1608, -0x24cddd08,
        0x3c7516df, -0x29e94eb, 0x2f501ec8, -0x52faad55,
        0x323db5fa, -0x2dc78a0, 0x53317b48, 0x3e00df82,
        -0x61a3a845, -0x35907360, 0x1a87562e, -0x20e89625,
        -0x2abd570a, 0x287effc3, -0x5398cd3a, -0x73b0aa8d,
        0x695b27b0, -0x4435a738, -0x1e005ca3, -0x470fee60,
        0x10fa3d98, -0x2de7c48, 0x4afcb56c, 0x2dd1d35b,
        -0x65ac1b87, -0x4907ba9b, -0x2d71b644, 0x4bfb9790,
        -0x1e220d26, -0x5b3481cd, 0x62fb1341, -0x311b3918,
        -0x10df3526, 0x36774c01, -0x2f816102, 0x2bf11fb4,
        -0x6a2425b3, -0x516f6e68, -0x1552718f, 0x6b93d5a0,
        -0x2f712e30, -0x5038da20, -0x71c3a4d1, -0x718a6b49,
        -0x70091d05, -0xdedd49c, -0x777747ee, -0x6ff20fe4,
        0x4fad5ea0, 0x688fc31c, -0x2e300e6f, -0x4c573e53,
        0x2f2f2218, -0x41f1e889, -0x158ad202, -0x74fde05f,
        -0x1a5f33f1, -0x4a908b18, 0x18acf3d6, -0x31761d67,
        -0x4b57b020, -0x2ec1f49, 0x7cc43b81, -0x2d525727,
        0x165fa266, -0x7f6a88fb, -0x6c338cec, 0x211a1477,
        -0x1952df9b, 0x77b5fa86, -0x38abbd0b, -0x462ca31,
        -0x143250f4, 0x7b3e89a0, -0x29bee42d, -0x51e181b7,
        0x00250e2d, 0x2071b35e, 0x226800bb, 0x57b8e0af,
        0x2464369b, -0xff646e2, 0x5563911d, 0x59dfa6aa,
        0x78c14389, -0x26a5ac81, 0x207d5ba2, 0x02e5b9c5,
        -0x7cd9fc8a, 0x6295cfa9, 0x11c81968, 0x4e734a41,
        -0x4cb8d236, 0x7b14a94a, 0x1b510052, -0x65acd6eb,
        -0x29f0a8c1, -0x4364391c, 0x2b60a476, -0x7e198c00,
        0x08ba6fb5, 0x571be91f, -0xd691395, 0x2a0dd915,
        -0x499c9adf, -0x1846064a, -0xcbfad2, -0x3a7aa99c,
        0x53b02d5d, -0x5660705f, 0x08ba4799, 0x6e85076a,
        0x4b7a70e9, -0x4a4cd6bc, -0x248af6d2, -0x3be6d9dd,
        -0x52915950, 0x49a7df7d, -0x63119f48, -0x70124d9a,
        -0x1355738f, 0x699a17ff, 0x5664526c, -0x3d4e611f,
        0x193602a5, 0x75094c29, -0x5fa6ecc0, -0x1be7c5c2,
        0x3f54989a, 0x5b429d65, 0x6b8fe4d6, -0x6608c02a,
        -0x5e2d63f9, -0x1017cf0b, 0x4d2d38e6, -0xfdaa23f,
        0x4cdd2086, -0x7b8f14da, 0x6382e9c6, 0x021ecc5e,
        0x09686b3f, 0x3ebaefc9, 0x3c971814, 0x6b6a70a1,
        0x687f3584, 0x52a0e286, -0x4863acfb, -0x55aff8c9,
        0x3e07841c, 0x7fdeae5c, -0x7182bb14, 0x5716f2b8,
        -0x4fc525c9, -0xfaff3f3, -0xfe3e0fc, 0x0200b3ff,
        -0x51f30ae6, 0x3cb574b2, 0x25837a58, -0x23f6de43,
        -0x2e6eec07, 0x7ca92ff6, -0x6bcdb88d, 0x22f54701,
        0x3ae5e581, 0x37c2dadc, -0x374a89cc, -0x650c2259,
        -0x56bb9eba, 0x0fd0030e, -0x133738c2, -0x5b8ae1bf,
        -0x1dc73267, 0x3bea0e2f, 0x3280bba1, 0x183eb331,
        0x4e548b38, 0x4f6db908, 0x6f420d03, -0x9f5fb41,
        0x2cb81290, 0x24977c79, 0x5679b072, -0x43507651,
        -0x216588e1, -0x266cf7f0, -0x4c7451ee, -0x2330c0d2,
        0x5512721f, 0x2e6b7124, 0x501adde6, -0x607b3279,
        0x7a584718, 0x7408da17, -0x43606544, -0x16b48274,
        -0x138513c6, -0x247ae206, 0x63094366, -0x3b9b3c2e,
        -0x10e3e7b9, 0x3215d908, -0x22bcc4c9, 0x24c2ba16,
        0x12a14d43, 0x2a65c451, 0x50940002, 0x133ae4dd,
        0x71dff89e, 0x10314e55, -0x7e53882a, 0x5f11199b,
        0x043556f1, -0x285c3895, 0x3c11183b, 0x5924a509,
        -0xd701913, -0x680e0406, -0x614540d4, 0x1e153c6e,
        -0x791cba90, -0x1516904f, -0x79f1a1f6, 0x5a3e2ab3,
        0x771fe71c, 0x4e3d06fa, 0x2965dcb9, -0x6618e2f1,
        -0x7fc1762a, 0x5266c825, 0x2e4cc978, -0x63ef4c96,
        -0x39eaf146, -0x6b1d1588, -0x5a03c3ad, 0x1e0a2df4,
        -0xd08b159, 0x361d2b3d, 0x1939260f, 0x19c27960,
        0x5223a708, -0x8eced4a, -0x14520192, -0x153ce09a,
        -0x1c43ba6b, -0x5984377d, -0x4e80c82f, 0x018cff28,
        -0x3ccd2211, -0x4193a55b, 0x65582185, 0x68ab9802,
        -0x11315af1, -0x24d06ac5, 0x2aef7dad, 0x5b6e2f84,
        0x1521b628, 0x29076170, -0x1322b88b, 0x619f1510,
        0x13cca830, -0x149e426a, 0x0334fe1e, -0x55fc9c31,
        -0x4a8ca370, 0x4c70a239, -0x2a6161f5, -0x345521ec,
        -0x11337944, 0x60622ca7, -0x6354a355, -0x4d0c7b92,
        0x648b1eaf, 0x19bdf0ca, -0x5fdc9647, 0x655abb50,
        0x40685a32, 0x3c2ab4b3, 0x319ee9d5, -0x3fde4709,
        -0x64abf4e7, -0x78a05f67, -0x6a086682, 0x623d7da8,
        -0x7c87766, -0x681cd289, 0x11ed935f, 0x16681281,
        0x0e358829, -0x3819e02a, -0x6921205f, 0x7858ba99,
        0x57f584a5, 0x1b227263, -0x647c3c01, 0x1ac24696,
        -0x324cf515, 0x532e3054, -0x7026b71c, 0x6dbc3128,
        0x58ebf2ef, 0x34c6ffea, -0x1d7129f, -0x1183c38d,
        0x5d4a14d9, -0x179b481d, 0x42105d14, 0x203e13e0,
        0x45eee2b6, -0x5c555416, -0x2493b0eb, -0x534b030,
        -0x38bd0bbe, -0x1095444b, 0x654f3b1d, 0x41cd2105,
        -0x27e18662, -0x797ab239, -0x1bb4b896, 0x3d816250,
        -0x309d5e0e, 0x5b8d2646, -0x3777c60, -0x3e38495d,
        0x7f1524c3, 0x69cb7492, 0x47848a0b, 0x5692b285,
        0x095bbf00, -0x52e6b763, 0x1462b174, 0x23820e00,
        0x58428d2a, 0x0c55f5ea, 0x1dadf43e, 0x233f7061,
        0x3372f092, -0x726c81bf, -0x29a0130f, 0x6c223bdb,
        0x7cde3759, -0x34118ba0, 0x4085f2a7, -0x3188cd92,
        -0x59f87f7c, 0x19f8509e, -0x171027ab, 0x61d99735,
        -0x56965856, -0x3af3f93e, 0x5a04abfc, -0x7ff43524,
        -0x61bb85d2, -0x3cbacb7c, -0x22a98fb, 0x0e1e9ec9,
        -0x248c242d, 0x105588cd, 0x675fda79, -0x1c98bcc0,
        -0x3a3bcb9b, 0x713e38d8, 0x3d28f89e, -0xe9200e0,
        0x153e21e7, -0x704fc2b6, -0x191c60d5, -0x247c5209,
        -0x16c2a598, -0x6b7ebf09, -0x9b3d9e4, -0x6b96d6cc,
        0x411520f7, 0x7602d4f7, -0x430b94d2, -0x2b5dff98,
        -0x2bf7db8f, 0x3320f46a, 0x43b7d4b7, 0x500061af,
        0x1e39f62e, -0x68dbbaba, 0x14214f74, -0x407477c0,
        0x4d95fc1d, -0x694a6e51, 0x70f4ddd3, 0x66a02f45,
        -0x4043f614, 0x03bd9785, 0x7fac6dd0, 0x31cb8504,
        -0x6914d84d, 0x55fd3941, -0x25dab81a, -0x5435f566,
        0x28507825, 0x530429f4, 0x0a2c86da, -0x16499205,
        0x68dc1462, -0x28b79700, 0x680ec0a4, 0x27a18dee,
        0x4f3ffea2, -0x17785274, -0x4a731ffa, 0x7af4d6b6,
        -0x5531e184, -0x2cc8a014, -0x31875c67, 0x406b2a42,
        0x20fe9e35, -0x260c7a47, -0x11c62855, 0x3b124e8b,
        0x1dc9faf7, 0x4b6d1856, 0x26a36631, -0x151c684e,
        0x3a6efa74, -0x22a4bcce, 0x6841e7f7, -0x3587df05,
        -0x4f50ab2, -0x27014c69, 0x454056ac, -0x45b76ad9,
        0x55533a3a, 0x20838d87, -0x1945649, -0x2f696ab5,
        0x55a867bc, -0x5eea65a8, -0x3356d69d, -0x661e24cd,
        -0x59d5b5aa, 0x3f3125f9, 0x5ef47e1c, -0x6fd6ce84,
        -0x20717fe, 0x04272f70, -0x7f44eaa4, 0x05282ce3,
        -0x6a3eeab8, -0x1b3992de, 0x48c1133f, -0x38f07924,
        0x07f9c9ee, 0x41041f0f, 0x404779a4, 0x5d886e17,
        0x325f51eb, -0x2a643f2f, -0xd433e71, 0x41113564,
        0x257b7834, 0x602a9c60, -0x2007175d, 0x1f636c1b,
        0x0e12b4c2, 0x02e1329e, -0x5099b02f, -0x352e7eeb,
        0x6b2395e0, 0x333e92e1, 0x3b240b62, -0x114146de,
        -0x7a4d5df2, -0x1945f267, -0x218df374, 0x2da2f728,
        -0x2fed87bb, -0x6a486b03, 0x647d0862, -0x18330a10,
        0x5449a36f, -0x7882b706, -0x3c6202d9, -0xcc172e2,
        0x0a476341, -0x66d1008c, 0x3a6f6eab, -0xb0702c9,
        -0x57ed23a0, -0x5e142208, -0x66e41eb4, -0x249194f3,
        -0x3984aaf0, 0x6d672c37, 0x2765d43b, -0x232f17fc,
        -0xed6f239, -0x33ff005d, -0x4ac6f06e, 0x690fed0b,
        0x667b9ffb, -0x31248264, -0x5f6e30f5, -0x26eaa15d,
        -0x44ecd078, 0x515bad24, 0x7b9479bf, 0x763bd6eb,
        0x37392eb3, -0x33eea687, -0x7fd91d69, -0xbd1ced3,
        0x6842ada7, -0x3995d4c5, 0x12754ccc, 0x782ef11c,
        0x6a124237, -0x486dae19, 0x06a1bbe6, 0x4bfb6350,
        0x1a6b1018, 0x11caedfa, 0x3d25bdd8, -0x1d1e3c37,
        0x44421659, 0x0a121386, -0x26f31392, -0x2a5415d6,
        0x64af674e, -0x257957a1, -0x41401678, 0x64e4c3fe,
        -0x62437fa9, -0xf083f7a, 0x60787bf8, 0x6003604d,
        -0x2e027cba, -0x9c7e050, 0x7745ae04, -0x28c90334,
        -0x7cbd94cd, -0xfe1548f, -0x4f7fbe79, 0x3c005e5f,
        0x77a057be, -0x421751dc, 0x55464299, -0x40a7d19f,
        0x4e58f48f, -0xd22025e, -0xb8b10c8, -0x7876423e,
        0x5366f9c3, -0x374c718c, -0x4b8a0dab, 0x46fcd9b9,
        0x7aeb2661, -0x74e2207c, -0x7b95f187, -0x6ea06a1e,
        0x466e598e, 0x20b45770, -0x732aaa6f, -0x36fd21b4,
        -0x46f4531f, -0x447dfa30, 0x11a86248, 0x7574a99e,
        -0x4880e64a, -0x1f5623f7, 0x662d09a1, -0x3bcdb9cd,
        -0x17a5e0fe, 0x09f0be8c, 0x4a99a025, 0x1d6efe10,
        0x1ab93d1d, 0x0ba5a4df, -0x5e790df1, 0x2868f169,
        -0x2348257d, 0x573906fe, -0x5e1d3165, 0x4fcd7f52,
        0x50115e01, -0x58f97c06, -0x5ffd4a3c, 0x0de6d027,
        -0x650773d9, 0x773f8641, -0x3c9fb3fa, 0x61a806b5,
        -0xfe885d8, -0x3f0a7920, 0x006058aa, 0x30dc7d62,
        0x11e69ed7, 0x2338ea63, 0x53c2dd94, -0x3d3de9cc,
        -0x443411aa, -0x6f434922, -0x1403825f, -0x31a6e28a,
        0x6f05e409, 0x4b7c0188, 0x39720a3d, 0x7c927c24,
        -0x791c8da1, 0x724d9db9, 0x1ac15bb4, -0x2c614704,
        -0x12abaa88, 0x08fca5b5, -0x27c2832d, 0x4dad0fc4,
        0x1e50ef5e, -0x4e9e1908, -0x5d7aeb27, 0x6c51133c,
        0x6fd5c7e7, 0x56e14ec4, 0x362abfce, -0x223937c9,
        -0x2865cdcc, -0x6d9c7dee, 0x670efa8e, 0x406000e0,
        0x3a39ce37, -0x2c050a31, -0x543d88c9, 0x5ac52d1b,
        0x5cb0679e, 0x4fa33742, -0x2c7dd8c0, -0x66436442,
        -0x2aee7163, -0x40f08ceb, -0x29d2e382, -0x38ff3b85,
        -0x4873e495, 0x21a19045, -0x4d914e42, 0x6a366eb4,
        0x5748ab2f, -0x436b9187, -0x395c892e, 0x6549c2c8,
        0x530ff8ee, 0x468dde7d, -0x2a8cf5e3, 0x4cd04dc6,
        0x2939bbdb, -0x5645b9b0, -0x536ad918, -0x41a11cfc,
        -0x5e052a10, 0x6a2d519a, 0x63ef8ce2, -0x657911de,
        -0x3f763d48, 0x43242ef6, -0x5ae1fc56, -0x630d2f5c,
        -0x7c3f9e46, -0x641695b3, -0x701aeab0, -0x459ba42a,
        0x2826a2f9, -0x58c5c51f, 0x4ba99586, -0x10aa9d17,
        -0x38d0102d, -0x8ad0826, 0x3f046f69, 0x77fa0a59,
        -0x7f1b56eb, -0x784f79ff, -0x64f61953, 0x3b3ee593,
        -0x166f02a6, -0x61cb2869, 0x2cf0b7d9, 0x022b8b51,
        -0x692a53c6, 0x017da67d, -0x2e30c12a, 0x7c7d2d28,
        0x1f9f25cf, -0x520d4765, 0x5ad6b472, 0x5a88f54c,
        -0x1fd6538f, -0x1fe65a1a, 0x47b0acfd, -0x126c0565,
        -0x172c3b73, 0x283b57cc, -0x72a99d7, 0x79132e28,
        0x785f0191, -0x128a9fab, -0x869f1bc, -0x1c2ca174,
        0x15056dd4, -0x770b9246, 0x03a16125, 0x0564f0bd,
        -0x3c1461eb, 0x3c9057a2, -0x68d8e514, -0x56c5f8d6,
        0x1b3f6d9b, 0x1e6321f5, -0xa639905, 0x26dcf319,
        0x7533d928, -0x4eaa020b, 0x03563482, -0x7545c345,
        0x28517711, -0x3df52608, -0x5433ae99, -0x33526da1,
        0x4de81751, 0x3830dc8e, 0x379d5862, -0x6cdf066f,
        -0x15856f3e, -0x4c18432, 0x5121ce64, 0x774fbe32,
        -0x57491c82, -0x3cd6c2ba, 0x48de5369, 0x6413e680,
        -0x5d51f7f0, -0x22924ddc, 0x69852dfd, 0x09072166,
        -0x4c65b9f6, 0x6445c0dd, 0x586cdecf, 0x1c20c8ae,
        0x5bbef7dd, 0x1b588d40, -0x332dfe81, 0x6bb4e3bb,
        -0x225d9582, 0x3a59ff45, 0x3e350a44, -0x434b322b,
        0x72eacea8, -0x59b7b45, -0x7299ed52, -0x40c390b9,
        -0x2d641b9d, 0x542f5d9e, -0x513d88e5, -0x9b19c90,
        0x740e0d8d, -0x18a4eca9, -0x78de98f, -0x50ac82a3,
        0x4040cb08, 0x4eb4e2cc, 0x34d2466a, 0x0115af84,
        -0x1e4ffbd8, -0x6a67c5e3, 0x06b89fb4, -0x31915fb8,
        0x6f3f3b82, 0x3520ab82, 0x011a1d4b, 0x277227f8,
        0x611560b1, -0x186cc024, -0x44c586d5, 0x344525bd,
        -0x5f77c61f, 0x51ce794b, 0x2f32c9b7, -0x5fe04537,
        -0x1fe33782, -0x43382e0a, -0x30feee3d, -0x5e175539,
        0x1a908749, -0x2bb04266, -0x2f252135, -0x2af525c8,
        0x0339c32a, -0x396ec999, -0x7206ce84, -0x1f4ed4b1,
        -0x861a649, 0x43f5bb3a, -0xd2ae601, 0x27d9459c,
        -0x4068ddd4, 0x15e6fc2a, 0x0f91fc71, -0x646beadb,
        -0x51a6c9f, -0x31496315, -0x3d579ba7, 0x12baa8d1,
        -0x493ef8a2, -0x1cfa95f4, 0x10d25065, -0x34fc5bbe,
        -0x1f1391f2, 0x1698db3b, 0x4c98a0be, 0x3278e964,
        -0x60e06ace, -0x1f2c6d21, -0x2c5fcbd5, -0x768e0de2,
        0x1b0a7441, 0x4ba3348c, -0x3a418ee0, -0x3c89cd28,
        -0x20ca6073, -0x6466d0d2, -0x19f490b9, 0x0fe3f11d,
        -0x1ab325ac, 0x1edad891, -0x319d8631, -0x32c18191,
        0x1618b166, -0x2d3e2fb, -0x7b702d3b, -0x904dd67,
        -0xadc0ca9, -0x59cd89dd, -0x6c57cacf, 0x56cccd02,
        -0x530f7e9e, 0x5a75ebb5, 0x6e163697, -0x772d8c34,
        -0x21699d6e, -0x7e46b630, 0x4c50901b, 0x71c65614,
        -0x19393843, 0x327a140a, 0x45e1d006, -0x3c0d8466,
        -0x3655ac03, 0x62a80f00, -0x44da401e, 0x35bdd2f6,
        0x71126905, -0x4dfbfdde, -0x49343084, -0x328963d5,
        0x53113ec0, 0x1640e3d3, 0x38abbd60, 0x2547adf0,
        -0x45c7df64, -0x8b9318a, 0x77afa1c5, 0x20756060,
        -0x7a3401b2, -0x75177228, 0x7aaaf9b0, 0x4cf9aa7e,
        0x1948c25c, 0x02fb8a8c, 0x01c36ae4, -0x29141e07,
        -0x6f2b0797, -0x59a32160, 0x3f09252d, -0x3df71961,
        -0x48b19ece, -0x31881da5, 0x578fdfe3, 0x3ac372e6
    )

    // bcrypt IV: "OrpheanBeholderScryDoubt". The C implementation calls
    // this "ciphertext", but it is really plaintext or an IV. We keep
    // the name to make code comparison easier.
    private val bf_crypt_ciphertext = intArrayOf(
        0x4f727068, 0x65616e42, 0x65686f6c,
        0x64657253, 0x63727944, 0x6f756274
    )

    /**
     * Cyclically extract a word of key material
     *
     * @param data the string to extract the data from
     * @param offp a "pointer" (as a one-entry array) to the
     * current offset into data
     * @return the next word of material from data
     */
    private fun streamToWord(data: ByteArray, offp: IntArray): Int {
        var i: Int
        var word = 0
        var off = offp[0]
        i = 0
        while (i < 4) {
            word = word shl 8 or (data[off].toInt() and 0xff)
            off = (off + 1) % data.size
            i++
        }
        offp[0] = off
        return word
    }
}