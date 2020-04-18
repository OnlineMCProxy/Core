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
package com.github.derrop.proxy.api.chat.component;

/**
 * This component processes a target selector into a pre-formatted set of
 * discovered names.
 * <br>
 * Multiple targets may be obtained, and with commas separating each one and a
 * final "and" for the last target. The resulting format cannot be overwritten.
 * This includes all styling from team prefixes, insertions, click events, and
 * hover events.
 * <br>
 * These values are filled in by the server-side implementation.
 * <br>
 * As of 1.12.2, a bug ( MC-56373 ) prevents full usage within hover events.
 */
public final class SelectorComponent extends BaseComponent {

    /**
     * An entity target selector (@p, @a, @r, @e, or @s) and, optionally,
     * selector arguments (e.g. @e[r=10,type=Creeper]).
     */
    private String selector;

    /**
     * Creates a selector component from the original to clone it.
     *
     * @param original the original for the new selector component
     */
    public SelectorComponent(SelectorComponent original) {
        super(original);
        setSelector(original.getSelector());
    }

    public SelectorComponent(String selector) {
        this.selector = selector;
    }

    @Override
    public SelectorComponent duplicate() {
        return new SelectorComponent(this);
    }

    @Override
    protected void toPlainText(StringBuilder builder) {
        builder.append(this.selector);
        super.toPlainText(builder);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        addFormat(builder);
        builder.append(this.selector);
        super.toLegacyText(builder);
    }

    public String getSelector() {
        return this.selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof SelectorComponent))
            return false;
        final SelectorComponent other = (SelectorComponent) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$selector = this.getSelector();
        final Object other$selector = other.getSelector();
        if (this$selector == null ? other$selector != null : !this$selector.equals(other$selector))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SelectorComponent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $selector = this.getSelector();
        result = result * PRIME + ($selector == null ? 43 : $selector.hashCode());
        return result;
    }

    public String toString() {
        return "SelectorComponent(selector=" + this.getSelector() + ")";
    }
}
