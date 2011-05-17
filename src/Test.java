import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.apache.commons.io.FileUtils;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLMem;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;


public class Test {
	static int dataSize = 102475;
	public static void main(String[] args) throws Exception {
		// System.out.println(Integer.toBinaryString(31) + " => " + Integer.toBinaryString(31 >>> 35));
		long time = 0;
		
		CLContext context = JavaCL.createBestContext();
		CLQueue queue = context.createDefaultQueue();

		ByteOrder order = context.getByteOrder();
		FloatBuffer buffer = ByteBuffer.allocateDirect(dataSize * 4).order(order).asFloatBuffer();
		
		for (int i = 0; i < dataSize; ++i)
			buffer.put((float)i);
		
		CLBuffer<FloatBuffer> input = context.createFloatBuffer(CLMem.Usage.Input, buffer, true);
		CLBuffer<FloatBuffer> output = context.createFloatBuffer(CLMem.Usage.Output, dataSize);
		float multFactor = 0.5f;
		
		String sources = FileUtils.readFileToString(new File("src/Test.cl"));
		
		CLProgram program = context.createProgram(sources).build();
		CLKernel kernel = program.createKernel("myKernel");
		time = -System.nanoTime();
		CLEvent kernelCompletion;
		// The same kernel can be safely used by different threads, as long as setArgs + enqueueNDRange are in a synchronized block
		synchronized (kernel) {
		    // setArgs will throw an exception at runtime if the types / sizes of the arguments are incorrect
		    kernel.setArgs(input, output, multFactor);

		   // Ask for 1-dimensional execution of length dataSize, with auto choice of local workgroup size :
		    kernelCompletion = kernel.enqueueNDRange(queue, new int[] { dataSize });
		}
		kernelCompletion.waitFor(); // better not to wait for it but to pass it as a dependent event to some other queuable operation (CLBuffer.read, for instance)
		time += System.nanoTime();
		
		System.out.printf("Number of seconds: %f\n", (double)time/1000000000);
		/*FloatBuffer outbuffer = output.read(queue);
		for (int i = 0; i < dataSize; i += 1024) {
			System.out.printf("%3d:%f\n", i, outbuffer.get(i));
		}*/
		System.out.println("LOL");
	}
}
