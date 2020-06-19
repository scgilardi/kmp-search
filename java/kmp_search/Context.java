package kmp_search;

// reference: http://www.inf.fh-flensburg.de/lang/algorithmen/pattern/kmpen.htm

public class Context
{
    public final byte[] pattern;
    public final int[] border;
    public final long position;
    public final int i;
    public final int j;
    public final Long match;

    public Context (byte[] pattern)
    {
        this.pattern = pattern;
        this.border = border(pattern);
        this.position = 0;
        this.i = 0;
        this.j = 0;
        this.match = null;
    }

    public Context (final Context k, long position, int i, int j, Long match)
    {
        this.pattern = k.pattern;
        this.border = k.border;
        this.position = position;
        this.i = i;
        this.j = j;
        this.match = match;
    }

    public Context search (final byte[] data, int start, int end)
    {
        final int length = this.pattern.length;
        long position = this.position;
        int i = this.i;
        int j = this.j;
        Long match = null;

        for (; start + i < end && j < length; ++i, ++j) {
            byte b = data[start + i];
            while (j != -1 && pattern[j] != b)
                j = border[j];
        }

        if (j == length) {
            j = border[j];
            match = position + i;
        }
        else {
            position += i;
            i = 0;
        }

        return new Context(this, position, i, j, match);
    }

    public Context search (final byte[] data)
    {
        return search (data, 0, data.length);
    }

    public Long position ()
    {
        return position;
    }

    public Long start ()
    {
        return match == null ? null : match - this.pattern.length;
    }

    public Long end ()
    {
        return match;
    }

    public Context focus (final Context k)
    {
        return new Context(this, k.position, k.i, 0, null);
    }

    public Context reset ()
    {
        return new Context(this, 0, 0, 0, null);
    }

    static public int[] border (final byte[] pattern)
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
