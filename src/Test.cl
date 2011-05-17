__kernel void myKernel(__global const float* input, __global float* output, float multFactor) {
   int i = get_global_id(0);
   for (int j = 0; j < 10000; ++j) {
	   output[i] += (input[i] * multFactor * input[i] / 2);
}
}