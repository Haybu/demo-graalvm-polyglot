package io.agilehandy.demo;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Language;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Component
public class PythonInterpolation implements ApplicationRunner {

    Logger log = LoggerFactory.getLogger(PythonInterpolation.class);

    private final static String LANGUAGE = "paython";
    private final static String file = "interpolator.py";

    @Override
    public void run(ApplicationArguments args) throws Exception {

        generateTimeSeries(10)
                //.transform(f -> runningSummary().apply(f, new Window(10,10)))
                .doOnNext(t -> log.info(t.toString()))
                .subscribe();
    }

    public BiFunction<Flux<TimeSeries>, Window, Flux<TimeSeries>> runningSummary() {
        return (flux, windowing) -> {
            if (windowing.getType() == WindowType.COUNT) {
                return flux.window(windowing.getWindow(), windowing.getSlide()).flatMap(f -> aggregate().apply(f));
            } else {
                Duration windowDuration = Duration.ofSeconds(windowing.getWindow());
                Duration slideDuration = Duration.ofSeconds(windowing.getSlide());
                return flux.window(windowDuration, slideDuration).flatMap(f -> aggregate().apply(f));
            }
        };
    }

    public Function<Flux<TimeSeries>, Flux<TimeSeries>> aggregate() {
        return chunk ->
            chunk.groupBy(t -> t.getKey())
                    .flatMap(group -> groupSummary().apply(group));

    }

    public Function<GroupedFlux<String, TimeSeries>,Mono<TimeSeries>> groupSummary() {
        return group -> {
            final TimeSeries result = new TimeSeries();
            return group.doOnNext(g -> {
                        result.setTime(g.getTime());
                        result.setKey(g.getKey());
                })
                    .map(t -> t.getValue())
                .reduce(0d, (t1, t2) -> t1 + t2)
                    .map(sum -> {
                        result.setValue(sum);
                        return result;
                    })
                    ;
        };
    }

    public Flux<TimeSeries> interpolate() throws Exception {
        Context context = Context.newBuilder(LANGUAGE)
                .allowAllAccess(true)
                //.allowExperimentalOptions(true)
                //.allowIO(true)
                //.allowNativeAccess(true)
                //.allowPolyglotAccess(PolyglotAccess.ALL)
                //.allowCreateProcess(true)
                //.allowHostClassLookup(name -> true)
                ////.option("python.Executable", "ENV_EXECUTABLE")
                ////.option("python.ForceImportSite", "true")
                .build();

        List<TimeSeries> timeSeries = new ArrayList<>();

        // to pass something to python side
        //Value pythonBindings = context.getBindings(language);
        //pythonBindings.putMember("foo", "Haytham Mohamed");

        Resource fileResource = new ClassPathResource(file);
        InputStream inputStream = fileResource.getInputStream();
        InputStreamReader code = new InputStreamReader(inputStream);
        Source source = Source.newBuilder(LANGUAGE, code, file).build();

        context.eval(source);

        // to get out of python
        // obtain python class
        Value interpolatorClass = context.getBindings(LANGUAGE).getMember("Interpolator"); //
        // create instance from the python class
        Value interpolatorInstance = interpolatorClass.newInstance();
        // cast to Java interface that exposes the methods
        Interpolator interpolator = interpolatorInstance.as(Interpolator.class);
        // call the method. The method will be routed to python method counterpart
        int sum = interpolator.sum(1, 5, 9);
        System.out.println("sum is " + sum);

        context.close();
        return Flux.empty();
    }

    public Flux<TimeSeries> generateTimeSeries(int count) {
        Supplier<Double> valueSupplier = () -> new Double((new Random()).nextInt(count));
        return Flux.fromStream(IntStream.range(1,count).boxed())
                .filter(randomPick())
                .map(d -> {
            LocalDateTime t = Instant.now().plus(d, ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).toLocalDateTime();
            return new TimeSeries(t, valueSupplier.get(), this.generateKey().get());
        })
                ;
    }

    public Supplier<String> generateKey() {
        return () -> {
            List<String> givenList = Arrays.asList("11111", "22222", "33333");
            Random rand = new Random();
            return givenList.get(rand.nextInt(givenList.size()));
        };
    }

    public Predicate<Integer> randomPick() {
        return i -> {
            List<Boolean> givenList = Arrays.asList(true, false, true, true, false, true, true, true);
            Random rand = new Random();
            return givenList.get(rand.nextInt(givenList.size()));
        } ;
    }

    class Window {
        int window;
        int slide;
        WindowType type;

        public Window(int window, int slide) {
            this.window = window;
            this.slide = slide;
        }

        public int getWindow() {
            return window;
        }

        public void setWindow(int window) {
            this.window = window;
        }

        public int getSlide() {
            return slide;
        }

        public void setSlide(int slide) {
            this.slide = slide;
        }

        public WindowType getType() {
            return type;
        }

        public void setType(WindowType type) {
            this.type = type;
        }
    }

    enum WindowType {
        COUNT, DURATION;
    }

}
