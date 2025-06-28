package cp_algorithm.cdq;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

public class P5621_Delisha {
    public static class Node {
        int a, b, c, d;
        int i;
        long v;
        boolean left;

        public Node(int a_, int b_, int c_, int d_, int v_) {
            a = a_;
            b = b_;
            c = c_;
            d = d_;
            v = v_;
        }
    }

    public static class Cmp1 implements Comparator<Node> {
        @Override
        public int compare(Node x, Node y) {
            if (x.a != y.a) return x.a - y.a;
            if (x.b != y.b) return x.b - y.b;
            if (x.c != y.c) return x.c - y.c;
            if (x.d != y.d) return x.d - y.d;
            return Long.compare(y.v, x.v);
        }
    }

    public static class Cmp2 implements Comparator<Node> {
        @Override
        public int compare(Node x, Node y) {
           if (x.b != y.b) return x.b - y.b;
           return x.i - y.i; // 稳定性排序
        }
    }

    public static class Cmp3 implements Comparator<Node> {
        @Override
        public int compare(Node x, Node y) {
            if (x.c != y.c) return x.c - y.c;
            return x.i - y.i; // 稳定性排序
        }
    }

    public static Cmp1 cmp1 = new Cmp1();
    public static Cmp2 cmp2 = new Cmp2();
    public static Cmp3 cmp3 = new Cmp3();

    public static int MAXN = 50005;
    public static long INF = (long) (1e18 + 1);

    public static int n, s;
    public static Node[] arr = new Node[MAXN];
    public static int[] sortd = new int[MAXN];
    public static Node[] tmp1 = new Node[MAXN];
    public static Node[] tmp2 = new Node[MAXN];

    public static long[] tree = new long[MAXN];
    public static long[] dp = new long[MAXN];

    public static int lowbit(int i) {
        return i & -i;
    }

    public static void update(int i, long num) {
        while (i <= s) {
            tree[i] = Math.max(tree[i], num);
            i += lowbit(i);
        }
    }

    public static long query(int i) {
        long res = -INF;
        while (i > 0) {
            res = Math.max(res, tree[i]);
            i -= lowbit(i);
        }
        return res;
    }

    public static void clear(int i) {
        while (i <= s) {
            tree[i] = -INF;
            i += lowbit(i);
        }
    }

    public static int lower(int val) {
        int l = 0, r = s + 1;
        while (l + 1 < r) {
            int mid = (l + r) / 2;
            if (sortd[mid] >= val) r = mid;
            else l = mid;
        }
        return r;
    }

    public static void cdq1(int l, int r) {
        if (l == r) return;
        int mid = (l + r) / 2;
        cdq1(l, mid);
        for (int i = l; i <= r; i++) {
            tmp1[i] = arr[i];
            tmp1[i].left = i <= mid;
        }
        Arrays.sort(tmp1, l, r + 1, cmp2);
        cdq2(l, r);
        cdq1(mid + 1, r);
    }

    public static void cdq2(int l, int r) {
        if (l == r) return;
        int mid = (l + r) / 2;
        cdq2(l, mid);
        if (r + 1 - l >= 0) System.arraycopy(tmp1, l, tmp2, l, r + 1 - l);
        Arrays.sort(tmp2, l, mid + 1, cmp3);
        Arrays.sort(tmp2, mid + 1, r + 1, cmp3);
        int p1, p2;
        for (p1 = l - 1, p2 = mid + 1; p2 <= r; ++p2) {
            while (p1 + 1 <= mid && tmp2[p1 + 1].c <= tmp2[p2].c) {
                p1++;
                if (tmp2[p1].left) {
                    update(tmp2[p1].d, dp[tmp2[p1].i]);
                }
            }
            if (!tmp2[p2].left) {
                dp[tmp2[p2].i] = Math.max(dp[tmp2[p2].i], query(tmp2[p2].d) + tmp2[p2].v);
            }
        }
        for (int i = l; i <= p1; i++) {
            if (tmp2[i].left) {
                clear(tmp2[i].d);
            }
        }
        cdq2(mid + 1, r);
    }

    public static void main(String[] args) throws IOException {
        FastReader in = new FastReader(System.in);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
        n = in.nextInt();
        for (int i = 1, a, b, c, d, v; i <= n; ++i) {
            a = in.nextInt();
            b = in.nextInt();
            c = in.nextInt();
            d = in.nextInt();
            v = in.nextInt();
            arr[i] = new Node(a, b, c, d, v);
        }
        for (int i = 1; i <= n; ++i) {
            sortd[i] = arr[i].d; // 树状数组使用的下标的值
        }
        Arrays.sort(sortd, 1, n + 1);
        s = 1;
        for (int i = 2; i <= n; ++i) {
            if (sortd[i] != sortd[s]) {
                sortd[++s] = sortd[i];
            }
        }
        for (int i = 1; i <= n; ++i) {
            arr[i].d = lower(arr[i].d);
        }
        Arrays.sort(arr, 1, n + 1, cmp1);
        int m = 1;
        for (int i = 2; i <= n; ++i) {
            if (arr[m].a == arr[i].a && arr[m].b == arr[i].b && arr[m].c == arr[i].c && arr[m].d == arr[i].d) {
                if (arr[i].v > 0) {
                    arr[m].v += arr[i].v;
                }
            }
            else {
                arr[++m] = arr[i];
            }
        }
        n = m;
        for (int i = 1; i <= n; ++i) {
            arr[i].i = i;
            dp[i] = arr[i].v;
        }
        for (int i = 1; i <= s; ++i) {
            tree[i] = -INF;
        }
        cdq1(1, n);
        long ans = -INF;
        for (int i = 1; i <= n; ++i) {
            ans = Math.max(ans, dp[i]);
        }
        out.println(ans);
        out.flush();
        out.close();
    }

    static class FastReader {
        private final byte[] buffer = new byte[1 << 20];
        private int ptr = 0, len = 0;
        private final InputStream in;

        FastReader(InputStream in) {
            this.in = in;
        }

        private int readByte() throws IOException {
            if (ptr >= len) {
                len = in.read(buffer);
                ptr = 0;
                if (len <= 0)
                    return -1;
            }
            return buffer[ptr++];
        }

        int nextInt() throws IOException {
            int c;
            do {
                c = readByte();
            } while (c <= ' ' && c != -1);
            boolean neg = false;
            if (c == '-') {
                neg = true;
                c = readByte();
            }
            int val = 0;
            while (c > ' ' && c != -1) {
                val = val * 10 + (c - '0');
                c = readByte();
            }
            return neg ? -val : val;
        }
    }
}
