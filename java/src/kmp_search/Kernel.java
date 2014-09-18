package kmp_search;

// reference: http://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm

public class Kernel
{
    public final byte[] pattern;
    public final int[] border;
    public final long offset;
    public final int i;
    public final int j;

    public Kernel(byte[] pattern)
    {
        this.pattern = pattern;
        this.border = border(pattern);
        this.offset = 0;
        this.i = 0;
        this.j = 0;
    }

    public Kernel(Kernel k, long offset, int i, int j)
    {
        this.pattern = k.pattern;
        this.border = k.border;
        this.offset = offset;
        this.i = i;
        this.j = j;
    }

    public Object[] search(byte[] data, int limit)
    {
        int i = this.i;
        int j = this.j;
        int length = pattern.length;

        for (; i < limit && j < length; ++i, ++j) {
            byte b = data[i];
            while (j != -1 && pattern[j] != b)
                j = border[j];
        }

        return (j == length) ? matched(i, j) : not_matched(i, j);
    }

    public Object[] matched(int i, int j)
    {
        long match = offset + i - j;
        return new Object[] {match, new Kernel(this, offset, i, border[j])};
    }

    public Object[] not_matched(int i, int j)
    {
        return new Object[] {null, new Kernel(this, offset + i, 0, j)};
    }

    static public int[] border(byte[] pattern)
    {
        int length = pattern.length;
        int[] border = new int[length + 1];
        int i, j;

        for (i = 0, j = -1; i < length; ++i, ++j) {
            border[i] = j;
            byte b = pattern [i];
            while (j != -1 && pattern[j] != b)
                j = border[j];
        }
        border[i] = j;

        return border;
    }
}
