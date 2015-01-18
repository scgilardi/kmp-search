package kmp_search;

// reference: http://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm

public class Context
{
    public final byte[] pattern;
    public final int[] border;
    public final long offset;
    public final int i;
    public final int j;
    public final Long match;

    public Context(byte[] pattern)
    {
        this.pattern = pattern;
        this.border = border(pattern);
        this.offset = 0;
        this.i = 0;
        this.j = 0;
        this.match = null;
    }

    public Context(final Context k, long offset, int i, int j, Long match)
    {
        this.pattern = k.pattern;
        this.border = k.border;
        this.offset = offset;
        this.i = i;
        this.j = j;
        this.match = match;
    }

    public Context search(final byte[] data, int limit)
    {
        final int length = this.pattern.length;
        long offset = this.offset;
        int i = this.i;
        int j = this.j;
        Long match = null;

        for (; i < limit && j < length; ++i, ++j) {
            byte b = data[i];
            while (j != -1 && pattern[j] != b)
                j = border[j];
        }

        if (j == length) {
            j = border[j];
            match = offset + i - length;
        }
        else {
            offset += i;
            i = 0;
        }

        return new Context(this, offset, i, j, match);
    }

    public Long match ()
    {
        return match;
    }

    static public int[] border(final byte[] pattern)
    {
        final int length = pattern.length;
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
