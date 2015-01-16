package kmp_search;

// reference: http://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm

public class Context
{
    public final byte[] pattern;
    public final int[] border;
    public final long offset;
    public final int i;
    public final int j;
    public final Object match;

    public Context(byte[] pattern)
    {
        this.pattern = pattern;
        this.border = border(pattern);
        this.offset = 0;
        this.i = 0;
        this.j = 0;
        this.match = null;
    }

    public Context(Context k, long offset, int i, int j, Object match)
    {
        this.pattern = k.pattern;
        this.border = k.border;
        this.offset = offset;
        this.i = i;
        this.j = j;
        this.match = match;
    }

    public Context search(byte[] data, int limit)
    {
        int i = this.i;
        int j = this.j;
        int length = pattern.length;

        for (; i < limit && j < length; ++i, ++j) {
            byte b = data[i];
            while (j != -1 && pattern[j] != b)
                j = border[j];
        }

        return (j == length) ? matched(i) : not_matched(i, j);
    }

    public Context matched(int i)
    {
        int j = border[pattern.length];
        return new Context(this, offset, i, j, offset + i - pattern.length);
    }

    public Context not_matched(int i, int j)
    {
        long offset = this.offset + i;
        return new Context(this, offset, 0, j, null);
    }

    public Object match ()
    {
        return match;
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
