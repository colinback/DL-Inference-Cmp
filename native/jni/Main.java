import com.ibm.dlc.*;

public class Main {
    public static void main(String[] args) throws Exception {
        int loop = args.length > 0 ? Integer.parseInt(args[0]) : 1;

        // input
        float[] t1_golden_copy = Reader.Read_float("t1.bin");

        //warm up
        DLC.run(t1_golden_copy);

        long maxtime = Long.MIN_VALUE;
        long mintime = Long.MAX_VALUE;
        long ttime = 0;

        for (int j = 0; j < loop; j++) {
            long stime = System.nanoTime();
            DLC.run(t1_golden_copy);
            long duration = System.nanoTime() - stime;

            ttime += duration;

            if (duration > maxtime)
                maxtime = duration;

            if (duration < mintime)
                mintime = duration;

            // Object[] outputs = DLC.run(t1_golden_copy);
            // Cfloat t2 = (Cfloat)outputs[0];

            // for(int i= 0; i < t2.size(); i++) {
            //     System.out.print(t2.get(i));
            // }
        }

        System.out.println("Rounds:               " + loop);
        System.out.println("Maxinum elpased time: " + maxtime / 1000000.0 + "ms");
        System.out.println("Minimun elpased time: " + mintime / 1000000.0 + "ms");
        System.out.println("Average elpased time: " + ttime / 1000000.0 / loop + "ms");
    }
}
