#include "common.cpp"
#include <limits.h>

extern "C" {
void prepare(void *env);
void **run(float (*t1)[8][6][2]);
void finalize(void *env);
}

using namespace std;

int main(int argc, char* argv[]) {
    int loop = argc > 1 ? atoi(argv[1]) : 1;

    // load input
    auto t1_golden_copy = load_test_tensor<float>("t1.bin", 8 * 6 * 2);

    prepare(NULL);

    // warn up
    run(vec_to_vla_ptr<float(*)[8][6][2]>(t1_golden_copy));

    long maxtime = LONG_MIN;
    long mintime = LONG_MAX;
    long ttime = 0;
    
    for (int j = 0; j < loop; j++) {
        // start time
        clock_t stime = clock();
        run(vec_to_vla_ptr<float(*)[8][6][2]>(t1_golden_copy));
        long duration = clock() - stime;

        ttime += duration;

        if (duration > maxtime)
            maxtime = duration;

        if (duration < mintime)
            mintime = duration;

        // void **outputs = (run(vec_to_vla_ptr<float(*)[8][6][2]>(t1_golden_copy)));
        // float *t2 = (float *)outputs[0];
        // vector<float> t2_vec = vector<float>(t2, t2 + 64 * 24 * 6);

        // for (int i = 0; i < t2_vec.size(); i++)
        //     std::cout << t2_vec[i] << " ";
    }

    std::cout << "Rounds:               " << loop << std::endl;
    std::cout << "Maxinum elpased time: " << (double)maxtime / CLOCKS_PER_SEC * 1000000
              << " us." << std::endl;
    std::cout << "Mininum elpased time: " << (double)mintime / CLOCKS_PER_SEC * 1000000
              << " us." << std::endl;
    std::cout << "Average elpased time: " << (double)ttime / CLOCKS_PER_SEC * 1000000 / loop
              << " us." << std::endl;


    finalize(NULL);

    return 0;
}
