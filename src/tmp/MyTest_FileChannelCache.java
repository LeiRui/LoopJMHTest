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
public class MyTest_FileChannelCache {

  private static final Path path = Paths.get("D:\\1571921649891-101.tsfile");
  private static final Path path2 = Paths.get("D:\\1571921649891-101-2.tsfile");
  private FileChannel fileChannel;
  private FileChannel fileChannel2;
  private ByteBuffer buffer;
  private ByteBuffer buffer2;
  private ByteBuffer buffer2_PreRead;


  @Setup(Level.Invocation)
  public void prepare() throws IOException {
    fileChannel = FileChannel.open(path, StandardOpenOption.READ);
    buffer = ByteBuffer.allocate(28);

    fileChannel2 = FileChannel.open(path2, StandardOpenOption.READ);
    buffer2_PreRead = ByteBuffer.allocate(28);
    fileChannel2.read(buffer2_PreRead, 13);
    buffer2_PreRead.flip();
    buffer2 = ByteBuffer.allocate(55677);
  }

  @TearDown(Level.Invocation)
  public void close() throws IOException {
    buffer.clear();
    buffer2.clear();
    buffer2_PreRead.clear();
    fileChannel.close();
    fileChannel2.close();
  }

  @Benchmark
  public void mimicReadChunkHeaderFirst(Blackhole bh) throws IOException {
    fileChannel.read(buffer, 13);
    buffer.flip();
    bh.consume(buffer);
  }

  @Benchmark
  public void mimicReadChunkSecond(Blackhole bh) throws IOException {
    fileChannel2.read(buffer2, 41);
    buffer2.flip();
    bh.consume(buffer2);
  }


  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(MyTest_FileChannelCache.class.getSimpleName())
        .forks(1)
        .warmupIterations(5)
        .measurementIterations(10)
        .build();

    new Runner(opt).run();
  }

}