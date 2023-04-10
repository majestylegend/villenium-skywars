package net.villenium.skywars.utils.structures;

import java.beans.ConstructorProperties;

public class Pair<A, B> {
    private A first;
    private B second;

    @ConstructorProperties({"first", "second"})
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return this.first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return this.second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair<?, ?> other = (Pair) o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$first = this.getFirst();
                Object other$first = other.getFirst();
                if (this$first == null) {
                    if (other$first != null) {
                        return false;
                    }
                } else if (!this$first.equals(other$first)) {
                    return false;
                }

                Object this$second = this.getSecond();
                Object other$second = other.getSecond();
                if (this$second == null) {
                    if (other$second != null) {
                        return false;
                    }
                } else if (!this$second.equals(other$second)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Pair;
    }

    public int hashCode() {
        int result = 1;
        Object $first = this.getFirst();
        result = result * 59 + ($first == null ? 43 : $first.hashCode());
        Object $second = this.getSecond();
        result = result * 59 + ($second == null ? 43 : $second.hashCode());
        return result;
    }

    public String toString() {
        return "Pair(first=" + this.getFirst() + ", second=" + this.getSecond() + ")";
    }
}
    