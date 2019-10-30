package tmp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class FileChannelTest {

  private static final Path path = Paths.get("D:\\tmp.pdf");
  private FileChannel fileChannel;
  private ByteBuffer buffer1_1;
  private ByteBuffer buffer1_2;
  private ByteBuffer buffer2;
  private ByteBuffer buffer3;


  @Setup(Level.Invocation)
  public void prepare() throws IOException {
    fileChannel = FileChannel.open(path, StandardOpenOption.READ);

    buffer1_1 = ByteBuffer.allocate(1000 * 1000);
    buffer1_2 = ByteBuffer.allocate(2000 * 1000);

    buffer2 = ByteBuffer.allocate(4000 * 1000);

    buffer3 = ByteBuffer.allocate(3000 * 1000);
  }

  @TearDown(Level.Invocation)
  public void close() throws IOException {
    buffer1_1.clear();
    buffer1_2.clear();
    buffer2.clear();
    fileChannel.close();
  }

  @Benchmark
  public void readTwice(Blackhole bh) throws IOException {
    fileChannel.read(buffer1_1, 1000000);
    fileChannel.read(buffer1_2, 3000000);
  }

  @Benchmark
  public void readOnce(Blackhole bh) throws IOException {
    fileChannel.read(buffer2, 1000000);
  }

  @Benchmark
  public void readOnceEqualSize(Blackhole bh) throws IOException {
    fileChannel.read(buffer3, 1000000);
  }


  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(FileChannelTest.class.getSimpleName())
        .forks(1)
        .warmupIterations(5)
        .measurementIterations(10)
        .build();

    new Runner(opt).run();
  }

}