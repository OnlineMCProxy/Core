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
package com.github.derrop.proxy.api.location;

import com.github.derrop.proxy.api.util.MathHelper;
import com.github.derrop.proxy.api.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Location {

    public static final Location ZERO = new Location(0, 0, 0, 0, 0);

    public Location(double x, double y, double z, float yaw, float pitch) {
        this(x, y, z, yaw, pitch, false);
    }

    public Location(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean onGround;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public Vector toVector() {
        return new Vector(this.getX(), this.getY(), this.getZ());
    }

    @NotNull
    public Location substract(@NotNull Location location) {
        this.x -= location.x;
        this.y -= location.y;
        this.z -= location.z;

        return this;
    }

    @NotNull
    public Location add(@NotNull Location location) {
        this.x += location.x;
        this.y += location.y;
        this.z += location.z;

        return this;
    }

    @NotNull
    public Location substract(@NotNull Vector location) {
        this.x -= location.getX();
        this.y -= location.getY();
        this.z -= location.getZ();

        return this;
    }

    @NotNull
    public Location add(@NotNull Vector location) {
        this.x += location.getX();
        this.y += location.getY();
        this.z += location.getZ();

        return this;
    }

    public double distanceSquared(@NotNull Location other) {
        double d1 = x - other.x;
        double d2 = y - other.y;
        double d3 = z - other.z;

        return (d1 * d1) + (d2 * d2) + (d3 * d3);
    }

    /**
     * Gets a unit-vector pointing in the direction that this Location is
     * facing.
     *
     * @return a vector pointing the direction of this location's {@link
     *     #getPitch() pitch} and {@link #getYaw() yaw}
     */
    @NotNull
    public Vector getDirection() {
        Vector vector = new Vector();

        double rotX = this.getYaw();
        double rotY = this.getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double xz = Math.cos(Math.toRadians(rotY));

        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

        return vector;
    }

    /**
     * Sets the {@link #getYaw() yaw} and {@link #getPitch() pitch} to point
     * in the direction of the vector.
     *
     * @param vector the direction vector
     * @return the same location
     */
    @NotNull
    public Location setDirection(@NotNull Vector vector) {
        /*
         * Sin = Opp / Hyp
         * Cos = Adj / Hyp
         * Tan = Opp / Adj
         *
         * x = -Opp
         * z = Adj
         */
        final double _2PI = 2 * Math.PI;
        final double x = vector.getX();
        final double z = vector.getZ();

        if (x == 0 && z == 0) {
            pitch = vector.getY() > 0 ? -90 : 90;
            return this;
        }

        double theta = Math.atan2(-x, z);
        yaw = (float) Math.toDegrees((theta + _2PI) % _2PI);

        double x2 = MathHelper.square(x);
        double z2 = MathHelper.square(z);
        double xz = Math.sqrt(x2 + z2);
        pitch = (float) Math.toDegrees(Math.atan(-vector.getY() / xz));

        return this;
    }

    public double distance(@NotNull Location other) {
        return Math.sqrt(this.distanceSquared(other));
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", onGround=" + onGround +
                '}';
    }

    @NotNull
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (final CloneNotSupportedException ex) {
            return new Location(this.x, this.y, this.z, this.yaw, this.pitch);
        }
    }
}
