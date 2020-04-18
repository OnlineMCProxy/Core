/*
 * MIT License
 *
 * Copyright (c) derrop and derklaro
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.derrop.proxy.api.util.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.UUID;

public final class NBTUtil {

    /**
     * Reads and returns a GameProfile that has been saved to the passed in NBTTagCompound
     */
    public static GameProfile readGameProfileFromNBT(NBTTagCompound compound) {
        String s = null;
        String s1 = null;

        if (compound.hasKey("Name", 8)) {
            s = compound.getString("Name");
        }

        if (compound.hasKey("Id", 8)) {
            s1 = compound.getString("Id");
        }

        if ((s == null || s.isEmpty()) && (s1 == null || s1.isEmpty())) {
            return null;
        } else {
            UUID uuid;

            try {
                uuid = UUID.fromString(s1);
            } catch (Throwable var12) {
                uuid = null;
            }

            GameProfile gameprofile = new GameProfile(uuid, s);

            if (compound.hasKey("Properties", 10)) {
                NBTTagCompound nbttagcompound = compound.getCompoundTag("Properties");

                for (String s2 : nbttagcompound.getKeySet()) {
                    NBTTagList nbttaglist = nbttagcompound.getTagList(s2, 10);

                    for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                        NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                        String s3 = nbttagcompound1.getString("Value");

                        if (nbttagcompound1.hasKey("Signature", 8)) {
                            gameprofile.getProperties().put(s2, new Property(s2, s3, nbttagcompound1.getString("Signature")));
                        } else {
                            gameprofile.getProperties().put(s2, new Property(s2, s3));
                        }
                    }
                }
            }

            return gameprofile;
        }
    }

    /**
     * Writes a GameProfile to an NBTTagCompound.
     */
    public static NBTTagCompound writeGameProfile(NBTTagCompound tagCompound, GameProfile profile) {
        if (!(profile.getName() == null || profile.getName().isEmpty())) {
            tagCompound.setString("Name", profile.getName());
        }

        if (profile.getId() != null) {
            tagCompound.setString("Id", profile.getId().toString());
        }

        if (!profile.getProperties().isEmpty()) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            for (String s : profile.getProperties().keySet()) {
                NBTTagList nbttaglist = new NBTTagList();

                for (Property property : profile.getProperties().get(s)) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setString("Value", property.getValue());

                    if (property.hasSignature()) {
                        nbttagcompound1.setString("Signature", property.getSignature());
                    }

                    nbttaglist.appendTag(nbttagcompound1);
                }

                nbttagcompound.setTag(s, nbttaglist);
            }

            tagCompound.setTag("Properties", nbttagcompound);
        }

        return tagCompound;
    }

    public static boolean func_181123_a(NBTBase p_181123_0_, NBTBase p_181123_1_, boolean p_181123_2_) {
        if (p_181123_0_ == p_181123_1_) {
            return true;
        } else if (p_181123_0_ == null) {
            return true;
        } else if (p_181123_1_ == null) {
            return false;
        } else if (!p_181123_0_.getClass().equals(p_181123_1_.getClass())) {
            return false;
        } else if (p_181123_0_ instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) p_181123_0_;
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) p_181123_1_;

            for (String s : nbttagcompound.getKeySet()) {
                NBTBase nbtbase1 = nbttagcompound.getTag(s);

                if (!func_181123_a(nbtbase1, nbttagcompound1.getTag(s), p_181123_2_)) {
                    return false;
                }
            }

            return true;
        } else if (p_181123_0_ instanceof NBTTagList && p_181123_2_) {
            NBTTagList nbttaglist = (NBTTagList) p_181123_0_;
            NBTTagList nbttaglist1 = (NBTTagList) p_181123_1_;

            if (nbttaglist.tagCount() == 0) {
                return nbttaglist1.tagCount() == 0;
            } else {
                for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                    NBTBase nbtbase = nbttaglist.get(i);
                    boolean flag = false;

                    for (int j = 0; j < nbttaglist1.tagCount(); ++j) {
                        if (func_181123_a(nbtbase, nbttaglist1.get(j), p_181123_2_)) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return p_181123_0_.equals(p_181123_1_);
        }
    }
}
