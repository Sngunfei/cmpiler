public class Main {

    public static void main(String[] args) {
        long[] N = new long[]{10, 100, 1000};
        long[] U = new long[]{500000, 1000000, 2000000};
        final long F = 15*1000*1000*1000;
        final long us = 30*1000*1000;
        final long di = 2*1000*1000;
        for(int i=0; i<3; i++){
            long ans = Math.max(N[i]*F/us,F/di);
            System.out.println(ans);
        }
        System.out.println("***************");
        for(int i=0; i<3; i++){
            System.out.println("N=" + N[i]);
            for(int j=0; j<3; j++) {
                long ans = Math.max(F/us, Math.max(F/di, N[i]*F/(us + N[i]*U[j])));
                System.out.println(ans);
            }

        }
    }
}
