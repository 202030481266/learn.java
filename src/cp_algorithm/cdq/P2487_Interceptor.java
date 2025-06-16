package cp_algorithm.cdq;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

public class P2487_Interceptor {
    public static int MAXN = 50001;
    public static int n, s;
    public static int[] h = new int[MAXN];
    public static int[] v = new int[MAXN];
    public static int[] sortv = new int[MAXN];

    public static int[][] arr = new int[MAXN][3];

    // fenwick tree
    public static double[] treeCnt = new double[MAXN];
    public static int[] treeVal = new int [MAXN];

    public static int lowbit(int i) {
        return i & -i;
    }

    public static int lower(int val) {
        int l = 0, r = s + 1;
        while (l + 1 < r) {
            int mid = (l + r) / 2;
            if (sortv[mid] >= val) r = mid;
            else l = mid;
        }
        return r;
    }

    // fenwick tree update
    public static void update(int pos, int val, double num) {
        while (pos <= s) {
            if (val > treeVal[pos]) {
                treeVal[pos] = val;
                treeCnt[pos] = num;
            } else if (val == treeVal[pos]) {
                treeCnt[pos] += num;
            }
            pos += lowbit(pos);
        }
    }

    // fenwick tree query
    public static int queryVal = 0;
    public static double queryNum = 0;
    public static void query(int pos) {
        queryNum = 0;
        queryVal = 0;
        while (pos > 0) {
            if (treeVal[pos] > queryVal) {
                queryVal = treeVal[pos];
                queryNum = treeCnt[pos];
            } else if (treeVal[pos] == queryVal) {
                queryNum += treeCnt[pos];
            }
            pos -= lowbit(pos);
        }
    }

    // fenwick tree clear
    public static void clear(int pos) {
        while (pos <= s) {
            treeVal[pos] = 0;
            treeCnt[pos] = 0;
            pos += lowbit(pos);
        }
    }

    // dp - 改为long类型防止溢出
    public static int[] dp1 = new int[MAXN];
    public static double[] cnt1 = new double[MAXN];  // 改为long
    public static int[] dp2 = new int[MAXN];
    public static double[] cnt2 = new double[MAXN];  // 改为long

    public static void cdq1(int l, int r) {
        if (l == r) return;
        int mid = (l + r) / 2;
        cdq1(l, mid);
        for (int i = l; i <= r; ++i) {
            arr[i][0] = i;
            arr[i][1] = h[i];
            arr[i][2] = v[i];
        }
        Arrays.sort(arr, l, mid + 1, (a, b) -> b[1] - a[1]);
        Arrays.sort(arr, mid + 1, r + 1, (a, b) -> b[1] - a[1]);
        int p1, p2;
        for (p1 = l - 1, p2 = mid + 1; p2 <= r; ++p2) {
            while(p1 + 1 <= mid && arr[p1 + 1][1] >= arr[p2][1]) {
                p1++;
                update(s - arr[p1][2] + 1, dp1[arr[p1][0]], cnt1[arr[p1][0]]);
            }
            query(s - arr[p2][2] + 1);
            if (queryVal + 1 > dp1[arr[p2][0]]) {
                dp1[arr[p2][0]] = queryVal + 1;
                cnt1[arr[p2][0]] = queryNum;
            }
            else if (queryVal + 1 == dp1[arr[p2][0]]) {
                cnt1[arr[p2][0]] += queryNum;
            }
        }
        for (int i = l; i <= p1; ++i) clear(s - arr[i][2] + 1);
        cdq1(mid + 1, r);
    }

    public static void cdq2(int l, int r) {
        if (l == r) return;
        int mid = (l + r) / 2;
        cdq2(l, mid);
        for (int i = l; i <= r; ++i) {
            arr[i][0] = i;
            arr[i][1] = h[i];
            arr[i][2] = v[i];
        }
        Arrays.sort(arr, l, mid + 1, Comparator.comparingInt(a -> a[1]));
        Arrays.sort(arr, mid + 1, r + 1, Comparator.comparingInt(a -> a[1]));
        int p1, p2;
        for (p1 = l - 1, p2 = mid + 1; p2 <= r; ++p2) {
            while (p1 + 1 <= mid && arr[p1 + 1][1] <= arr[p2][1]) {
                p1++;
                update(arr[p1][2], dp2[arr[p1][0]], cnt2[arr[p1][0]]);
            }
            query(arr[p2][2]);
            if (queryVal + 1 > dp2[arr[p2][0]]) {
                dp2[arr[p2][0]] = queryVal + 1;
                cnt2[arr[p2][0]] = queryNum;
            }
            else if (queryVal + 1 == dp2[arr[p2][0]]) {
                cnt2[arr[p2][0]] += queryNum;
            }
        }
        for (int i = l; i <= p1; ++i) {
            clear(arr[i][2]);
        }
        cdq2(mid + 1, r);
    }

    public static void main(String[] args) throws IOException {
        FastReader reader = new FastReader(System.in);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));
        n = reader.nextInt();
        for (int i = 1; i <= n; ++i) {
            h[i] = reader.nextInt();
            v[i] = reader.nextInt();
        }
        if (n >= 0) System.arraycopy(v, 1, sortv, 1, n);
        Arrays.sort(sortv, 1, n + 1);
        s = 1;
        for (int i = 2; i <= n; ++i) {
            if (sortv[i] != sortv[s]) {
                sortv[++s] = sortv[i];
            }
        }
        for (int i = 1; i <= n; ++i) {
            v[i] = lower(v[i]);
        }
        for (int i = 1; i <= n; ++i) {
            dp1[i] = 1;
            cnt1[i] = 1;
            dp2[i] = 1;
            cnt2[i] = 1;
        }
        cdq1(1, n);
        for (int l = 1, r = n; l < r; l++, r--) {
            int a = h[l];
            h[l] = h[r];
            h[r] = a;
            int b = v[l];
            v[l] = v[r];
            v[r] = b;
        }
        cdq2(1, n);
        for (int l = 1, r = n; l < r; l++, r--) {
            int a = dp2[l];
            dp2[l] = dp2[r];
            dp2[r] = a;
            double b = cnt2[l];  // 改为long
            cnt2[l] = cnt2[r];
            cnt2[r] = b;
        }
        int len = 0;
        double cnt = 0;  // 改为long
        for (int i = 1; i <= n; ++i) {
            if (dp1[i] > len) {
                len = dp1[i];
                cnt = cnt1[i];
            }
            else if (dp1[i] == len) {
                cnt += cnt1[i];
            }
        }
        pw.println(len);
        for (int i = 1; i <= n; ++i) {
            if (dp1[i] + dp2[i] - 1 < len) {
                pw.print("0 ");
            }
            else {
                // 防止除零和溢出
                if (cnt == 0) {
                    pw.print("0 ");
                } else {
                    // 使用double计算避免整数溢出
                    double result = (cnt1[i] * cnt2[i]) / cnt;
                    pw.printf("%.5f ", result);
                }
            }
        }
        pw.println();
        pw.flush();
    }

    static class FastReader {
        private final byte[] buffer = new byte[1 << 20];
        private int pos = 0;
        private int len = 0;
        private final InputStream in;

        FastReader(InputStream in) {
            this.in = in;
        }

        private int readByte() throws IOException {
            if (pos >= len) {
                len = in.read(buffer);
                pos = 0;
                if (len <= 0) {
                    return -1;
                }
            }
            return buffer[pos++];
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
            while (c > ' ') {
                val = 10 * val + (c - '0');
                c = readByte();
            }
            return neg ? -val : val;
        }
    }
}