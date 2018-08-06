package io.gulp.validation;

public class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(final L _1, final R _2) {
        this.left = _1;
        this.right = _2;
    }

    public L _1() {
        return this.left;
    }

    public R _2() {
        return this.right;
    }

    @Override
    public int hashCode() {
        return this.left.hashCode() ^ this.right.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        final Pair pairo = (Pair) o;
        return this.left.equals(pairo._1()) && this.right.equals(pairo._2());
    }

}
